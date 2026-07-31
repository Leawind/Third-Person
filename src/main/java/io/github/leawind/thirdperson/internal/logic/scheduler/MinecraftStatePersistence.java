package io.github.leawind.thirdperson.internal.logic.scheduler;

import io.github.leawind.thirdperson.ThirdPerson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;

/// Loads runtime-owned state and observes it for debounced persistence.
public final class MinecraftStatePersistence {
  private static final int SAVE_DELAY_TICKS = 20;
  private static final JsonStateStore STORE = new JsonStateStore();

  private static boolean loaded;
  private static Path statePath;
  private static ThirdPersonPersistentState observedState;
  private static ThirdPersonPersistentState pendingSave;
  private static int saveDelayTicks;

  private MinecraftStatePersistence() {}

  public static void flushScheduledSave() {
    if (!loaded || statePath == null) {
      return;
    }
    ThirdPersonPersistentState current =
        ThirdPersonPersistentState.extract(SchedulerRuntime.getInstance());
    if (pendingSave == null && current.equals(observedState)) {
      return;
    }
    observedState = current;
    if (save(current)) {
      pendingSave = null;
      saveDelayTicks = 0;
    } else {
      pendingSave = current;
      saveDelayTicks = SAVE_DELAY_TICKS;
    }
  }

  public static void onClientTick() {
    if (!loaded) {
      loadOnce();
      return;
    }

    ThirdPersonPersistentState current =
        ThirdPersonPersistentState.extract(SchedulerRuntime.getInstance());
    if (!current.equals(observedState)) {
      observedState = current;
      pendingSave = current;
      saveDelayTicks = SAVE_DELAY_TICKS;
    } else if (pendingSave != null && saveDelayTicks > 0 && --saveDelayTicks == 0) {
      ThirdPersonPersistentState state = pendingSave;
      if (save(state)) {
        pendingSave = null;
      } else {
        saveDelayTicks = SAVE_DELAY_TICKS;
      }
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
    boolean needsSave = false;
    try {
      if (Files.exists(path)) {
        state = STORE.load(path);
        ThirdPerson.LOGGER.info("Loaded state from {}", path);
      } else {
        needsSave = !save(state);
        if (!needsSave) {
          ThirdPerson.LOGGER.info("Created state at {}", path);
        }
      }
    } catch (IOException | RuntimeException exception) {
      ThirdPerson.LOGGER.error("Failed to load state from {}; using defaults", path, exception);
      needsSave = true;
    }
    state.applyTo(SchedulerRuntime.getInstance());
    observedState = ThirdPersonPersistentState.extract(SchedulerRuntime.getInstance());
    if (needsSave) {
      pendingSave = observedState;
      saveDelayTicks = SAVE_DELAY_TICKS;
    }
  }

  private static boolean save(ThirdPersonPersistentState state) {
    try {
      STORE.save(statePath, state);
      return true;
    } catch (IOException exception) {
      ThirdPerson.LOGGER.error("Failed to save state to {}", statePath, exception);
      return false;
    }
  }
}
