package io.github.leawind.thirdperson.internal.core.config;

import java.util.Objects;

/// Decides whether this perspective should replace vanilla's first-person-only reticle gate.
public final class ReticlePolicy {
  private ReticlePolicy() {}

  public static boolean shouldRender(
      boolean vanillaDecision,
      boolean thirdPersonCurrent,
      boolean cameraControlEnabled,
      ReticleMode mode) {
    Objects.requireNonNull(mode, "mode");
    if (!thirdPersonCurrent || !cameraControlEnabled) {
      return vanillaDecision;
    }
    return mode != ReticleMode.OFF;
  }
}
