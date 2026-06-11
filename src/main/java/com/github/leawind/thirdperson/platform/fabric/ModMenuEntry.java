/*? if fabric {*/
package com.github.leawind.thirdperson.platform.fabric;

import com.github.leawind.thirdperson.api.ThirdPerson;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/** Modmenu 入口 */
@SuppressWarnings("unused")
public class ModMenuEntry implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return ThirdPerson.CONFIG_MANAGER::getConfigScreen;
  }
}
/*?}*/
