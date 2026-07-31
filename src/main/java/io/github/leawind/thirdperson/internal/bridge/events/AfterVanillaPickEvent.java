package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/// Neutral event emitted after vanilla updates the client's current hit result.
public final class AfterVanillaPickEvent {
  private static final CopyOnWriteArrayList<Listener> LISTENERS = new CopyOnWriteArrayList<>();

  private AfterVanillaPickEvent() {}

  public static void register(Listener listener) {
    LISTENERS.add(Objects.requireNonNull(listener, "listener"));
  }

  public static void emit(float partialTick) {
    if (!Float.isFinite(partialTick)) {
      return;
    }
    for (Listener listener : LISTENERS) {
      listener.afterVanillaPick(partialTick);
    }
  }

  @FunctionalInterface
  public interface Listener {
    void afterVanillaPick(float partialTick);
  }
}
