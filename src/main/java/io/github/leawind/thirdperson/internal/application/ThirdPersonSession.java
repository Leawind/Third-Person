package io.github.leawind.thirdperson.internal.application;

import io.github.leawind.thirdperson.internal.core.camera.CameraMode;
import io.github.leawind.thirdperson.internal.core.camera.CameraPose;
import io.github.leawind.thirdperson.internal.core.camera.CameraProfile;
import io.github.leawind.thirdperson.internal.core.camera.CameraProfileSlot;
import io.github.leawind.thirdperson.internal.core.camera.CameraSmoother;
import io.github.leawind.thirdperson.internal.core.input.CameraAdjustmentController;
import io.github.leawind.thirdperson.internal.core.input.LookController;
import io.github.leawind.thirdperson.internal.core.player.PlayerRotationController;
import java.util.Objects;
import java.util.Optional;

/// Minecraft-independent mutable state for one active client session.
public final class ThirdPersonSession {
  private boolean perspectiveActive;
  private CameraMode mode = CameraMode.BYPASS;
  private final LookController lookController = new LookController();
  private final PlayerRotationController playerRotationController = new PlayerRotationController();
  private final CameraSmoother cameraSmoother = new CameraSmoother();
  private final CameraAdjustmentController cameraAdjustmentController =
      new CameraAdjustmentController();
  private CameraPose finalCameraPose;
  private CameraProfileSlot cameraAdjustmentSlot;

  public boolean isPerspectiveActive() {
    return perspectiveActive;
  }

  public CameraMode mode() {
    return mode;
  }

  public boolean isControllingCamera() {
    return perspectiveActive && mode != CameraMode.BYPASS;
  }

  public LookController lookController() {
    return lookController;
  }

  public PlayerRotationController playerRotationController() {
    return playerRotationController;
  }

  public CameraSmoother cameraSmoother() {
    return cameraSmoother;
  }

  public CameraAdjustmentController cameraAdjustmentController() {
    return cameraAdjustmentController;
  }

  public Optional<CameraProfileSlot> cameraAdjustmentSlot() {
    return Optional.ofNullable(cameraAdjustmentSlot);
  }

  public void beginCameraAdjustment(CameraProfileSlot slot, CameraProfile profile) {
    if (cameraAdjustmentController.isAdjusting()) {
      return;
    }
    cameraAdjustmentSlot = Objects.requireNonNull(slot, "slot");
    cameraAdjustmentController.begin(profile);
  }

  public void finishCameraAdjustment() {
    cameraAdjustmentController.finish();
    cameraAdjustmentSlot = null;
  }

  public Optional<CameraPose> finalCameraPose() {
    return Optional.ofNullable(finalCameraPose);
  }

  public void recordFinalCameraPose(CameraPose pose) {
    finalCameraPose = Objects.requireNonNull(pose, "pose");
  }

  /// Clears state tied to the identity and previous poses of the camera entity.
  public void resetCameraTracking() {
    lookController.reset();
    cameraSmoother.reset();
    finalCameraPose = null;
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
    resetCameraTracking();
    playerRotationController.reset();
    cameraAdjustmentController.reset();
    cameraAdjustmentSlot = null;
  }
}
