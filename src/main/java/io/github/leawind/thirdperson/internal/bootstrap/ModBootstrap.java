package io.github.leawind.thirdperson.internal.bootstrap;

import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.integration.config.MinecraftConfigIntegration;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftClientIntegration;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftHudIntegration;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftInputIntegration;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftInteractionIntegration;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftKeyIntegration;
import io.github.leawind.thirdperson.internal.integration.resource.MinecraftItemPredicateIntegration;

/// Outermost composition root for loader entrypoints and Minecraft integrations.
public final class ModBootstrap {
  private ModBootstrap() {}

  public static void initialize() {
    if (ThirdPersonRuntime.getInstance().initialize()) {
      ThirdPerson.LOGGER.info("{} initialized", ThirdPerson.MOD_NAME);
    }
    MinecraftConfigIntegration.register();
    MinecraftItemPredicateIntegration.register();
    MinecraftKeyIntegration.register();
    MinecraftClientIntegration.register();
    MinecraftInputIntegration.register();
    MinecraftHudIntegration.register();
    MinecraftInteractionIntegration.register();
  }
}
