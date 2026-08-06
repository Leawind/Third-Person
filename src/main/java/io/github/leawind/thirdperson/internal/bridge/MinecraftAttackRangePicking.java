package io.github.leawind.thirdperson.internal.bridge;

import io.github.leawind.thirdperson.internal.bridge.compat.sable.SableCompatibility;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.player.LocalPlayer;
/*? if >=1.21.11 {*/
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.component.AttackRange;
/*? }*/
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/// Version-sensitive item attack-range operations used by interaction logic.
public final class MinecraftAttackRangePicking {
  private MinecraftAttackRangePicking() {}

  /// Reads the active item's effective vanilla attack-range parameters.
  public static Optional<Parameters> parameters(LocalPlayer player, Vec3 direction) {
    /*? if >=1.21.11 {*/
    AttackRange attackRange = player.getActiveItem().get(DataComponents.ATTACK_RANGE);
    if (attackRange == null) {
      return Optional.empty();
    }
    return Optional.of(
        new Parameters(
            attackRange.effectiveMinRange(player),
            attackRange.effectiveMaxRange(player),
            attackRange.hitboxMargin(),
            Math.max(0.0, player.getKnownMovement().dot(direction))));
    /*? } else {*/
    /*return Optional.empty();
    *//*? }*/
  }

  /// Collects vanilla-style block and entity candidates along an arbitrary attack ray.
  public static Candidates collectCandidates(
      LocalPlayer player,
      Vec3 from,
      Vec3 direction,
      double minimumCandidateDistance,
      double candidateRange,
      double hitboxMargin) {
    /*? if >=1.21.11 {*/
    Vec3 entityRayStart = from.add(direction.scale(minimumCandidateDistance));
    Vec3 rayEnd = from.add(direction.scale(candidateRange));
    BlockHitResult blockHit =
        player
            .level()
            .clipIncludingBorder(
                new ClipContext(
                    from,
                    rayEnd,
                    ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.NONE,
                    player));
    Vec3 blockWorldLocation =
        blockHit.getType() == HitResult.Type.BLOCK
            ? SableCompatibility.projectToWorld(player.level(), blockHit.getLocation())
            : blockHit.getLocation();
    Vec3 entityRayEnd =
        blockHit.getType() == HitResult.Type.MISS ? rayEnd : blockHit.getLocation();
    if (blockHit.getType() != HitResult.Type.MISS
        && blockHit.getLocation().distanceToSqr(from) < entityRayStart.distanceToSqr(from)) {
      return new Candidates(
          new MinecraftRaycasting.SpatialHit(blockHit, blockWorldLocation), List.of());
    }
    AABB searchArea =
        AABB.ofSize(entityRayStart, hitboxMargin, hitboxMargin, hitboxMargin)
            .expandTowards(entityRayEnd.subtract(entityRayStart))
            .inflate(1.0);
    List<MinecraftRaycasting.SpatialHit> entityHits = new ArrayList<>();
    for (EntityHitResult candidate :
        ProjectileUtil.getManyEntityHitResult(
            player.level(),
            player,
            entityRayStart,
            entityRayEnd,
            searchArea,
            EntitySelector.CAN_BE_PICKED,
            (float) hitboxMargin,
            ClipContext.Block.OUTLINE,
            true)) {
      Vec3 worldLocation =
          SableCompatibility.projectToWorld(player.level(), candidate.getLocation());
      entityHits.add(new MinecraftRaycasting.SpatialHit(candidate, worldLocation));
    }
    return new Candidates(
        new MinecraftRaycasting.SpatialHit(blockHit, blockWorldLocation), List.copyOf(entityHits));
    /*? } else {*/
    /*return new Candidates(
        new MinecraftRaycasting.SpatialHit(MinecraftRaycasting.missAt(from, from), from), List.of());
    *//*? }*/
  }

  public record Parameters(
      double minimumRange,
      double maximumRange,
      double hitboxMargin,
      double forwardMovement) {}

  public record Candidates(
      MinecraftRaycasting.SpatialHit blockHit, List<MinecraftRaycasting.SpatialHit> entityHits) {}
}
