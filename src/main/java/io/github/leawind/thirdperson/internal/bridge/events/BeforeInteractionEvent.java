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

  /// Returns whether a listener prepared authoritative interaction state.
  public static Result emit() {
    for (Listener listener : LISTENERS) {
      Result result = Objects.requireNonNull(listener.beforeInteraction(), "listener result");
      if (result != Result.PASS) {
        return result;
      }
    }
    return Result.PASS;
  }

  @FunctionalInterface
  public interface Listener {
    Result beforeInteraction();
  }

  public enum Result {
    PASS,
    APPLIED
  }
}
