package io.github.leawind.thirdperson.internal.application;

import io.github.leawind.thirdperson.internal.application.camera.CameraController;
import io.github.leawind.thirdperson.internal.application.camera.CameraFrameInput;
import io.github.leawind.thirdperson.internal.core.camera.CameraMode;
import io.github.leawind.thirdperson.internal.core.camera.CameraPose;
import io.github.leawind.thirdperson.internal.core.camera.CameraSmoothingParameters;
import io.github.leawind.thirdperson.internal.core.config.CameraProfileSlot;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import java.util.Objects;
import java.util.Optional;

/// Process-wide owner of services and the current Minecraft-independent session state.
public final class ThirdPersonRuntime {
  private static final ThirdPersonRuntime INSTANCE = new ThirdPersonRuntime();

  private final ThirdPersonSession session = new ThirdPersonSession();
  private final CameraController cameraController = new CameraController(session);
  private volatile ThirdPersonConfig config = ThirdPersonConfig.defaults();
  private boolean initialized;

  private ThirdPersonRuntime() {}

  public static ThirdPersonRuntime getInstance() {
    return INSTANCE;
  }

  public ThirdPersonSession session() {
    return session;
  }

  public ThirdPersonConfig config() {
    return config;
  }

  public boolean isCameraControlEnabled() {
    return session.isControllingCamera();
  }

  public ThirdPersonConfig.CameraProfile cameraProfile(boolean centered) {
    ThirdPersonConfig.CameraProfile profile =
        session.mode() == CameraMode.AIMING ? config.camera().aiming() : config.camera().normal();
    return centered ? profile.withCentered(true) : profile;
  }

  public CameraSmoothingParameters cameraSmoothing(boolean flyingOrSwimming) {
    ThirdPersonConfig.SmoothingSettings smoothing = config.camera().smoothing();
    ThirdPersonConfig.ModeSmoothing modeSmoothing =
        session.mode() == CameraMode.AIMING ? smoothing.aiming() : smoothing.normal();
    double horizontalPivotHalfLife =
        flyingOrSwimming
            ? smoothing.flyingPivotHalfLife()
            : modeSmoothing.horizontalPivotHalfLife();
    double verticalPivotHalfLife =
        flyingOrSwimming ? smoothing.flyingPivotHalfLife() : modeSmoothing.verticalPivotHalfLife();
    boolean adjusting = session.cameraAdjustmentController().isAdjusting();
    double offsetHalfLife =
        adjusting ? smoothing.adjustingOffsetHalfLife() : modeSmoothing.offsetHalfLife();
    double distanceHalfLife =
        adjusting ? smoothing.adjustingDistanceHalfLife() : modeSmoothing.distanceHalfLife();
    return new CameraSmoothingParameters(
        horizontalPivotHalfLife,
        verticalPivotHalfLife,
        smoothing.rotationHalfLife(),
        offsetHalfLife,
        distanceHalfLife,
        modeSmoothing.fovHalfLife());
  }

  public Optional<CameraPose> updateCamera(CameraFrameInput frame) {
    Objects.requireNonNull(frame, "frame");
    return cameraController.update(
        frame, cameraProfile(frame.flyingOrSwimming()), cameraSmoothing(frame.flyingOrSwimming()));
  }

  public boolean initialize() {
    if (initialized) {
      return false;
    }
    initialized = true;
    return true;
  }

  public void onPerspectiveActivated() {
    if (!session.isPerspectiveActive()) {
      session.activatePerspective();
    }
  }

  public void onPerspectiveDeactivated() {
    session.reset();
  }

  public void onClientIdentityChanged(boolean perspectiveCurrent) {
    session.reset();
    if (perspectiveCurrent) {
      onPerspectiveActivated();
    }
  }

  public void updateConfig(ThirdPersonConfig config) {
    this.config = Objects.requireNonNull(config, "config");
  }

  public ThirdPersonConfig updateNormalCameraProfile(ThirdPersonConfig.CameraProfile profile) {
    return updateCameraProfile(CameraProfileSlot.NORMAL, profile);
  }

  public ThirdPersonConfig updateCameraProfile(
      CameraProfileSlot slot, ThirdPersonConfig.CameraProfile profile) {
    Objects.requireNonNull(slot, "slot");
    Objects.requireNonNull(profile, "profile");
    ThirdPersonConfig previous = config;
    ThirdPersonConfig.CameraSettings previousCamera = previous.camera();
    ThirdPersonConfig updated =
        new ThirdPersonConfig(
            previous.schemaVersion(),
            new ThirdPersonConfig.CameraSettings(
                slot == CameraProfileSlot.NORMAL ? profile : previousCamera.normal(),
                slot == CameraProfileSlot.AIMING ? profile : previousCamera.aiming(),
                previousCamera.smoothing()),
            previous.aiming(),
            previous.player(),
            previous.hud());
    updateConfig(updated);
    return updated;
  }

  public void setAiming(boolean aiming) {
    if (!session.isPerspectiveActive()) {
      return;
    }
    session.setMode(aiming ? CameraMode.AIMING : CameraMode.NORMAL);
  }
}
