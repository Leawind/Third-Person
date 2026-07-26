package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/// Neutral bridge event emitted before vanilla handles a mouse-wheel input.
public final class MouseScrollEvent {
  private static final CopyOnWriteArrayList<Listener> LISTENERS = new CopyOnWriteArrayList<>();

  private MouseScrollEvent() {}

  public static void register(Listener listener) {
    LISTENERS.add(Objects.requireNonNull(listener, "listener"));
  }

  public static boolean emit(double xOffset, double yOffset) {
    for (Listener listener : LISTENERS) {
      if (listener.onScroll(xOffset, yOffset)) {
        return true;
      }
    }
    return false;
  }

  @FunctionalInterface
  public interface Listener {
    boolean onScroll(double xOffset, double yOffset);
  }
}
