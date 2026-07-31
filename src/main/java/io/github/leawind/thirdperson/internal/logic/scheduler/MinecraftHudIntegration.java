package io.github.leawind.thirdperson.internal.logic.scheduler;


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
