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
              new CameraProfile(4.0, -0.18, 0.12, 0.24, 1.0, false),
              new CameraProfile(2.4, -0.30, 0.16, 0.48, 0.9, false),
              new SmoothingSettings(
                  0.0,
                  0.07,
                  0.05,
                  0.05,
                  new ModeSmoothing(0.064, 0.08, 0.06, 0.08, 0.0),
                  new ModeSmoothing(0.02, 0.025, 0.025, 0.08, 0.0)),
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
      double distanceHalfLife,
      double fovHalfLife) {
    public ModeSmoothing {
      requireHalfLife(horizontalPivotHalfLife);
      requireHalfLife(verticalPivotHalfLife);
      requireHalfLife(offsetHalfLife);
      requireHalfLife(distanceHalfLife);
      requireHalfLife(fovHalfLife);
    }
  }

  public record CameraProfile(
      double distance,
      double offsetX,
      double offsetY,
      double centeredOffsetY,
      double fovMultiplier,
      boolean centered) {
    public CameraProfile {
      if (!Double.isFinite(distance)
          || !Double.isFinite(offsetX)
          || !Double.isFinite(offsetY)
          || !Double.isFinite(centeredOffsetY)
          || !Double.isFinite(fovMultiplier)
          || distance < 0.0
          || distance > 16.0
          || Math.abs(offsetX) > 1.0
          || Math.abs(offsetY) > 1.0
          || Math.abs(centeredOffsetY) > 1.0
          || fovMultiplier < 0.25
          || fovMultiplier > 2.0) {
        throw new IllegalArgumentException("Invalid camera profile");
      }
    }

    public CameraParameters cameraParameters() {
      return centered
          ? new CameraParameters(distance, 0.0, centeredOffsetY)
          : new CameraParameters(distance, offsetX, offsetY);
    }

    public CameraProfile withCentered(boolean centered) {
      return this.centered == centered
          ? this
          : new CameraProfile(
              distance, offsetX, offsetY, centeredOffsetY, fovMultiplier, centered);
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
    if (!Double.isFinite(value) || value < 0.0 || value > 0.2) {
      throw new IllegalArgumentException("Smoothing half-life must be within [0, 0.2]");
    }
  }
}
