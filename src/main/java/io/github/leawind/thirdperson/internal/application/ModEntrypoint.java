package io.github.leawind.thirdperson.internal.application;

import io.github.leawind.thirdperson.internal.integration.config.MinecraftConfigIntegration;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftClientIntegration;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftInputIntegration;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftKeyIntegration;
import io.github.leawind.thirdperson.internal.integration.perspective.MinecraftTemporaryFirstPersonIntegration;
import io.github.leawind.thirdperson.internal.integration.resource.MinecraftAimingRuleIntegration;

public final class ModEntrypoint {
  private ModEntrypoint() {}

  public static void initialize() {
    ThirdPersonRuntime.getInstance().initialize();
    MinecraftConfigIntegration.register();
    MinecraftAimingRuleIntegration.register();
    MinecraftKeyIntegration.register();
    MinecraftClientIntegration.register();
    MinecraftInputIntegration.register();
    MinecraftTemporaryFirstPersonIntegration.register();
  }
}
