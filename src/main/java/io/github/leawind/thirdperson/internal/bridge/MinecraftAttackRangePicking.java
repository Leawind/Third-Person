package io.github.leawind.thirdperson.internal.bridge;

import java.util.Optional;
import net.minecraft.client.player.LocalPlayer;
/*? if >=1.21.11 {*/
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.AttackRange;
/*? }*/
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

  public record Parameters(
      double minimumRange,
      double maximumRange,
      double hitboxMargin,
      double forwardMovement) {}
}
