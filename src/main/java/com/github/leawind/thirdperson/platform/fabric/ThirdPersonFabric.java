/*? if fabric {*/
package com.github.leawind.thirdperson.platform.fabric;

import com.github.leawind.thirdperson.api.ThirdPerson;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public final class ThirdPersonFabric implements ClientModInitializer {
  public void onInitializeClient() {
    ThirdPerson.init(Minecraft.getInstance());
  }
}
/*?}*/
