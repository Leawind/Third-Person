package io.github.leawind.thirdperson.platform.neoforge;

/*? if neoforge {*/
/*import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.bootstrap.ModBootstrap;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftKeyIntegration;
/^?   if >=1.21.11 {^/
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(value = ThirdPerson.MOD_ID, dist = Dist.CLIENT)
public final class Entrypoint {
  public Entrypoint(IEventBus modBus) {
    ModBootstrap.initialize();
    NeoForgeConfigScreenIntegration.register();
    modBus.addListener(Entrypoint::registerKeyMappings);
  }

  private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
    MinecraftKeyIntegration.registerKeyMappings(event::register);
  }
}

/^?   } else {^/
/^import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(value = ThirdPerson.MOD_ID)
public final class Entrypoint {
  public Entrypoint(IEventBus modBus) {
    if (FMLEnvironment.dist != Dist.CLIENT) {
      return;
    }
    ModBootstrap.initialize();
    NeoForgeConfigScreenIntegration.register();
    modBus.addListener(Entrypoint::registerKeyMappings);
  }

  private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
    MinecraftKeyIntegration.registerKeyMappings(event::register);
  }
}
^//^?   }^/

*//*? }*/
