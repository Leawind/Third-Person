/*? if fabric {*/
package io.github.leawind.thirdperson.platform.fabric;

import io.github.leawind.thirdperson.internal.bootstrap.ModBootstrap;
import io.github.leawind.thirdperson.internal.logic.scheduler.MinecraftKeyIntegration;
import net.fabricmc.api.ClientModInitializer;
/*? if >=26.1 {*/
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
/*? } else {*/
/*import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
*//*? }*/

@SuppressWarnings("unused")
public final class Entrypoint implements ClientModInitializer {
  public void onInitializeClient() {
    ModBootstrap.initialize();
    /*? if >=26.1 {*/
    MinecraftKeyIntegration.registerKeyMappings(KeyMappingHelper::registerKeyMapping);
    /*? } else {*/
    /*MinecraftKeyIntegration.registerKeyMappings(KeyBindingHelper::registerKeyBinding);
    *//*? }*/
  }
}
/*?}*/
