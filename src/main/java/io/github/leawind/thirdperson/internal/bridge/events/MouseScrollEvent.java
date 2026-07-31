package io.github.leawind.thirdperson.internal.bridge.events;

/// Neutral bridge event emitted before vanilla handles a mouse-wheel input.
public final class MouseScrollEvent {
  private static final SingleEventHandler<Listener> HANDLER = new SingleEventHandler<>();

  private MouseScrollEvent() {}

  public static void register(Listener listener) {
    HANDLER.install(listener);
  }

  public static boolean emit(double xOffset, double yOffset) {
    Listener listener = HANDLER.get();
    return listener != null && listener.onScroll(xOffset, yOffset);
  }

  @FunctionalInterface
  public interface Listener {
    boolean onScroll(double xOffset, double yOffset);
  }
}
