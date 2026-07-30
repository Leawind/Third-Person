package io.github.leawind.thirdperson.internal.bootstrap;

import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.base.application.BaseRuntime;
import io.github.leawind.thirdperson.internal.base.integration.minecraft.MinecraftClientIntegration;
import io.github.leawind.thirdperson.internal.scheduler.integration.minecraft.MinecraftHudIntegration;
import io.github.leawind.thirdperson.internal.base.integration.minecraft.MinecraftInputIntegration;
import io.github.leawind.thirdperson.internal.base.integration.minecraft.MinecraftInteractionIntegration;
import io.github.leawind.thirdperson.internal.scheduler.integration.minecraft.MinecraftKeyIntegration;
import io.github.leawind.thirdperson.internal.scheduler.integration.minecraft.MinecraftCameraAdjustmentIntegration;
import io.github.leawind.thirdperson.internal.scheduler.integration.minecraft.MinecraftSchedulingIntegration;
import io.github.leawind.thirdperson.internal.scheduler.integration.resource.MinecraftItemPredicateIntegration;
import io.github.leawind.thirdperson.internal.scheduler.persistence.MinecraftStatePersistence;
import io.github.leawind.thirdperson.internal.scheduler.SchedulerRuntime;

/// Outermost composition root for loader entrypoints and Minecraft integrations.
public final class ModBootstrap {
  private ModBootstrap() {}

  public static void initialize() {
    BaseRuntime base = BaseRuntime.getInstance();
    if (base.initialize()) {
      ThirdPerson.LOGGER.info("{} initialized", ThirdPerson.MOD_NAME);
    }
    SchedulerRuntime.getInstance().initialize(base);
    MinecraftStatePersistence.register();
    MinecraftItemPredicateIntegration.register();
    MinecraftKeyIntegration.register();
    MinecraftCameraAdjustmentIntegration.register();
    MinecraftSchedulingIntegration.register();
    MinecraftClientIntegration.register();
    MinecraftInputIntegration.register();
    MinecraftHudIntegration.register();
    MinecraftInteractionIntegration.register();
  }
}
