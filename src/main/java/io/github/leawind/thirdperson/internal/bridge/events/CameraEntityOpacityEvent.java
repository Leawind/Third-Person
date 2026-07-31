package io.github.leawind.thirdperson.internal.bridge.events;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/// Neutral render-time query for the opacity selected by the base layer.
public final class CameraEntityOpacityEvent {
  private static final CopyOnWriteArrayList<Listener> LISTENERS = new CopyOnWriteArrayList<>();

  private CameraEntityOpacityEvent() {}

  public static void register(Listener listener) {
    LISTENERS.add(Objects.requireNonNull(listener, "listener"));
  }

  public static float emit(float partialTick) {
    if (!Float.isFinite(partialTick)) {
      return 1.0f;
    }
    float opacity = 1.0f;
    for (Listener listener : LISTENERS) {
      float candidate = listener.cameraEntityOpacity(partialTick);
      if (Float.isFinite(candidate)) {
        opacity = Math.min(opacity, Math.max(0.0f, Math.min(1.0f, candidate)));
      }
    }
    return opacity;
  }

  @FunctionalInterface
  public interface Listener {
    float cameraEntityOpacity(float partialTick);
  }
}
