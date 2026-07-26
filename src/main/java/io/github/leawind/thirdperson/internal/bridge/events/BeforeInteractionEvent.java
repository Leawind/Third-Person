package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/// Neutral event emitted before vanilla resolves an attack, use, or pick interaction.
public final class BeforeInteractionEvent {
  private static final CopyOnWriteArrayList<Listener> LISTENERS = new CopyOnWriteArrayList<>();

  private BeforeInteractionEvent() {}

  public static void register(Listener listener) {
    LISTENERS.add(Objects.requireNonNull(listener, "listener"));
  }

  /// Returns whether vanilla should repick after listeners have prepared interaction state.
  public static boolean emit() {
    for (Listener listener : LISTENERS) {
      if (listener.beforeInteraction()) {
        return true;
      }
    }
    return false;
  }

  @FunctionalInterface
  public interface Listener {
    boolean beforeInteraction();
  }
}
