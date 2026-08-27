package io.github.leawind.thirdperson.internal.extension.sable;

import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.platform.api.Services;

/// Cached availability of the optional Sable Companion runtime.
final class SableAvailability {
  private static final boolean AVAILABLE =
      Services.PLATFORM_HELPER.isModLoaded("sablecompanion");
  private static final boolean COMPATIBLE = isCompatibleVersion();

  private SableAvailability() {}

  static boolean isAvailableAndCompatible() {
    if (AVAILABLE && !COMPATIBLE) {
      ThirdPerson.LOGGER.warn(
          "Sable Companion is installed but not compatible with this Minecraft version. "
              + "Raycast coordinates may be incorrect. Supported: 1.21 - 1.21.10");
    }
    return AVAILABLE && COMPATIBLE;
  }

  private static boolean isCompatibleVersion() {
    /*? if >=1.21 && <1.21.11 {*/
    return true;
    /*? } else {*/
    /*return false;
    *//*? }*/
  }
}
