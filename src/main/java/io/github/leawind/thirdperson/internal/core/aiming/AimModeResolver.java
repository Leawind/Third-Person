package io.github.leawind.thirdperson.internal.core.aiming;

import java.util.Objects;

/// Resolves manual and conservative vanilla-use signals into one aiming intent.
public final class AimModeResolver {
  private AimModeResolver() {}

  public static boolean shouldAim(
      boolean manualAiming,
      boolean smartAimingEnabled,
      boolean usingItem,
      AimUseAnimation useAnimation) {
    return shouldAim(
        manualAiming, smartAimingEnabled, usingItem, useAnimation, null);
  }

  public static boolean shouldAim(
      boolean manualAiming,
      boolean smartAimingEnabled,
      boolean usingItem,
      AimUseAnimation useAnimation,
      AimRuleAction resourceAction) {
    Objects.requireNonNull(useAnimation, "useAnimation");
    if (manualAiming) {
      return true;
    }
    if (!smartAimingEnabled) {
      return false;
    }
    if (resourceAction == AimRuleAction.AIM_WHILE_HOLDING
        || resourceAction == AimRuleAction.AIM_WHILE_USING) {
      return true;
    }
    if (!usingItem) {
      return false;
    }
    return switch (useAnimation) {
      case BOW, CROSSBOW, SPEAR -> true;
      case NONE, OTHER -> false;
    };
  }
}
