package io.github.leawind.thirdperson.platform.neoforge;

/*? if neoforge {*/
/*import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.application.ModEntrypoint;
/^?   if >=1.21.11 {^/
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = ThirdPerson.MOD_ID, dist = Dist.CLIENT)
public final class Entrypoint {
  public Entrypoint(IEventBus modBus) {
    ModEntrypoint.initialize();
  }
}

/^?   } else {^/
/^import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(value = ThirdPerson.MOD_ID)
public final class Entrypoint {
  public Entrypoint(IEventBus modBus) {
    if (FMLEnvironment.dist != Dist.CLIENT) {
      return;
    }
    ModEntrypoint.initialize();
  }
}
^//^?   }^/

*//*? }*/
