package io.github.leawind.thirdperson.internal.application;

import io.github.leawind.thirdperson.internal.core.camera.CameraMode;
import io.github.leawind.thirdperson.internal.core.camera.CameraPose;
import io.github.leawind.thirdperson.internal.core.camera.CameraSmoother;
import io.github.leawind.thirdperson.internal.core.camera.TightSpaceDetector;
import io.github.leawind.thirdperson.internal.core.config.CameraProfileSlot;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import io.github.leawind.thirdperson.internal.core.input.LookController;
import io.github.leawind.thirdperson.internal.core.input.CameraAdjustmentController;
import java.util.Objects;
import java.util.Optional;

/// Minecraft-independent mutable state for one active client session.
public final class ThirdPersonSession {
  private boolean perspectiveActive;
  private CameraMode mode = CameraMode.BYPASS;
  private final LookController lookController = new LookController();
  private final CameraSmoother cameraSmoother = new CameraSmoother();
  private final CameraAdjustmentController cameraAdjustmentController =
      new CameraAdjustmentController();
  private final TightSpaceDetector tightSpaceDetector = new TightSpaceDetector();
  private CameraPose lastSafeCameraPose;
  private CameraPose finalCameraPose;
  private CameraProfileSlot cameraAdjustmentSlot;
  private boolean temporaryFirstPersonRequested;
  private CameraMode modeBeforeTemporaryFirstPerson = CameraMode.NORMAL;

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

  public CameraSmoother cameraSmoother() {
    return cameraSmoother;
  }

  public CameraAdjustmentController cameraAdjustmentController() {
    return cameraAdjustmentController;
  }

  public TightSpaceDetector tightSpaceDetector() {
    return tightSpaceDetector;
  }

  public boolean isTemporaryFirstPersonRequested() {
    return temporaryFirstPersonRequested;
  }

  public CameraMode compositionMode() {
    return mode == CameraMode.TEMP_FIRST_PERSON ? modeBeforeTemporaryFirstPerson : mode;
  }

  public void requestTemporaryFirstPerson(boolean requested) {
    if (requested == temporaryFirstPersonRequested) {
      return;
    }
    temporaryFirstPersonRequested = requested;
    if (requested) {
      modeBeforeTemporaryFirstPerson =
          mode == CameraMode.AIMING ? CameraMode.AIMING : CameraMode.NORMAL;
      mode = CameraMode.TEMP_FIRST_PERSON;
    } else if (perspectiveActive) {
      mode = modeBeforeTemporaryFirstPerson;
    }
  }

  public Optional<CameraProfileSlot> cameraAdjustmentSlot() {
    return Optional.ofNullable(cameraAdjustmentSlot);
  }

  public void beginCameraAdjustment(
      CameraProfileSlot slot, ThirdPersonConfig.CameraProfile profile) {
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

  public Optional<CameraPose> lastSafeCameraPose() {
    return Optional.ofNullable(lastSafeCameraPose);
  }

  public void recordSafeCameraPose(CameraPose pose) {
    lastSafeCameraPose = Objects.requireNonNull(pose, "pose");
  }

  public Optional<CameraPose> finalCameraPose() {
    return Optional.ofNullable(finalCameraPose);
  }

  public void recordFinalCameraPose(CameraPose pose) {
    finalCameraPose = Objects.requireNonNull(pose, "pose");
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
    lookController.reset();
    cameraSmoother.reset();
    cameraAdjustmentController.reset();
    tightSpaceDetector.reset();
    cameraAdjustmentSlot = null;
    lastSafeCameraPose = null;
    finalCameraPose = null;
    temporaryFirstPersonRequested = false;
    modeBeforeTemporaryFirstPerson = CameraMode.NORMAL;
  }
}
