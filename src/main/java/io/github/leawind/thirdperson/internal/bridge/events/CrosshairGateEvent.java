package io.github.leawind.thirdperson.internal.bridge.events;

/// Neutral event for the first-person gate used by vanilla crosshair rendering.
public final class CrosshairGateEvent {
  private static final SingleEventHandler<Listener> HANDLER = new SingleEventHandler<>();

  private CrosshairGateEvent() {}

  public static void register(Listener listener) {
    HANDLER.install(listener);
  }

  public static boolean emit(boolean vanillaDecision) {
    Listener listener = HANDLER.get();
    return listener == null ? vanillaDecision : listener.modify(vanillaDecision);
  }

  @FunctionalInterface
  public interface Listener {
    boolean modify(boolean vanillaDecision);
  }
}
