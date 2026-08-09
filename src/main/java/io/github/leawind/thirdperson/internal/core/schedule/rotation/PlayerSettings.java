package io.github.leawind.thirdperson.internal.core.schedule.rotation;

import io.github.leawind.thirdperson.internal.core.base.RaycastOrigin;
import java.util.Objects;

/// Owns player-rotation preferences.
public final class PlayerSettings {
  private NormalPlayerRotationMode normalMode = NormalPlayerRotationMode.INTEREST_POINT;
  private boolean autoRotateInteracting = true;
  private boolean doNotRotateWhenEating = true;
  private RaycastOrigin raycastOrigin = RaycastOrigin.CAMERA;

  public NormalPlayerRotationMode normalMode() {
    return normalMode;
  }

  public void setNormalMode(NormalPlayerRotationMode normalMode) {
    this.normalMode = Objects.requireNonNull(normalMode, "normalMode");
  }

  public boolean autoRotateInteracting() {
    return autoRotateInteracting;
  }

  public void setAutoRotateInteracting(boolean autoRotateInteracting) {
    this.autoRotateInteracting = autoRotateInteracting;
  }

  public boolean doNotRotateWhenEating() {
    return doNotRotateWhenEating;
  }

  public void setDoNotRotateWhenEating(boolean doNotRotateWhenEating) {
    this.doNotRotateWhenEating = doNotRotateWhenEating;
  }

  public RaycastOrigin raycastOrigin() {
    return raycastOrigin;
  }

  public void setRaycastOrigin(RaycastOrigin raycastOrigin) {
    this.raycastOrigin = Objects.requireNonNull(raycastOrigin, "raycastOrigin");
  }

  public void restore(
      NormalPlayerRotationMode normalMode,
      boolean autoRotateInteracting,
      boolean doNotRotateWhenEating,
      RaycastOrigin raycastOrigin) {
    this.normalMode = Objects.requireNonNull(normalMode, "normalMode");
    this.autoRotateInteracting = autoRotateInteracting;
    this.doNotRotateWhenEating = doNotRotateWhenEating;
    this.raycastOrigin = Objects.requireNonNull(raycastOrigin, "raycastOrigin");
  }
}
