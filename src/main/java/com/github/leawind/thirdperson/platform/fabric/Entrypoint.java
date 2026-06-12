/*? if fabric {*/
package com.github.leawind.thirdperson.platform.fabric;

import com.github.leawind.thirdperson.minecraft.logic.ModEntrypoint;
import net.fabricmc.api.ClientModInitializer;

@SuppressWarnings("unused")
public final class Entrypoint implements ClientModInitializer {
  public void onInitializeClient() {
    ModEntrypoint.initialize();
    initialize();
  }

  private void initialize() {}
}
/*?}*/
