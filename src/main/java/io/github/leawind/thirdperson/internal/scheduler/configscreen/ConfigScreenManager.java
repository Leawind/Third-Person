package io.github.leawind.thirdperson.internal.scheduler.configscreen;

import net.minecraft.client.gui.screens.Screen;

/// Keeps every hard YACL reference out of loader entrypoints and normal runtime paths.
public final class ConfigScreenManager {
  private static final String YACL_CLASS = "dev.isxander.yacl3.api.YetAnotherConfigLib";

  private ConfigScreenManager() {}

  public static boolean isAvailable() {
    try {
      Class.forName(YACL_CLASS, false, Thread.currentThread().getContextClassLoader());
      return true;
    } catch (ClassNotFoundException | LinkageError exception) {
      return false;
    }
  }

  public static Screen build(Screen parent) {
    if (!isAvailable()) {
      return parent;
    }
    return YaclConfigScreenBuilder.build(parent);
  }
}
