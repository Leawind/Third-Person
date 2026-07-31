package io.github.leawind.thirdperson.internal.bridge.events;

/// Neutral end-of-client-tick event used for lifecycle and logical state updates.
public final class ClientTickEvent {
  private static final SingleEventHandler<Runnable> HANDLER = new SingleEventHandler<>();

  private ClientTickEvent() {}

  public static void register(Runnable listener) {
    HANDLER.install(listener);
  }

  public static void emit() {
    Runnable listener = HANDLER.get();
    if (listener != null) {
      listener.run();
    }
  }
}
