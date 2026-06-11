package com.github.leawind.thirdperson.platform.neoforge;

/*? if neoforge {*/
/*
import com.github.leawind.thirdperson.api.ThirdPerson;
import com.github.leawind.thirdperson.impl.ThirdPersonConstants;
import com.github.leawind.thirdperson.impl.ThirdPersonKeys;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@SuppressWarnings("unused")
@Mod(value = ThirdPersonConstants.MOD_ID, dist = net.neoforged.api.distmarker.Dist.CLIENT)
public final class ThirdPersonNeoForgeClient {
  public ThirdPersonNeoForgeClient(IEventBus modBus) {
    ClientLifecycleEvent.CLIENT_STARTED.register(ThirdPerson::init);
  }

  @EventBusSubscriber(modid = ThirdPersonConstants.MOD_ID)
  public static class EventHandler {

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
      ThirdPersonKeys.register(event::register);
    }
  }
}
*//*?}*/
