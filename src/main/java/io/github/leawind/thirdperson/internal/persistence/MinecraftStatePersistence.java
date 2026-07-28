package io.github.leawind.thirdperson.internal.persistence;

import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.bridge.events.ClientTickEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;

/// Loads runtime-owned state and observes it for debounced persistence.
public final class MinecraftStatePersistence {
  private static final int SAVE_DELAY_TICKS = 20;
  private static final JsonStateStore STORE = new JsonStateStore();

  private static boolean registered;
  private static boolean loaded;
  private static Path statePath;
  private static ThirdPersonPersistentState observedState;
  private static ThirdPersonPersistentState pendingSave;
  private static int saveDelayTicks;

  private MinecraftStatePersistence() {}

  public static void register() {
    if (registered) {
      return;
    }
    registered = true;
    ClientTickEvent.register(MinecraftStatePersistence::onClientTick);
  }

  public static void flushScheduledSave() {
    if (!loaded || statePath == null) {
      return;
    }
    ThirdPersonPersistentState current =
        ThirdPersonPersistentState.extract(ThirdPersonRuntime.getInstance());
    if (pendingSave == null && current.equals(observedState)) {
      return;
    }
    pendingSave = null;
    saveDelayTicks = 0;
    save(current);
    observedState = current;
  }

  private static void onClientTick() {
    if (!loaded) {
      loadOnce();
      return;
    }

    ThirdPersonPersistentState current =
        ThirdPersonPersistentState.extract(ThirdPersonRuntime.getInstance());
    if (!current.equals(observedState)) {
      observedState = current;
      pendingSave = current;
      saveDelayTicks = SAVE_DELAY_TICKS;
    } else if (pendingSave != null && saveDelayTicks > 0 && --saveDelayTicks == 0) {
      ThirdPersonPersistentState state = pendingSave;
      pendingSave = null;
      save(state);
    }
  }

  private static void loadOnce() {
    loaded = true;
    Path path =
        Minecraft.getInstance()
            .gameDirectory
            .toPath()
            .resolve("config")
            .resolve(ThirdPerson.MOD_ID + ".json");
    statePath = path;
    ThirdPersonPersistentState state = ThirdPersonPersistentState.defaults();
    try {
      if (Files.exists(path)) {
        state = STORE.load(path);
        ThirdPerson.LOGGER.info("Loaded state from {}", path);
      } else {
        STORE.save(path, state);
        ThirdPerson.LOGGER.info("Created state at {}", path);
      }
    } catch (IOException | RuntimeException exception) {
      ThirdPerson.LOGGER.error("Failed to load state from {}; using defaults", path, exception);
    }
    state.applyTo(ThirdPersonRuntime.getInstance());
    observedState = ThirdPersonPersistentState.extract(ThirdPersonRuntime.getInstance());
  }

  private static void save(ThirdPersonPersistentState state) {
    try {
      STORE.save(statePath, state);
    } catch (IOException exception) {
      ThirdPerson.LOGGER.error("Failed to save state to {}", statePath, exception);
    }
  }
}
