package io.github.leawind.thirdperson.internal.bridge.events;

/// Neutral event for the first-person gate used by vanilla reticle rendering.
public final class ReticleGateEvent {
  private static final SingleEventHandler<Listener> HANDLER = new SingleEventHandler<>();

  private ReticleGateEvent() {}

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
