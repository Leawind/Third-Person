package io.github.leawind.thirdperson.internal.logic.scheduler;

import io.github.leawind.thirdperson.internal.logic.base.BaseParameters;
import io.github.leawind.thirdperson.internal.logic.base.camera.CameraProfile;
import io.github.leawind.thirdperson.internal.logic.base.camera.CameraSmoothingParameters;
import io.github.leawind.thirdperson.internal.logic.base.rotation.PlayerRotationParameters;
import io.github.leawind.thirdperson.internal.logic.base.ThirdPersonBase;
import java.util.Objects;

/// Owns configuration and projects dynamic game state into instantaneous base parameters.
public final class SchedulerRuntime {
  private static final SchedulerRuntime INSTANCE = new SchedulerRuntime();

  private final SchedulerSession session = new SchedulerSession();
  private final CameraSettings cameraSettings = new CameraSettings();
  private final AimingSettings aimingSettings = new AimingSettings();
  private final PlayerSettings playerSettings = new PlayerSettings();
  private final HudSettings hudSettings = new HudSettings();
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

  public void applyParameters(
      boolean flyingOrSwimming, PlayerRotationParameters playerRotation) {
    applyParameters(
        new BaseParameters(
            cameraProfile(flyingOrSwimming),
            cameraSmoothing(flyingOrSwimming),
            playerSettings.raycastOrigin(),
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

  public CameraProfile updateCameraProfile(CameraProfileSlot slot, CameraProfile profile) {
    Objects.requireNonNull(slot, "slot");
    Objects.requireNonNull(profile, "profile");
    cameraSettings.setProfile(slot, profile);
    return profile;
  }
}
