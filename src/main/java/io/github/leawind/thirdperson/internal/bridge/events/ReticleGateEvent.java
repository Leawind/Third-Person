package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/// Neutral event for the first-person gate used by vanilla reticle rendering.
public final class ReticleGateEvent {
  private static final CopyOnWriteArrayList<Listener> LISTENERS = new CopyOnWriteArrayList<>();

  private ReticleGateEvent() {}

  public static void register(Listener listener) {
    LISTENERS.add(Objects.requireNonNull(listener, "listener"));
  }

  public static boolean emit(boolean vanillaDecision) {
    boolean result = vanillaDecision;
    for (Listener listener : LISTENERS) {
      result = listener.modify(result);
    }
    return result;
  }

  @FunctionalInterface
  public interface Listener {
    boolean modify(boolean vanillaDecision);
  }
}
