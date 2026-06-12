package com.github.leawind.thirdperson.minecraft.logic;

import com.github.leawind.thirdperson.api.ThirdPerson;
import com.github.leawind.thirdperson.minecraft.logic.resources.ThirdPersonResources;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;

public final class ModEntrypoint {
  private ModEntrypoint() {}

  public static void initialize() {
    MixinExtrasBootstrap.init();

    ThirdPerson.getConfigManager().tryLoad();

    ThirdPersonResources.register();
    ThirdPersonKeys.registerKeyMappings(KeyMappingRegistry::register);
    ThirdPersonEventHandler.register();
  }
}
