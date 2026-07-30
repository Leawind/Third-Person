package io.github.leawind.thirdperson.internal.scheduler.integration.minecraft;

import io.github.leawind.thirdperson.internal.scheduler.SchedulerRuntime;
import io.github.leawind.thirdperson.internal.bridge.events.ReticleGateEvent;
import io.github.leawind.thirdperson.internal.scheduler.hud.ReticlePolicy;

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
    SchedulerRuntime runtime = SchedulerRuntime.getInstance();
    boolean current = runtime.base().isCameraControlEnabled();
    return ReticlePolicy.shouldRender(
        vanillaDecision,
        current,
        runtime.base().isCameraControlEnabled(),
        runtime.hudSettings().reticleMode());
  }
}
