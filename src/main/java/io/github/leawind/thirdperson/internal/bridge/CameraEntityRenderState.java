package io.github.leawind.thirdperson.internal.bridge;

import io.github.leawind.thirdperson.internal.bridge.events.CameraEntityOpacityEvent;

/// Version-neutral render context shared by camera-entity rendering Mixins.
public final class CameraEntityRenderState {
  private static final float INVISIBLE_THRESHOLD = 0.01f;
  private static final float VANILLA_RENDER_THRESHOLD = 0.99f;

  private static Object cameraEntityRenderState;
  private static boolean applyingTransparency;
  private static float opacity = 1.0f;

  private CameraEntityRenderState() {}

  public static void beginFrame() {
    cameraEntityRenderState = null;
    end();
  }

  public static void setCameraEntityRenderState(Object renderState) {
    cameraEntityRenderState = renderState;
  }

  public static boolean isCameraEntityRenderState(Object renderState) {
    return renderState != null && renderState == cameraEntityRenderState;
  }

  /// Begins the camera-entity scope and returns whether rendering can be skipped entirely.
  public static boolean begin(float partialTick) {
    opacity = CameraEntityOpacityEvent.emit(partialTick);
    applyingTransparency = opacity < VANILLA_RENDER_THRESHOLD;
    return applyingTransparency && opacity <= INVISIBLE_THRESHOLD;
  }

  public static void end() {
    applyingTransparency = false;
    opacity = 1.0f;
  }

  public static boolean isApplyingTransparency() {
    return applyingTransparency;
  }

  public static float applyOpacity(float originalAlpha) {
    return applyingTransparency ? Math.min(originalAlpha, opacity) : originalAlpha;
  }

  public static int applyOpacity(int color) {
    if (!applyingTransparency) {
      return color;
    }
    int originalAlpha = color >>> 24;
    int cameraEntityAlpha = (int) (opacity * 255.0f);
    int alpha = Math.min(originalAlpha, cameraEntityAlpha);
    return color & 0x00ffffff | alpha << 24;
  }
}
