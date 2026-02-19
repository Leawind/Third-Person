package com.github.leawind.thirdperson.neoforge;

import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonConstants;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@SuppressWarnings("unused")
@Mod(value = ThirdPersonConstants.MOD_ID, dist = net.neoforged.api.distmarker.Dist.CLIENT)
public final class ThirdPersonNeoForgeClient {
  public ThirdPersonNeoForgeClient(IEventBus modBus) {
    
    ClientLifecycleEvent.CLIENT_STARTED.register(ThirdPerson::init);
  }
}
