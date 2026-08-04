package io.github.leawind.thirdperson.internal.logic.scheduler;

import io.github.leawind.thirdperson.internal.logic.scheduler.hud.CrosshairPolicy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

public final class MinecraftHudIntegration {
  private MinecraftHudIntegration() {}

  public static boolean shouldRenderCrosshair(boolean vanillaDecision) {
    SchedulerRuntime runtime = SchedulerRuntime.getInstance();
    boolean current = runtime.base().isCameraControlEnabled();
    boolean fallFlying =
        Minecraft.getInstance().getCameraEntity() instanceof LivingEntity entity
            && entity.isFallFlying();
    return CrosshairPolicy.shouldRender(
        vanillaDecision,
        current,
        runtime.isAiming(),
        fallFlying,
        runtime.hudSettings().crosshairMode(),
        runtime.hudSettings().hideCrosshairWhenFallFlyingAndNotAiming());
  }
}
