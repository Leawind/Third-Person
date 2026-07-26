package io.github.leawind.thirdperson.internal.application;

import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftInputIntegration;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftClientIntegration;
import io.github.leawind.thirdperson.internal.integration.config.MinecraftConfigIntegration;

public final class ModEntrypoint {
  private ModEntrypoint() {}

  public static void initialize() {
    ThirdPersonRuntime.getInstance().initialize();
    MinecraftConfigIntegration.register();
    MinecraftClientIntegration.register();
    MinecraftInputIntegration.register();
  }
}
