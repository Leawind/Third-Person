package io.github.leawind.thirdperson.platform.fabric;

/*? if fabric {*/
import com.google.auto.service.AutoService;
import io.github.leawind.thirdperson.platform.api.PlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

@AutoService(PlatformHelper.class)
public class PlatformHelperImpl implements PlatformHelper {

  @Override
  public boolean isDevelopmentEnvironment() {
    return FabricLoader.getInstance().isDevelopmentEnvironment();
  }
}
/*?}*/
