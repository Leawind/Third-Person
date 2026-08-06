package io.github.leawind.thirdperson.internal.bridge;

import io.github.leawind.thirdperson.internal.bridge.compat.sable.SableCompatibility;
import java.util.Optional;
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

/// Version-sensitive vanilla raycasting, block clipping, and hit adaptation.
public final class MinecraftRaycasting {
  private static final double MAX_FALLBACK_RAY_LENGTH = 4096.0;
  private static final double MAX_FALLBACK_RAY_LENGTH_SQUARED =
      MAX_FALLBACK_RAY_LENGTH * MAX_FALLBACK_RAY_LENGTH;

  private MinecraftRaycasting() {}

  public static Optional<Vector3d> clipVisualBlocks(
      Entity entity, Vector3dc from, Vector3dc to) {
    HitResult hit =
        entity
            .level()
            .clip(
                new ClipContext(
                    toVec3(from),
                    toVec3(to),
                    ClipContext.Block.VISUAL,
                    ClipContext.Fluid.NONE,
                    entity));
    return hit.getType() == HitResult.Type.MISS
        ? Optional.empty()
        : Optional.of(
            toVector3d(SableCompatibility.projectToWorld(entity.level(), hit.getLocation())));
  }

  public static BlockHit clipBlocks(
      Entity entity, Vec3 from, Vec3 to, boolean useColliderBlocks) {
    Vec3 effectiveTo = limitRayEnd(from, to);
    HitResult hit =
        entity
            .level()
            .clip(
                new ClipContext(
                    from,
                    effectiveTo,
                    useColliderBlocks
                        ? ClipContext.Block.COLLIDER
                        : ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.NONE,
                    entity));
    boolean blocked = hit.getType() == HitResult.Type.BLOCK;
    Vec3 worldLocation =
        blocked
            ? SableCompatibility.projectToWorld(entity.level(), hit.getLocation())
            : hit.getLocation();
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
        : Optional.of(SableCompatibility.projectToWorld(source.level(), hit.getLocation()));
  }

  /// Runs vanilla-style block/entity candidate selection along an arbitrary world-space ray.
  ///
  /// The raw hit remains in the coordinate space expected by vanilla/Sable interaction code;
  /// [SpatialHit#worldLocation()] is the corresponding point for distance calculations.
  ///
  /// The caller is responsible for validating the selected hit against the player's actual
  /// interaction ranges.
  public static SpatialHit pickFrom(
      Entity source, Vec3 from, Vec3 direction, double candidateRange) {
    Vec3 rayEnd = from.add(direction.scale(candidateRange));
    HitResult blockHit =
        source
            .level()
            .clip(
                new ClipContext(
                    from,
                    rayEnd,
                    ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.NONE,
                    source));
    Vec3 blockWorldLocation =
        blockHit.getType() == HitResult.Type.BLOCK
            ? SableCompatibility.projectToWorld(source.level(), blockHit.getLocation())
            : blockHit.getLocation();
    double blockDistanceSquared = blockWorldLocation.distanceToSqr(from);
    Vec3 entityRayEnd =
        blockHit.getType() == HitResult.Type.MISS ? rayEnd : blockWorldLocation;
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
    Vec3 entityWorldLocation =
        SableCompatibility.projectToWorld(source.level(), entityHit.getLocation());
    return entityWorldLocation.distanceToSqr(from) < blockDistanceSquared
        ? new SpatialHit(entityHit, entityWorldLocation)
        : new SpatialHit(blockHit, blockWorldLocation);
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

  private static Vec3 toVec3(Vector3dc value) {
    return new Vec3(value.x(), value.y(), value.z());
  }

  private static Vector3d toVector3d(Vec3 value) {
    return new Vector3d(value.x, value.y, value.z);
  }

  public record BlockHit(Vec3 worldLocation, boolean blocked, boolean missed) {}

  /// A raw vanilla/Sable hit paired with its world-space position for spatial calculations.
  public record SpatialHit(HitResult rawHit, Vec3 worldLocation) {}
}
