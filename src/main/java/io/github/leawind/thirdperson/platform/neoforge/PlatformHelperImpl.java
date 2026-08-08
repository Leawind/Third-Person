package io.github.leawind.thirdperson.platform.neoforge;

/*? if neoforge {*/
/*import com.google.auto.service.AutoService;
import io.github.leawind.thirdperson.platform.api.PlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;

@AutoService(PlatformHelper.class)
public class PlatformHelperImpl implements PlatformHelper {
  @Override
  public boolean isDevelopmentEnvironment() {
/^?   if >=1.21.11 {^/
    return !FMLLoader.getCurrent().isProduction();
/^?   } else {^/
/^return !FMLLoader.isProduction();
^//^?   }^/
  }

  @Override
  public boolean isModLoaded(String modId) {
    return ModList.get().isLoaded(modId);
  }
}
*//*?}*/
