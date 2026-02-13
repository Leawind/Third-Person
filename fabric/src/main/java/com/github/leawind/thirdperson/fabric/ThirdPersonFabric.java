package com.github.leawind.thirdperson.fabric;

import com.github.leawind.thirdperson.ThirdPerson;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public final class ThirdPersonFabric implements ClientModInitializer {
  public void onInitializeClient() {
    ThirdPerson.init(Minecraft.getInstance());
  }
}
