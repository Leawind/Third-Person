package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;

/// Neutral event emitted before vanilla resolves an attack, use, or pick interaction.
public final class BeforeInteractionEvent {
  private static final SingleEventHandler<Listener> HANDLER = new SingleEventHandler<>();

  private BeforeInteractionEvent() {}

  public static void register(Listener listener) {
    HANDLER.install(listener);
  }

  /// Returns whether a listener prepared authoritative interaction state.
  public static Result emit() {
    Listener listener = HANDLER.get();
    if (listener == null) {
      return Result.PASS;
    }
    return Objects.requireNonNull(listener.beforeInteraction(), "listener result");
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
