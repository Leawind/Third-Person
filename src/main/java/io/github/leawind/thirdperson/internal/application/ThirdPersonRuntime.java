package io.github.leawind.thirdperson.internal.application;

import io.github.leawind.thirdperson.internal.application.camera.CameraController;
import io.github.leawind.thirdperson.internal.application.camera.CameraFrameInput;
import io.github.leawind.thirdperson.internal.application.port.CameraCollisionPort;
import io.github.leawind.thirdperson.internal.core.camera.CameraMode;
import io.github.leawind.thirdperson.internal.core.camera.CameraPose;
import io.github.leawind.thirdperson.internal.core.camera.CameraSmoothingParameters;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import io.github.leawind.thirdperson.internal.core.config.CameraProfileSlot;
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
    return config.enabled() && session.isControllingCamera();
  }

  public ThirdPersonConfig.CameraProfile cameraProfile(boolean centered) {
    ThirdPersonConfig.CameraProfile profile =
        session.compositionMode() == CameraMode.AIMING
            ? config.camera().aiming()
            : config.camera().normal();
    return centered ? profile.withCentered(true) : profile;
  }

  public CameraSmoothingParameters cameraSmoothing(boolean flyingOrSwimming) {
    ThirdPersonConfig.SmoothingSettings smoothing = config.camera().smoothing();
    ThirdPersonConfig.ModeSmoothing modeSmoothing =
        session.compositionMode() == CameraMode.AIMING
            ? smoothing.aiming()
            : smoothing.normal();
    double horizontalPivotHalfLife =
        flyingOrSwimming
            ? smoothing.flyingPivotHalfLife()
            : modeSmoothing.horizontalPivotHalfLife();
    double verticalPivotHalfLife =
        flyingOrSwimming
            ? smoothing.flyingPivotHalfLife()
            : modeSmoothing.verticalPivotHalfLife();
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

  public Optional<CameraPose> updateCamera(
      CameraFrameInput frame, CameraCollisionPort collision) {
    Objects.requireNonNull(frame, "frame");
    return cameraController.update(
        frame,
        cameraProfile(frame.flyingOrSwimming()),
        cameraSmoothing(frame.flyingOrSwimming()),
        collision);
  }

  public boolean updateTightSpace(CameraFrameInput frame, CameraCollisionPort collision) {
    Objects.requireNonNull(frame, "frame");
    return cameraController.updateTightSpace(
        frame, cameraProfile(frame.flyingOrSwimming()), collision);
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
    if (!config.enabled()) {
      session.setMode(CameraMode.BYPASS);
    }
  }

  public void onPerspectiveDeactivated() {
    if (!session.isTemporaryFirstPersonRequested()) {
      session.reset();
    }
  }

  public void onClientIdentityChanged(boolean perspectiveCurrent) {
    session.reset();
    if (perspectiveCurrent) {
      onPerspectiveActivated();
    }
  }

  public void updateConfig(ThirdPersonConfig config) {
    this.config = Objects.requireNonNull(config, "config");
    if (!session.isPerspectiveActive()) {
      return;
    }
    if (!config.enabled()) {
      session.setMode(CameraMode.BYPASS);
    } else if (session.mode() == CameraMode.BYPASS) {
      session.setMode(CameraMode.NORMAL);
    }
  }

  public ThirdPersonConfig updateNormalCameraProfile(
      ThirdPersonConfig.CameraProfile profile) {
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
            previous.enabled(),
            new ThirdPersonConfig.CameraSettings(
                slot == CameraProfileSlot.NORMAL ? profile : previousCamera.normal(),
                slot == CameraProfileSlot.AIMING ? profile : previousCamera.aiming(),
                previousCamera.smoothing(),
                previousCamera.temporaryFirstPersonInTightSpace()),
            previous.aiming(),
            previous.player(),
            previous.hud());
    updateConfig(updated);
    return updated;
  }

  public void setAiming(boolean aiming) {
    if (!session.isPerspectiveActive()
        || !config.enabled()
        || session.mode() == CameraMode.TEMP_FIRST_PERSON) {
      return;
    }
    session.setMode(aiming ? CameraMode.AIMING : CameraMode.NORMAL);
  }

  public void requestTemporaryFirstPerson(boolean requested) {
    if (!session.isPerspectiveActive() || !config.enabled()) {
      requested = false;
    }
    session.requestTemporaryFirstPerson(requested);
    if (!config.enabled() && session.isPerspectiveActive()) {
      session.setMode(CameraMode.BYPASS);
    }
  }
}
