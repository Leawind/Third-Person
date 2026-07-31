package io.github.leawind.thirdperson.internal.logic.scheduler.input;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.bridge.Bridge;
import java.util.List;
import net.minecraft.client.KeyMapping;

/// Loader-neutral definitions for this mod's key mappings.
public final class ThirdPersonKeyMappings {
  public static final KeyMapping ADJUST_CAMERA =
      Bridge.createKeyMapping(key("adjust_camera"), InputConstants.KEY_Z);
  public static final KeyMapping SWITCH_SHOULDER =
      Bridge.createKeyMapping(key("switch_shoulder"), InputConstants.KEY_CAPSLOCK);
  public static final KeyMapping AIM =
      Bridge.createKeyMapping(key("aim"), InputConstants.UNKNOWN.getValue());

  private ThirdPersonKeyMappings() {}

  public static List<KeyMapping> all() {
    return List.of(ADJUST_CAMERA, SWITCH_SHOULDER, AIM);
  }

  private static String key(String name) {
    return "key." + ThirdPerson.MOD_ID + "." + name;
  }
}
