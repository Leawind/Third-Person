package io.github.leawind.thirdperson.internal.bridge;

import io.github.leawind.thirdperson.internal.extension.spatial.SpatialQueryHitLocation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Version-sensitive vanilla raycasting, block clipping, and spatial queries.
///
/// This class is the single entry point for Minecraft spatial queries, including:
///
/// - ray clipping
/// - entity picking
/// - region entity/block traversal
public final class MinecraftSpatialQuerying {
  private static final double MAX_FALLBACK_RAY_LENGTH = 768.0;
  private static final double MAX_FALLBACK_RAY_LENGTH_SQUARED =
      MAX_FALLBACK_RAY_LENGTH * MAX_FALLBACK_RAY_LENGTH;

  private MinecraftSpatialQuerying() {}

  public static Optional<Vector3d> clipVisualBlocks(Entity entity, Vector3dc from, Vector3dc to) {
    Vec3 effectiveFrom = toVec3(from);
    Vec3 effectiveTo = limitRayEnd(effectiveFrom, toVec3(to));
    HitResult hit =
        entity
            .level()
            .clip(
                new ClipContext(
                    effectiveFrom,
                    effectiveTo,
                    ClipContext.Block.VISUAL,
                    ClipContext.Fluid.NONE,
                    entity));
    return hit.getType() == HitResult.Type.MISS
        ? Optional.empty()
        : Optional.of(toVector3d(SpatialQueryHitLocation.resolve(entity.level(), hit)));
  }

  public static BlockHit clipBlocks(Entity entity, Vec3 from, Vec3 to, boolean useColliderBlocks) {
    Vec3 effectiveTo = limitRayEnd(from, to);
    HitResult hit =
        entity
            .level()
            .clip(
                new ClipContext(
                    from,
                    effectiveTo,
                    useColliderBlocks ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.NONE,
                    entity));
    boolean blocked = hit.getType() == HitResult.Type.BLOCK;
    Vec3 worldLocation = SpatialQueryHitLocation.resolve(entity.level(), hit);
    return new BlockHit(worldLocation, blocked, hit.getType() == HitResult.Type.MISS);
  }

  public static Optional<Vec3> pickEntity(
      Entity source, Vec3 from, Vec3 to, double maxDistanceSquared) {
    Vec3 effectiveTo = limitRayEnd(from, to);
    double effectiveMaxDistanceSquared =
        Math.min(maxDistanceSquared, from.distanceToSqr(effectiveTo));
    if (!Double.isFinite(effectiveMaxDistanceSquared) || effectiveMaxDistanceSquared < 0.0) {
      return Optional.empty();
    }
    EntityHitResult hit =
        ProjectileUtil.getEntityHitResult(
            source,
            from,
            effectiveTo,
            new AABB(from, effectiveTo),
            entity -> !entity.isSpectator() && entity.isPickable(),
            effectiveMaxDistanceSquared);
    return hit == null
        ? Optional.empty()
        : Optional.of(SpatialQueryHitLocation.resolve(source.level(), hit));
  }

  /// Runs vanilla-style block/entity candidate selection along an arbitrary world-space ray.
  ///
  /// The raw hit remains in the coordinate space expected by Minecraft interaction code;
  /// [SpatialHit#worldLocation()] is the corresponding point for distance calculations.
  ///
  /// The caller is responsible for validating the selected hit against the player's actual
  /// interaction ranges.
  public static SpatialHit pickFrom(
      Entity source, Vec3 from, Vec3 direction, double candidateRange) {
    Vec3 rayEnd = limitRayEnd(from, from.add(direction.scale(candidateRange)));
    HitResult blockHit =
        source
            .level()
            .clip(
                new ClipContext(
                    from, rayEnd, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, source));
    Vec3 blockWorldLocation = SpatialQueryHitLocation.resolve(source.level(), blockHit);
    double blockDistanceSquared = blockWorldLocation.distanceToSqr(from);
    Vec3 entityRayEnd = blockHit.getType() == HitResult.Type.MISS ? rayEnd : blockWorldLocation;
    entityRayEnd = limitRayEnd(from, entityRayEnd);
    EntityHitResult entityHit =
        ProjectileUtil.getEntityHitResult(
            source,
            from,
            entityRayEnd,
            new AABB(from, entityRayEnd).inflate(1.0, 1.0, 1.0),
            entity -> !entity.isSpectator() && entity.isPickable(),
            blockDistanceSquared);
    if (entityHit == null) {
      return new SpatialHit(blockHit, blockWorldLocation);
    }
    Vec3 entityWorldLocation = SpatialQueryHitLocation.resolve(source.level(), entityHit);
    return entityWorldLocation.distanceToSqr(from) < blockDistanceSquared
        ? new SpatialHit(entityHit, entityWorldLocation)
        : new SpatialHit(blockHit, blockWorldLocation);
  }

