/*? if fabric {*/
package io.github.leawind.thirdperson.platform.fabric;

import io.github.leawind.thirdperson.internal.logic.ModEntrypoint;
import net.fabricmc.api.ClientModInitializer;

@SuppressWarnings("unused")
public final class Entrypoint implements ClientModInitializer {
  public void onInitializeClient() {
    ModEntrypoint.initialize();
  }
}
/*?}*/
