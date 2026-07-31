package io.github.leawind.thirdperson.internal.logic.scheduler;

import io.github.leawind.thirdperson.internal.logic.scheduler.hud.ReticlePolicy;
public final class MinecraftHudIntegration {
  private MinecraftHudIntegration() {}

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
