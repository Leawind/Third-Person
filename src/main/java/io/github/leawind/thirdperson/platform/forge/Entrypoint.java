package io.github.leawind.thirdperson.platform.forge;

/*? if forge {*/
/*import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.application.ModEntrypoint;
import io.github.leawind.thirdperson.internal.integration.minecraft.MinecraftKeyIntegration;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.api.distmarker.Dist;
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
    context.getModEventBus().addListener(Entrypoint::registerKeyMappings);
  }

  private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
    MinecraftKeyIntegration.registerKeyMappings(event::register);
  }
}
*//*?}*/
