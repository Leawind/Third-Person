package io.github.leawind.thirdperson.internal.core.schedule.hud;

import java.util.Objects;

/// Decides whether this perspective should replace vanilla's first-person-only crosshair gate.
public final class CrosshairPolicy {
  private CrosshairPolicy() {}

  public static boolean shouldRender(
      boolean vanillaDecision,
      boolean thirdPersonActive,
      boolean aiming,
      boolean fallFlying,
      CrosshairMode mode,
      boolean hideWhenFallFlyingAndNotAiming) {
    Objects.requireNonNull(mode, "mode");
    if (!thirdPersonActive) {
      return vanillaDecision;
    }
    boolean enabledForMode =
        switch (mode) {
          case ALWAYS -> true;
          case AIMING -> aiming;
          case NOT_AIMING -> !aiming;
          case OFF -> false;
        };
    if (!enabledForMode) {
      return false;
    }
    return aiming || !fallFlying || !hideWhenFallFlyingAndNotAiming;
  }
}
