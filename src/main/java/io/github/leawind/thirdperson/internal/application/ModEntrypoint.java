package io.github.leawind.thirdperson.internal.application;

import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftInputIntegration;

public final class ModEntrypoint {
  private ModEntrypoint() {}

  public static void initialize() {
    ThirdPersonRuntime.getInstance().initialize();
    MinecraftInputIntegration.register();
  }
}
