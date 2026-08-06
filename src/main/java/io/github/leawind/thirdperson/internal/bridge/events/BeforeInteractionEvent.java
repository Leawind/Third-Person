package io.github.leawind.thirdperson.internal.bridge.events;

/// Neutral event emitted before vanilla resolves an attack, use, or pick interaction.
public final class BeforeInteractionEvent {
  private static final SingleEventHandler<Listener> HANDLER = new SingleEventHandler<>();

  private BeforeInteractionEvent() {}

  public static void register(Listener listener) {
    HANDLER.install(listener);
  }

  public static void emit() {
    Listener listener = HANDLER.get();
    if (listener != null) {
      listener.beforeInteraction();
    }
  }

  @FunctionalInterface
  public interface Listener {
    void beforeInteraction();
  }
}
