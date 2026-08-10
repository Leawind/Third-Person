package io.github.leawind.thirdperson.internal.core.schedule;

import io.github.leawind.thirdperson.internal.core.base.BaseParameters;
import io.github.leawind.thirdperson.internal.core.base.RaycastOrigin;
import io.github.leawind.thirdperson.internal.core.base.ThirdPersonBase;
import io.github.leawind.thirdperson.internal.core.base.camera.CameraProfile;
import io.github.leawind.thirdperson.internal.core.base.camera.CameraSmoothingParameters;
import io.github.leawind.thirdperson.internal.core.base.pivot.CameraPivotSmoothing;
import io.github.leawind.thirdperson.internal.core.base.rotation.PlayerRotationParameters;
import io.github.leawind.thirdperson.internal.core.schedule.aiming.AimingSettings;
import io.github.leawind.thirdperson.internal.core.schedule.aiming.CameraMode;
import io.github.leawind.thirdperson.internal.core.schedule.camera.CameraProfileSlot;
import io.github.leawind.thirdperson.internal.core.schedule.camera.CameraSettings;
import io.github.leawind.thirdperson.internal.core.schedule.hud.HudSettings;
import io.github.leawind.thirdperson.internal.core.schedule.rotation.PlayerSettings;
import io.github.leawind.thirdperson.internal.core.schedule.sound.SoundSettings;
import java.util.Objects;

/// Owns configuration and projects dynamic game state into instantaneous base parameters.
public final class SchedulerRuntime {
  private static final SchedulerRuntime INSTANCE = new SchedulerRuntime();

  private final SchedulerSession session = new SchedulerSession();
  private final CameraSettings cameraSettings = new CameraSettings();
  private final AimingSettings aimingSettings = new AimingSettings();
  private final PlayerSettings playerSettings = new PlayerSettings();
  private final HudSettings hudSettings = new HudSettings();
  private final SoundSettings soundSettings = new SoundSettings();
  private BaseParameters appliedParameters = BaseParameters.defaults();
  private ThirdPersonBase base;

  SchedulerRuntime() {}

  public static SchedulerRuntime getInstance() {
    return INSTANCE;
  }

  public boolean initialize(ThirdPersonBase base) {
    if (this.base != null) {
      return false;
    }
    this.base = Objects.requireNonNull(base, "base");
    return true;
  }

  public ThirdPersonBase base() {
    if (base == null) {
      throw new IllegalStateException("Scheduling layer has not been initialized");
    }
    return base;
  }

  public SchedulerSession session() {
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

  public SoundSettings soundSettings() {
    return soundSettings;
  }

  public boolean isAiming() {
    return session.mode() == CameraMode.AIMING;
  }

  public void setAiming(boolean aiming) {
    session.setAiming(aiming);
  }

  public CameraProfile cameraProfile(boolean centered) {
    CameraProfile profile =
        session.mode() == CameraMode.AIMING
            ? cameraSettings.aimingProfile()
            : cameraSettings.normalProfile();
    return centered ? profile.withCentered(true) : profile;
  }

  public CameraPivotSmoothing cameraPivotSmoothing(boolean flyingOrSwimming) {
    var smoothing = cameraSettings.smoothing();
    var modeSmoothing =
        session.mode() == CameraMode.AIMING ? smoothing.aiming() : smoothing.normal();
    double pivotPositionHalfLife =
        flyingOrSwimming
            ? smoothing.flyingPivotPositionHalfLife()
            : modeSmoothing.pivotPositionHalfLife();
    return new CameraPivotSmoothing(pivotPositionHalfLife);
  }

  public CameraSmoothingParameters cameraSmoothing() {
    var smoothing = cameraSettings.smoothing();
    var modeSmoothing =
        session.mode() == CameraMode.AIMING ? smoothing.aiming() : smoothing.normal();
    boolean adjusting = session.cameraAdjustmentController().isAdjusting();
    double offsetHalfLife =
        adjusting ? smoothing.adjustingOffsetHalfLife() : modeSmoothing.offsetHalfLife();
    double distanceHalfLife =
        adjusting ? smoothing.adjustingDistanceHalfLife() : modeSmoothing.distanceHalfLife();
    return new CameraSmoothingParameters(
        smoothing.rotationHalfLife(),
        offsetHalfLife,
        distanceHalfLife,
        modeSmoothing.fovHalfLife());
  }

  public void applyParameters(
      boolean flyingOrSwimming,
      boolean cameraRaycastOriginAllowed,
      PlayerRotationParameters playerRotation) {
    applyParameters(
        new BaseParameters(
            cameraProfile(flyingOrSwimming),
            cameraPivotSmoothing(flyingOrSwimming),
            cameraSmoothing(),
            cameraRaycastOriginAllowed
                ? playerSettings.raycastOrigin()
                : RaycastOrigin.PLAYER_EYE,
            soundSettings.centerCameraEntitySounds(),
            Objects.requireNonNull(playerRotation, "playerRotation")));
  }

  public BaseParameters appliedParameters() {
    return appliedParameters;
  }

  public void applyPlayerRotation(PlayerRotationParameters playerRotation) {
    applyParameters(
        appliedParameters.withPlayerRotation(
            Objects.requireNonNull(playerRotation, "playerRotation")));
  }

  private void applyParameters(BaseParameters parameters) {
    appliedParameters = Objects.requireNonNull(parameters, "parameters");
    base().applyParameters(parameters);
  }

  public void updateCameraProfile(CameraProfileSlot slot, CameraProfile profile) {
    Objects.requireNonNull(slot, "slot");
    Objects.requireNonNull(profile, "profile");
    cameraSettings.setProfile(slot, profile);
  }
}
