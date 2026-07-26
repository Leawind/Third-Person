package io.github.leawind.thirdperson.internal.application;

import io.github.leawind.thirdperson.internal.core.camera.CameraMode;
import java.util.Objects;

/// Minecraft-independent mutable state for one active client session.
public final class ThirdPersonSession {
  private boolean perspectiveActive;
  private CameraMode mode = CameraMode.BYPASS;

  public boolean isPerspectiveActive() {
    return perspectiveActive;
  }

  public CameraMode mode() {
    return mode;
  }

  public void activatePerspective() {
    perspectiveActive = true;
    mode = CameraMode.NORMAL;
  }

  public void setMode(CameraMode mode) {
    Objects.requireNonNull(mode, "mode");
    if (!perspectiveActive && mode != CameraMode.BYPASS) {
      throw new IllegalStateException("An inactive perspective can only be in bypass mode");
    }
    this.mode = mode;
  }

  public void reset() {
    perspectiveActive = false;
    mode = CameraMode.BYPASS;
  }
}
