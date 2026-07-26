package io.github.leawind.thirdperson.platform.fabric;

/*? if fabric {*/
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.leawind.thirdperson.internal.integration.config.ConfigScreenManager;

@SuppressWarnings("unused")
public final class ModMenuEntry implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return ConfigScreenManager.isAvailable()
        ? ConfigScreenManager::build
        : parent -> null;
  }
}
/*?}*/
