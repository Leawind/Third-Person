package io.github.leawind.thirdperson.internal.core.config;

import io.github.leawind.thirdperson.internal.core.camera.CameraParameters;
import java.util.Objects;

/// Immutable, validated runtime configuration.
public record ThirdPersonConfig(
    int schemaVersion,
    boolean enabled,
    CameraSettings camera,
    AimingSettings aiming,
    PlayerSettings player,
    HudSettings hud) {
  public static final int CURRENT_SCHEMA_VERSION = 2;
  private static final ThirdPersonConfig DEFAULTS =
      new ThirdPersonConfig(
          CURRENT_SCHEMA_VERSION,
          true,
          new CameraSettings(
              new CameraProfile(4.0, -0.18, 0.12, 1.0),
              new CameraProfile(2.4, -0.30, 0.16, 0.9),
              new SmoothingSettings(
                  0.0,
                  0.45,
                  0.04,
                  0.08,
                  new ModeSmoothing(0.25, 0.20, 0.08, 0.72),
                  new ModeSmoothing(0.05, 0.05, 0.03, 0.04)),
              true),
          new AimingSettings(true),
          new PlayerSettings(PlayerRotationMode.AUTO),
          new HudSettings(ReticleMode.AUTO));

  public ThirdPersonConfig {
    if (schemaVersion != CURRENT_SCHEMA_VERSION) {
      throw new IllegalArgumentException("Unsupported config schema version: " + schemaVersion);
    }
    Objects.requireNonNull(camera, "camera");
    Objects.requireNonNull(aiming, "aiming");
    Objects.requireNonNull(player, "player");
    Objects.requireNonNull(hud, "hud");
  }

  public static ThirdPersonConfig defaults() {
    return DEFAULTS;
  }

  public record CameraSettings(
      CameraProfile normal,
      CameraProfile aiming,
      SmoothingSettings smoothing,
      boolean temporaryFirstPersonInTightSpace) {
    public CameraSettings {
      Objects.requireNonNull(normal, "normal");
      Objects.requireNonNull(aiming, "aiming");
      Objects.requireNonNull(smoothing, "smoothing");
    }
  }

  public record SmoothingSettings(
      double rotationHalfLife,
      double flyingPivotHalfLife,
      double adjustingOffsetHalfLife,
      double adjustingDistanceHalfLife,
      ModeSmoothing normal,
      ModeSmoothing aiming) {
    public SmoothingSettings {
      requireHalfLife(rotationHalfLife);
      requireHalfLife(flyingPivotHalfLife);
      requireHalfLife(adjustingOffsetHalfLife);
      requireHalfLife(adjustingDistanceHalfLife);
      Objects.requireNonNull(normal, "normal");
      Objects.requireNonNull(aiming, "aiming");
    }
  }

  public record ModeSmoothing(
      double horizontalPivotHalfLife,
      double verticalPivotHalfLife,
      double offsetHalfLife,
      double distanceHalfLife) {
    public ModeSmoothing {
      requireHalfLife(horizontalPivotHalfLife);
      requireHalfLife(verticalPivotHalfLife);
      requireHalfLife(offsetHalfLife);
      requireHalfLife(distanceHalfLife);
    }
  }

  public record CameraProfile(
      double distance, double offsetX, double offsetY, double fovMultiplier) {
    public CameraProfile {
      if (!Double.isFinite(distance)
          || !Double.isFinite(offsetX)
          || !Double.isFinite(offsetY)
          || !Double.isFinite(fovMultiplier)
          || distance < 0.0
          || distance > 16.0
          || Math.abs(offsetX) > 1.0
          || Math.abs(offsetY) > 1.0
          || fovMultiplier < 0.25
          || fovMultiplier > 2.0) {
        throw new IllegalArgumentException("Invalid camera profile");
      }
    }

    public CameraParameters cameraParameters() {
      return new CameraParameters(distance, offsetX, offsetY);
    }

    public CameraProfile centered() {
      return offsetX == 0.0
          ? this
          : new CameraProfile(distance, 0.0, offsetY, fovMultiplier);
    }
  }

  public record AimingSettings(boolean smartAiming) {}

  public record PlayerSettings(PlayerRotationMode rotationMode) {
    public PlayerSettings {
      Objects.requireNonNull(rotationMode, "rotationMode");
    }
  }

  public record HudSettings(ReticleMode reticle) {
    public HudSettings {
      Objects.requireNonNull(reticle, "reticle");
    }
  }

  private static void requireHalfLife(double value) {
    if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
      throw new IllegalArgumentException("Smoothing half-life must be within [0, 1]");
    }
  }
}
