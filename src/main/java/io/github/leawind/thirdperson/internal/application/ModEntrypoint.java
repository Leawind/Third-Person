package io.github.leawind.thirdperson.internal.application;

import io.github.leawind.thirdperson.internal.integration.config.MinecraftConfigIntegration;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftClientIntegration;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftInputIntegration;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftKeyIntegration;

public final class ModEntrypoint {
  private ModEntrypoint() {}

  public static void initialize() {
    ThirdPersonRuntime.getInstance().initialize();
    MinecraftConfigIntegration.register();
    MinecraftKeyIntegration.register();
    MinecraftClientIntegration.register();
    MinecraftInputIntegration.register();
  }
}
