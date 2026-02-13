package com.github.leawind.thirdperson.fabric;

import com.github.leawind.thirdperson.ThirdPerson;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.fabricmc.api.ClientModInitializer;

@SuppressWarnings("unused")
public final class ThirdPersonFabric implements ClientModInitializer {
  public void onInitializeClient() {
    ClientLifecycleEvent.CLIENT_STARTED.register(ThirdPerson::init);
  }
}
