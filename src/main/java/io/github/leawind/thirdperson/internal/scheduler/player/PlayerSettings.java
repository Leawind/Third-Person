package io.github.leawind.thirdperson.internal.scheduler.player;

import io.github.leawind.thirdperson.internal.base.api.RaycastOrigin;
import io.github.leawind.thirdperson.internal.scheduler.player.NormalPlayerRotationMode;
import io.github.leawind.thirdperson.internal.scheduler.player.PlayerRotationMode;
import java.util.Objects;

/// Owns player-rotation preferences.
public final class PlayerSettings {
  private PlayerRotationMode rotationMode = PlayerRotationMode.AUTO;
  private NormalPlayerRotationMode normalMode = NormalPlayerRotationMode.INTEREST_POINT;
  private boolean autoRotateInteracting = true;
  private boolean doNotRotateWhenEating = true;
  private RaycastOrigin raycastOrigin = RaycastOrigin.CAMERA;

  public PlayerRotationMode rotationMode() {
    return rotationMode;
  }

  public void setRotationMode(PlayerRotationMode rotationMode) {
    this.rotationMode = Objects.requireNonNull(rotationMode, "rotationMode");
  }

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
      PlayerRotationMode rotationMode,
      NormalPlayerRotationMode normalMode,
      boolean autoRotateInteracting,
      boolean doNotRotateWhenEating,
      RaycastOrigin raycastOrigin) {
    this.rotationMode = Objects.requireNonNull(rotationMode, "rotationMode");
    this.normalMode = Objects.requireNonNull(normalMode, "normalMode");
    this.autoRotateInteracting = autoRotateInteracting;
    this.doNotRotateWhenEating = doNotRotateWhenEating;
    this.raycastOrigin = Objects.requireNonNull(raycastOrigin, "raycastOrigin");
  }
}
