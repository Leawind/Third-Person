package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/// Neutral bridge event emitted before the local player's vanilla turn handling.
public final class LocalPlayerTurnEvent {
  private static final CopyOnWriteArrayList<Listener> LISTENERS = new CopyOnWriteArrayList<>();

  private LocalPlayerTurnEvent() {}

  public static void register(Listener listener) {
    LISTENERS.add(Objects.requireNonNull(listener, "listener"));
  }

  public static boolean emit(double rawYaw, double rawPitch) {
    for (Listener listener : LISTENERS) {
      if (listener.onTurn(rawYaw, rawPitch)) {
        return true;
      }
    }
    return false;
  }

  @FunctionalInterface
  public interface Listener {
    boolean onTurn(double rawYaw, double rawPitch);
  }
}
