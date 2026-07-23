package io.github.leawind.thirdperson.platform.api;

import net.minecraft.SharedConstants;

/// Abstraction over platform-specific utilities (Fabric, Forge, NeoForge).
///
/// Each platform provides its own implementation of this interface.
public interface PlatformHelper {

  /// Returns the current Minecraft data version number.
  default int getDataVersion() {
    /*? if >=1.21.11 {*/
    return SharedConstants.getCurrentVersion().dataVersion().version();
    /*? } else {*/
    /*return SharedConstants.getCurrentVersion().getDataVersion().getVersion();
    *//*? }*/
  }

  /// Returns {@code true} if the game is running in a development environment.
  boolean isDevelopmentEnvironment();
}
