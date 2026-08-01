package io.github.leawind.thirdperson.internal.logic.scheduler;

import io.github.leawind.thirdperson.internal.logic.scheduler.hud.CrosshairPolicy;

public final class MinecraftHudIntegration {
  private MinecraftHudIntegration() {}

  public static boolean shouldRenderCrosshair(boolean vanillaDecision) {
    SchedulerRuntime runtime = SchedulerRuntime.getInstance();
    boolean current = runtime.base().isCameraControlEnabled();
    return CrosshairPolicy.shouldRender(
        vanillaDecision,
        current,
        runtime.base().isCameraControlEnabled(),
        runtime.hudSettings().crosshairMode());
  }
}
