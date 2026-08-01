package io.github.leawind.thirdperson.internal.logic.scheduler.hud;

import java.util.Objects;

/// Decides whether this perspective should replace vanilla's first-person-only crosshair gate.
public final class CrosshairPolicy {
  private CrosshairPolicy() {}

  public static boolean shouldRender(
      boolean vanillaDecision,
      boolean thirdPersonCurrent,
      boolean cameraControlEnabled,
      CrosshairMode mode) {
    Objects.requireNonNull(mode, "mode");
    if (!thirdPersonCurrent || !cameraControlEnabled) {
      return vanillaDecision;
    }
    return mode != CrosshairMode.OFF;
  }
}
