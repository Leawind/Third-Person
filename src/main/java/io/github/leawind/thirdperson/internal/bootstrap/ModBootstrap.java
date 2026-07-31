package io.github.leawind.thirdperson.internal.bootstrap;

import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.logic.base.BaseRuntime;
import io.github.leawind.thirdperson.internal.logic.base.MinecraftClientIntegration;
import io.github.leawind.thirdperson.internal.logic.scheduler.MinecraftHudIntegration;
import io.github.leawind.thirdperson.internal.logic.base.MinecraftInputIntegration;
import io.github.leawind.thirdperson.internal.logic.base.MinecraftInteractionIntegration;
import io.github.leawind.thirdperson.internal.logic.scheduler.MinecraftKeyIntegration;
import io.github.leawind.thirdperson.internal.logic.scheduler.MinecraftCameraAdjustmentIntegration;
import io.github.leawind.thirdperson.internal.logic.scheduler.MinecraftSchedulingIntegration;
import io.github.leawind.thirdperson.internal.logic.scheduler.MinecraftItemPredicateIntegration;
import io.github.leawind.thirdperson.internal.logic.scheduler.MinecraftStatePersistence;
import io.github.leawind.thirdperson.internal.logic.scheduler.SchedulerRuntime;

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
