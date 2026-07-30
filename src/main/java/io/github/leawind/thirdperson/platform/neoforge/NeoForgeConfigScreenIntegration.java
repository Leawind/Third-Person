package io.github.leawind.thirdperson.platform.neoforge;

/*? if neoforge {*/
/*import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.scheduler.configscreen.ConfigScreenManager;
import net.neoforged.fml.ModList;

final class NeoForgeConfigScreenIntegration {
  private NeoForgeConfigScreenIntegration() {}

  static void register() {
    if (!ConfigScreenManager.isAvailable()) {
      return;
    }
    /^? if >=1.20.6 {^/
    /^ModList.get()
        .getModContainerById(ThirdPerson.MOD_ID)
        .ifPresent(
            container ->
                container.registerExtensionPoint(
                    net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
                    (ignored, screen) -> ConfigScreenManager.build(screen)));
    ^//^? } else {^/
    ModList.get()
        .getModContainerById(ThirdPerson.MOD_ID)
        .ifPresent(
            container ->
                container.registerExtensionPoint(
                    net.neoforged.neoforge.client.ConfigScreenHandler.ConfigScreenFactory.class,
                    () ->
                        new net.neoforged.neoforge.client.ConfigScreenHandler.ConfigScreenFactory(
                            (minecraft, screen) -> ConfigScreenManager.build(screen))));
    /^? }^/
  }
}
*//*?}*/
