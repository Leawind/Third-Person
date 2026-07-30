package io.github.leawind.thirdperson.internal.scheduler;

import io.github.leawind.thirdperson.internal.base.api.BaseParameters;
import io.github.leawind.thirdperson.internal.base.api.CameraProfile;
import io.github.leawind.thirdperson.internal.base.api.CameraSmoothingParameters;
import io.github.leawind.thirdperson.internal.base.api.PlayerRotationParameters;
import io.github.leawind.thirdperson.internal.base.api.ThirdPersonBase;
import io.github.leawind.thirdperson.internal.scheduler.aiming.AimingSettings;
import io.github.leawind.thirdperson.internal.scheduler.camera.CameraMode;
import io.github.leawind.thirdperson.internal.scheduler.camera.CameraProfileSlot;
import io.github.leawind.thirdperson.internal.scheduler.camera.CameraSettings;
import io.github.leawind.thirdperson.internal.scheduler.hud.HudSettings;
import io.github.leawind.thirdperson.internal.scheduler.player.PlayerSettings;
import java.util.Objects;

/// Owns configuration and projects dynamic game state into instantaneous base parameters.
public final class SchedulerRuntime {
  private static final SchedulerRuntime INSTANCE = new SchedulerRuntime();

  private final SchedulerSession session = new SchedulerSession();
  private final CameraSettings cameraSettings = new CameraSettings();
  private final AimingSettings aimingSettings = new AimingSettings();
  private final PlayerSettings playerSettings = new PlayerSettings();
  private final HudSettings hudSettings = new HudSettings();
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
    base().applyParameters(
        new BaseParameters(
            cameraProfile(flyingOrSwimming),
            cameraSmoothing(flyingOrSwimming),
            playerSettings.raycastOrigin(),
            Objects.requireNonNull(playerRotation, "playerRotation")));
  }

  public CameraProfile updateCameraProfile(CameraProfileSlot slot, CameraProfile profile) {
    Objects.requireNonNull(slot, "slot");
    Objects.requireNonNull(profile, "profile");
    cameraSettings.setProfile(slot, profile);
    return profile;
  }
}
