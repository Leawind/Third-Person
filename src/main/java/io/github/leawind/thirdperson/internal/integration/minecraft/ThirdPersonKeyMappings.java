package io.github.leawind.thirdperson.internal.integration.minecraft;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.leawind.thirdperson.ThirdPerson;
import java.util.List;
import net.minecraft.client.KeyMapping;
/*? if >=1.21.11 {*/
import net.minecraft.resources.Identifier;
/*? }*/

/// Loader-neutral definitions for this mod's key mappings.
public final class ThirdPersonKeyMappings {
  /*? if >=1.21.11 {*/
  private static final KeyMapping.Category CATEGORY =
      KeyMapping.Category.register(
          Identifier.fromNamespaceAndPath(ThirdPerson.MOD_ID, "keybinds"));
  /*? } else {*/
  /*private static final String CATEGORY = "key.categories." + ThirdPerson.MOD_ID;
  *//*? }*/

  public static final KeyMapping ADJUST_CAMERA =
      new KeyMapping(key("adjust_camera"), InputConstants.KEY_Z, CATEGORY);
  public static final KeyMapping SWITCH_SHOULDER =
      new KeyMapping(key("switch_shoulder"), InputConstants.KEY_CAPSLOCK, CATEGORY);

  private ThirdPersonKeyMappings() {}

  public static List<KeyMapping> all() {
    return List.of(ADJUST_CAMERA, SWITCH_SHOULDER);
  }

  private static String key(String name) {
    return "key." + ThirdPerson.MOD_ID + "." + name;
  }
}
