package io.github.leawind.thirdperson.internal.bridge.events;

/// Neutral event emitted after vanilla updates the client's current hit result.
public final class AfterVanillaPickEvent {
  private static final SingleEventHandler<Listener> HANDLER = new SingleEventHandler<>();

  private AfterVanillaPickEvent() {}

  public static void register(Listener listener) {
    HANDLER.install(listener);
  }

  public static void emit(float partialTick) {
    if (!Float.isFinite(partialTick)) {
      return;
    }
    Listener listener = HANDLER.get();
    if (listener != null) {
      listener.afterVanillaPick(partialTick);
    }
  }

  @FunctionalInterface
  public interface Listener {
    void afterVanillaPick(float partialTick);
  }
}