  /// Collects vanilla-style block and entity candidates along an arbitrary attack ray.
  ///
  /// The caller is responsible for validating the candidates against the player's actual
  /// interaction ranges.
  public static RaycastCandidates collectCandidates(
      LocalPlayer player,
      Vec3 from,
      Vec3 direction,
      double minimumCandidateDistance,
      double candidateRange,
      double hitboxMargin) {
    /*? if >=1.21.11 {*/
    Vec3 entityRayStart = limitRayEnd(from, from.add(direction.scale(minimumCandidateDistance)));
    Vec3 rayEnd = limitRayEnd(from, from.add(direction.scale(candidateRange)));
    BlockHitResult blockHit =
        player
            .level()
            .clipIncludingBorder(
                new ClipContext(
                    from, rayEnd, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    Vec3 blockWorldLocation = SpatialQueryHitLocation.resolve(player.level(), blockHit);
    Vec3 entityRayEnd =
        limitRayEnd(
            from, blockHit.getType() == HitResult.Type.MISS ? rayEnd : blockHit.getLocation());
    if (blockHit.getType() != HitResult.Type.MISS
        && blockHit.getLocation().distanceToSqr(from) < entityRayStart.distanceToSqr(from)) {
      return new RaycastCandidates(new SpatialHit(blockHit, blockWorldLocation), List.of());
    }
    AABB searchArea =
        AABB.ofSize(entityRayStart, hitboxMargin, hitboxMargin, hitboxMargin)
            .expandTowards(entityRayEnd.subtract(entityRayStart))
            .inflate(1.0);
    List<SpatialHit> entityHits = new ArrayList<>();
    for (EntityHitResult candidate :
        ProjectileUtil.getManyEntityHitResult(
            player.level(),
            player,
            entityRayStart,
            entityRayEnd,
            searchArea,
            net.minecraft.world.entity.EntitySelector.CAN_BE_PICKED,
            (float) hitboxMargin,
            ClipContext.Block.OUTLINE,
            true)) {
      Vec3 worldLocation = SpatialQueryHitLocation.resolve(player.level(), candidate);
      entityHits.add(new SpatialHit(candidate, worldLocation));
    }
    return new RaycastCandidates(
        new SpatialHit(blockHit, blockWorldLocation), List.copyOf(entityHits));
    /*? } else {*/
    /*return new RaycastCandidates(new SpatialHit(missAt(from, from), from), List.of());
     *//*? }*/
  }

  /// Lists entities intersecting the source's bounding box inflated by the given amount, reusing
  /// vanilla's own region query.
  ///
  /// The inflation is capped to keep the query AABB bounded.
  ///
  /// The source itself is excluded, matching the underlying Level#getEntities semantics.
  public static List<Entity> entitiesInInflatedBounds(
      Entity source, double boundsInflation, Predicate<Entity> predicate) {
    double cappedInflation = limitInflation(boundsInflation);
    AABB searchArea = source.getBoundingBox().inflate(cappedInflation);
    return source.level().getEntities(source, searchArea, predicate);
  }

  public static HitResult missAt(Vec3 location, Vec3 origin) {
    Vec3 offset = location.subtract(origin);
    /*? if >=1.21.11 {*/
    Direction direction = Direction.getApproximateNearest(offset.x, offset.y, offset.z);
    /*? } else {*/
    /*Direction direction = Direction.getNearest(offset.x, offset.y, offset.z);
     *//*? }*/
    return BlockHitResult.miss(location, direction, BlockPos.containing(location));
  }

  /// Bounds bridge-level ray work even if an upstream caller fails to apply its stricter policy.
  ///
  /// @see ProjectileUtil#getEntityHitResult
  /// @see net.minecraft.world.level.BlockGetter#clip
  static Vec3 limitRayEnd(Vec3 from, Vec3 to) {
    Vec3 delta = to.subtract(from);
    double lengthSquared = delta.lengthSqr();
    if (!Double.isFinite(lengthSquared)) {
      return from;
    }
    if (lengthSquared <= MAX_FALLBACK_RAY_LENGTH_SQUARED) {
      return to;
    }
    return from.add(delta.scale(MAX_FALLBACK_RAY_LENGTH / Math.sqrt(lengthSquared)));
  }

  /// Bounds the inflation used to build a region-query AABB even if an upstream caller passes an
  /// unbounded value, keeping the query area finite.
  static double limitInflation(double boundsInflation) {
    if (!Double.isFinite(boundsInflation)) {
      return MAX_FALLBACK_RAY_LENGTH;
    }
    return Math.min(boundsInflation, MAX_FALLBACK_RAY_LENGTH);
  }

  private static Vec3 toVec3(Vector3dc value) {
    return new Vec3(value.x(), value.y(), value.z());
  }

  private static Vector3d toVector3d(Vec3 value) {
    return new Vector3d(value.x, value.y, value.z);
  }

  public record BlockHit(Vec3 worldLocation, boolean blocked, boolean missed) {}

  /// A raw query hit paired with its world-space position for spatial calculations.
  public record SpatialHit(HitResult rawHit, Vec3 worldLocation) {}

  /// Block and entity candidates collected along a single attack ray.
  public record RaycastCandidates(SpatialHit blockHit, List<SpatialHit> entityHits) {}
}
