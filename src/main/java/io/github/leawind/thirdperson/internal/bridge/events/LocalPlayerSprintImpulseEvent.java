package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/// Neutral bridge event for the directional-input condition used by vanilla sprinting.
public final class LocalPlayerSprintImpulseEvent {
  private static final CopyOnWriteArrayList<Listener> LISTENERS = new CopyOnWriteArrayList<>();

  private LocalPlayerSprintImpulseEvent() {}

  public static void register(Listener listener) {
    LISTENERS.add(Objects.requireNonNull(listener, "listener"));
  }

  public static boolean emit(
      boolean vanillaResult,
      double leftImpulse,
      double forwardImpulse,
      double minimumMagnitude) {
    boolean result = vanillaResult;
    for (Listener listener : LISTENERS) {
      result = listener.modify(result, leftImpulse, forwardImpulse, minimumMagnitude);
    }
    return result;
  }

  @FunctionalInterface
  public interface Listener {
    boolean modify(
        boolean vanillaResult,
        double leftImpulse,
        double forwardImpulse,
        double minimumMagnitude);
  }
}
