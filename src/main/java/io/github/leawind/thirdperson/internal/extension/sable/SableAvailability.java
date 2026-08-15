package io.github.leawind.thirdperson.internal.extension.sable;

import io.github.leawind.thirdperson.platform.api.Services;

/// Cached availability of the optional Sable Companion runtime.
final class SableAvailability {
  private static final boolean AVAILABLE =
      Services.PLATFORM_HELPER.isModLoaded("sablecompanion");

  private SableAvailability() {}

  static boolean isAvailable() {
    return AVAILABLE;
  }
}
