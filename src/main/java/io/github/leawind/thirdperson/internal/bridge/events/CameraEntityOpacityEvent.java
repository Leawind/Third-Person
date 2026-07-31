package io.github.leawind.thirdperson.internal.bridge.events;

/// Neutral render-time query for the opacity selected by the base layer.
public final class CameraEntityOpacityEvent {
  private static final SingleEventHandler<Listener> HANDLER = new SingleEventHandler<>();

  private CameraEntityOpacityEvent() {}

  public static void register(Listener listener) {
    HANDLER.install(listener);
  }

  public static float emit(float partialTick) {
    if (!Float.isFinite(partialTick)) {
      return 1.0f;
    }
    Listener listener = HANDLER.get();
    if (listener == null) {
      return 1.0f;
    }
    float opacity = listener.cameraEntityOpacity(partialTick);
    return Float.isFinite(opacity) ? Math.max(0.0f, Math.min(1.0f, opacity)) : 1.0f;
  }

  @FunctionalInterface
  public interface Listener {
    float cameraEntityOpacity(float partialTick);
  }
}
