package io.github.leawind.thirdperson.platform.neoforge;

/*? if neoforge {*/
/*import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.bootstrap.ModBootstrap;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftKeyIntegration;
import io.github.leawind.thirdperson.internal.integration.resource.MinecraftItemPredicateIntegration;
import net.minecraft.resources.ResourceLocation;
/^?   if >=1.21.11 {^/
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(value = ThirdPerson.MOD_ID, dist = Dist.CLIENT)
public final class Entrypoint {
  public Entrypoint(IEventBus modBus) {
    ModBootstrap.initialize();
    NeoForgeConfigScreenIntegration.register();
    modBus.addListener(Entrypoint::registerKeyMappings);
    modBus.addListener(Entrypoint::registerReloadListeners);
  }

  private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
    MinecraftKeyIntegration.registerKeyMappings(event::register);
  }

  private static void registerReloadListeners(AddClientReloadListenersEvent event) {
    event.addListener(
        ResourceLocation.fromNamespaceAndPath(ThirdPerson.MOD_ID, "item_patterns"),
        MinecraftItemPredicateIntegration.reloadListener());
  }
}

/^?   } else {^/
/^import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
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
    modBus.addListener(Entrypoint::registerReloadListeners);
  }

  private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
    MinecraftKeyIntegration.registerKeyMappings(event::register);
  }

  private static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
    event.registerReloadListener(MinecraftItemPredicateIntegration.reloadListener());
  }
}
^//^?   }^/

*//*? }*/
