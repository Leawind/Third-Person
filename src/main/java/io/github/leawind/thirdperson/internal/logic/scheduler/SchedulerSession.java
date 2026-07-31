package io.github.leawind.thirdperson.internal.logic.scheduler;

import io.github.leawind.thirdperson.internal.logic.base.camera.CameraProfile;
import io.github.leawind.thirdperson.internal.logic.scheduler.aiming.CameraMode;
import io.github.leawind.thirdperson.internal.logic.scheduler.camera.CameraAdjustmentController;
import io.github.leawind.thirdperson.internal.logic.scheduler.camera.CameraProfileSlot;
import java.util.Objects;
import java.util.Optional;

/// Transient state used only while selecting parameters for the base layer.
public final class SchedulerSession {
  private CameraMode mode = CameraMode.NORMAL;
  private final CameraAdjustmentController cameraAdjustmentController =
      new CameraAdjustmentController();
  private CameraProfileSlot cameraAdjustmentSlot;

  public CameraMode mode() {
    return mode;
  }

  public void setAiming(boolean aiming) {
    mode = aiming ? CameraMode.AIMING : CameraMode.NORMAL;
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

  public void reset() {
    mode = CameraMode.NORMAL;
    cameraAdjustmentController.reset();
    cameraAdjustmentSlot = null;
  }
}
