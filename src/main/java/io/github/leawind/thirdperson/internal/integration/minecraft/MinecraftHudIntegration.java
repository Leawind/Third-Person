package io.github.leawind.thirdperson.internal.integration.minecraft;

import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.bridge.events.ReticleGateEvent;
import io.github.leawind.thirdperson.internal.core.hud.ReticlePolicy;
import io.github.leawind.thirdperson.internal.integration.perspective.PerspectiveGuard;

public final class MinecraftHudIntegration {
  private static boolean registered;

  private MinecraftHudIntegration() {}

  public static void register() {
    if (registered) {
      return;
    }
    registered = true;
    ReticleGateEvent.register(MinecraftHudIntegration::shouldRenderReticle);
  }

  public static boolean shouldRenderReticle(boolean vanillaDecision) {
    ThirdPersonRuntime runtime = ThirdPersonRuntime.getInstance();
    boolean current = PerspectiveGuard.isThirdPersonCurrent();
    return ReticlePolicy.shouldRender(
        vanillaDecision,
        current,
        runtime.isCameraControlEnabled(),
        runtime.hudSettings().reticleMode());
  }
}
