package io.github.leawind.thirdperson.internal.integration.config;

import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.bridge.events.ClientTickEvent;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;

/// Loads the config after Minecraft has established its game directory.
public final class MinecraftConfigIntegration {
  private static final JsonConfigStore STORE = new JsonConfigStore();
  private static boolean registered;
  private static boolean loaded;
  private static Path configPath;
  private static ThirdPersonConfig pendingSave;
  private static int saveDelayTicks;

  private MinecraftConfigIntegration() {}

  public static void register() {
    if (registered) {
      return;
    }
    registered = true;
    ClientTickEvent.register(MinecraftConfigIntegration::loadOnce);
  }

  private static void loadOnce() {
    if (loaded) {
      saveIfDue();
      return;
    }
    loaded = true;

    Path path =
        Minecraft.getInstance()
            .gameDirectory
            .toPath()
            .resolve("config")
            .resolve(ThirdPerson.MOD_ID + ".json");
    configPath = path;
    ThirdPersonConfig config = ThirdPersonConfig.defaults();
    try {
      if (Files.exists(path)) {
        DecodedConfig decoded = STORE.load(path);
        config = decoded.config();
        if (decoded.migrated()) {
          STORE.save(path, config);
          ThirdPerson.LOGGER.info("Migrated config at {}", path);
        } else {
          ThirdPerson.LOGGER.info("Loaded config from {}", path);
        }
      } else {
        STORE.save(path, config);
        ThirdPerson.LOGGER.info("Created config at {}", path);
      }
    } catch (IOException | RuntimeException exception) {
      ThirdPerson.LOGGER.error("Failed to load config from {}; using defaults", path, exception);
    }
    ThirdPersonRuntime.getInstance().updateConfig(config);
  }

  public static void scheduleSave(ThirdPersonConfig config) {
    pendingSave = config;
    saveDelayTicks = 20;
  }

  public static void saveNow(ThirdPersonConfig config) {
    scheduleSave(config);
    flushScheduledSave();
  }

  public static void flushScheduledSave() {
    if (pendingSave == null || configPath == null) {
      return;
    }
    ThirdPersonConfig config = pendingSave;
    pendingSave = null;
    saveDelayTicks = 0;
    try {
      STORE.save(configPath, config);
    } catch (IOException exception) {
      ThirdPerson.LOGGER.error("Failed to save config to {}", configPath, exception);
    }
  }

  private static void saveIfDue() {
    if (pendingSave != null && saveDelayTicks > 0 && --saveDelayTicks == 0) {
      flushScheduledSave();
    }
  }
}
