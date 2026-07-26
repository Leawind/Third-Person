package io.github.leawind.thirdperson.internal.integration.minecraft;

import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.core.config.ReticlePolicy;
import io.github.leawind.thirdperson.internal.integration.perspective.PerspectiveGuard;

public final class MinecraftHudIntegration {
  private MinecraftHudIntegration() {}

  public static boolean shouldRenderReticle(boolean vanillaDecision) {
    ThirdPersonRuntime runtime = ThirdPersonRuntime.getInstance();
    boolean current = PerspectiveGuard.isThirdPersonCurrentForLocalPlayer();
    return ReticlePolicy.shouldRender(
        vanillaDecision,
        current,
        runtime.isCameraControlEnabled(),
        runtime.config().hud().reticle());
  }
}
