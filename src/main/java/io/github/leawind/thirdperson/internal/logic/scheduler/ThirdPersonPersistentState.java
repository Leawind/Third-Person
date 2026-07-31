package io.github.leawind.thirdperson.internal.logic.scheduler;

import io.github.leawind.thirdperson.internal.logic.base.camera.CameraProfile;
import io.github.leawind.thirdperson.internal.logic.base.RaycastOrigin;
import java.util.List;
import java.util.Objects;

/// Serialized projection of runtime-owned state. This is not a live configuration object.
public record ThirdPersonPersistentState(
    int schemaVersion,
    CameraState camera,
    AimingState aiming,
    PlayerState player,
    HudState hud) {
  public static final int CURRENT_SCHEMA_VERSION = 2;

  public ThirdPersonPersistentState {
    if (schemaVersion != CURRENT_SCHEMA_VERSION) {
      throw new IllegalArgumentException("Unsupported state schema version: " + schemaVersion);
    }
    Objects.requireNonNull(camera, "camera");
    Objects.requireNonNull(aiming, "aiming");
    Objects.requireNonNull(player, "player");
    Objects.requireNonNull(hud, "hud");
  }

  public static ThirdPersonPersistentState defaults() {
    return new ThirdPersonPersistentState(
        CURRENT_SCHEMA_VERSION,
        new CameraState(
            CameraSettings.defaultNormalProfile(),
            CameraSettings.defaultAimingProfile(),
            CameraSettings.defaultSmoothing()),
        new AimingState(true, List.of(), List.of()),
        new PlayerState(
            ConfiguredPlayerRotationMode.AUTO,
            NormalPlayerRotationMode.INTEREST_POINT,
            true,
            true,
            RaycastOrigin.CAMERA),
        new HudState(ReticleMode.AUTO));
  }

  public static ThirdPersonPersistentState extract(SchedulerRuntime runtime) {
    Objects.requireNonNull(runtime, "runtime");
    var camera = runtime.cameraSettings();
    var aiming = runtime.aimingSettings();
    var player = runtime.playerSettings();
    return new ThirdPersonPersistentState(
        CURRENT_SCHEMA_VERSION,
        new CameraState(camera.normalProfile(), camera.aimingProfile(), camera.smoothing()),
        new AimingState(
            aiming.smartAiming(),
            aiming.holdToAimItemPatterns(),
            aiming.useToAimItemPatterns()),
        new PlayerState(
            player.rotationMode(),
            player.normalMode(),
            player.autoRotateInteracting(),
            player.doNotRotateWhenEating(),
            player.raycastOrigin()),
        new HudState(runtime.hudSettings().reticleMode()));
  }

  public void applyTo(SchedulerRuntime runtime) {
    Objects.requireNonNull(runtime, "runtime");
    runtime.cameraSettings().restore(camera.normal(), camera.aiming(), camera.smoothing());
    runtime
        .aimingSettings()
        .restore(
            aiming.smartAiming(),
            aiming.holdToAimItemPatterns(),
            aiming.useToAimItemPatterns());
    runtime
        .playerSettings()
        .restore(
            player.rotationMode(),
            player.normalMode(),
            player.autoRotateInteracting(),
            player.doNotRotateWhenEating(),
            player.raycastOrigin());
    runtime.hudSettings().setReticleMode(hud.reticle());
  }

  public record CameraState(
      CameraProfile normal, CameraProfile aiming, CameraSmoothing smoothing) {
    public CameraState {
      Objects.requireNonNull(normal, "normal");
      Objects.requireNonNull(aiming, "aiming");
      Objects.requireNonNull(smoothing, "smoothing");
    }
  }

  public record AimingState(
      boolean smartAiming, List<String> holdToAimItemPatterns, List<String> useToAimItemPatterns) {
    private static final int MAX_PATTERNS_PER_LIST = 1024;

    public AimingState {
      holdToAimItemPatterns = copyPatterns(holdToAimItemPatterns, "holdToAimItemPatterns");
      useToAimItemPatterns = copyPatterns(useToAimItemPatterns, "useToAimItemPatterns");
    }

    private static List<String> copyPatterns(List<String> patterns, String name) {
      Objects.requireNonNull(patterns, name);
      if (patterns.size() > MAX_PATTERNS_PER_LIST || patterns.stream().anyMatch(Objects::isNull)) {
        throw new IllegalArgumentException(name + " must contain at most 1024 non-null values");
      }
      return List.copyOf(patterns);
    }
  }

  public record PlayerState(
      ConfiguredPlayerRotationMode rotationMode,
      NormalPlayerRotationMode normalMode,
      boolean autoRotateInteracting,
      boolean doNotRotateWhenEating,
      RaycastOrigin raycastOrigin) {
    public PlayerState {
      Objects.requireNonNull(rotationMode, "rotationMode");
      Objects.requireNonNull(normalMode, "normalMode");
      Objects.requireNonNull(raycastOrigin, "raycastOrigin");
    }
  }

  public record HudState(ReticleMode reticle) {
    public HudState {
      Objects.requireNonNull(reticle, "reticle");
    }
  }
}
