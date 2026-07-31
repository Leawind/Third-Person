package io.github.leawind.thirdperson.internal.bridge.events;

/// Neutral pre-world-render event used to apply interpolated client-side state.
public final class RenderFrameEvent {
  private static final SingleEventHandler<Listener> HANDLER = new SingleEventHandler<>();

  private RenderFrameEvent() {}

  public static void register(Listener listener) {
    HANDLER.install(listener);
  }

  public static void emit(float partialTick) {
    if (!Float.isFinite(partialTick)) {
      return;
    }
    Listener listener = HANDLER.get();
    if (listener != null) {
      listener.beforeRender(partialTick);
    }
  }

  @FunctionalInterface
  public interface Listener {
    void beforeRender(float partialTick);
  }
}
