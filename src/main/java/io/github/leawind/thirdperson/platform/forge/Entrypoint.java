package io.github.leawind.thirdperson.platform.forge;

/*? if forge {*/
/*import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.logic.ModEntrypoint;
import io.github.leawind.thirdperson.internal.logic.scheduler.ConfigScreenManager;
import io.github.leawind.thirdperson.internal.logic.scheduler.MinecraftKeyIntegration;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(ThirdPerson.MOD_ID)
public class Entrypoint {
  public Entrypoint(final FMLJavaModLoadingContext context) {
    if(FMLEnvironment.dist != Dist.CLIENT) {
      return;
    }
    ModEntrypoint.initialize();
    registerConfigScreen();
    context.getModEventBus().addListener(Entrypoint::registerKeyMappings);
  }

  private static void registerConfigScreen() {
    if (!ConfigScreenManager.isAvailable()) {
      return;
    }
    ModList.get()
        .getModContainerById(ThirdPerson.MOD_ID)
        .ifPresent(
            container ->
                container.registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () ->
                        new ConfigScreenHandler.ConfigScreenFactory(
                            (minecraft, screen) -> ConfigScreenManager.build(screen))));
  }

  private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
    MinecraftKeyIntegration.registerKeyMappings(event::register);
  }
}
*//*?}*/
