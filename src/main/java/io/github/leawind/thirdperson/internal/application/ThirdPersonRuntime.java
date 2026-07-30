package io.github.leawind.thirdperson.internal.application;

import io.github.leawind.thirdperson.internal.application.aiming.AimingSettings;
import io.github.leawind.thirdperson.internal.application.camera.CameraController;
import io.github.leawind.thirdperson.internal.application.camera.CameraFrameInput;
import io.github.leawind.thirdperson.internal.application.camera.CameraSettings;
import io.github.leawind.thirdperson.internal.application.hud.HudSettings;
import io.github.leawind.thirdperson.internal.application.player.PlayerSettings;
import io.github.leawind.thirdperson.internal.application.port.CameraCollisionPort;
import io.github.leawind.thirdperson.internal.core.camera.CameraMode;
import io.github.leawind.thirdperson.internal.core.camera.CameraPose;
import io.github.leawind.thirdperson.internal.core.camera.CameraProfile;
import io.github.leawind.thirdperson.internal.core.camera.CameraProfileSlot;
import io.github.leawind.thirdperson.internal.core.camera.CameraSmoothingParameters;
import java.util.Objects;
import java.util.Optional;

/// Process-wide owner of services and the current Minecraft-independent session state.
public final class ThirdPersonRuntime {
  private static final ThirdPersonRuntime INSTANCE = new ThirdPersonRuntime();

  private final ThirdPersonSession session = new ThirdPersonSession();
  private final CameraController cameraController = new CameraController(session);
  private final CameraSettings cameraSettings = new CameraSettings();
  private final AimingSettings aimingSettings = new AimingSettings();
  private final PlayerSettings playerSettings = new PlayerSettings();
  private final HudSettings hudSettings = new HudSettings();
  private boolean initialized;

  private ThirdPersonRuntime() {}

  public static ThirdPersonRuntime getInstance() {
    return INSTANCE;
  }

  public ThirdPersonSession session() {
    return session;
  }

  public CameraSettings cameraSettings() {
    return cameraSettings;
  }

  public AimingSettings aimingSettings() {
    return aimingSettings;
  }

  public PlayerSettings playerSettings() {
    return playerSettings;
  }

  public HudSettings hudSettings() {
    return hudSettings;
  }

  public boolean isCameraControlEnabled() {
    return session.isControllingCamera();
  }

  public CameraProfile cameraProfile(boolean centered) {
    CameraProfile profile =
        session.mode() == CameraMode.AIMING
            ? cameraSettings.aimingProfile()
            : cameraSettings.normalProfile();
    return centered ? profile.withCentered(true) : profile;
  }

  public CameraSmoothingParameters cameraSmoothing(boolean flyingOrSwimming) {
    var smoothing = cameraSettings.smoothing();
    var modeSmoothing =
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

  public Optional<CameraPose> updateCamera(CameraFrameInput frame, CameraCollisionPort collision) {
    Objects.requireNonNull(frame, "frame");
    Objects.requireNonNull(collision, "collision");
    return cameraController.update(
        frame,
        cameraProfile(frame.flyingOrSwimming()),
        cameraSmoothing(frame.flyingOrSwimming()),
        collision);
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

  public CameraProfile updateNormalCameraProfile(CameraProfile profile) {
    return updateCameraProfile(CameraProfileSlot.NORMAL, profile);
  }

  public CameraProfile updateCameraProfile(CameraProfileSlot slot, CameraProfile profile) {
    Objects.requireNonNull(slot, "slot");
    Objects.requireNonNull(profile, "profile");
    cameraSettings.setProfile(slot, profile);
    return profile;
  }

  public void setAiming(boolean aiming) {
    if (!session.isPerspectiveActive()) {
      return;
    }
    session.setMode(aiming ? CameraMode.AIMING : CameraMode.NORMAL);
  }
}
