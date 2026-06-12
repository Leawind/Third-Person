package com.github.leawind.thirdperson.platform.neoforge;

/*? if neoforge {*/

/*import com.github.leawind.thirdperson.api.ThirdPerson;
import com.github.leawind.thirdperson.minecraft.logic.ThirdPersonKeys;
import com.github.leawind.thirdperson.minecraft.logic.ModEntrypoint;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@SuppressWarnings("unused")
@Mod(value = ThirdPerson.MOD_ID, dist = Dist.CLIENT)
public final class Entrypoint {
  public Entrypoint(IEventBus modBus) {
    ModEntrypoint.initialize();
    initialize();
  }

  private void initialize() {}

  @EventBusSubscriber(modid = ThirdPerson.MOD_ID)
  public static class EventHandler {

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
      ThirdPersonKeys.register(event::register);
    }
  }
}
*//*?}*/
