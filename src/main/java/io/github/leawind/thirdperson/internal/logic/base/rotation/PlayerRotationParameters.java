package io.github.leawind.thirdperson.internal.logic.base.rotation;

import java.util.Objects;
import java.util.Optional;

/// One scheduling-layer instruction for the base player-rotation pipeline.
public record PlayerRotationParameters(
    PlayerRotationMode mode,
    Optional<LookRotation> customRotation,
    double halfLifeSeconds,
    PlayerRotationSmoothing smoothing,
    boolean threeDimensionalMovement) {
  public PlayerRotationParameters {
    Objects.requireNonNull(mode, "mode");
    customRotation = Objects.requireNonNull(customRotation, "customRotation");
    Objects.requireNonNull(smoothing, "smoothing");
    if (!Double.isFinite(halfLifeSeconds) || halfLifeSeconds < 0.0) {
      throw new IllegalArgumentException("Rotation half-life must be finite and non-negative");
    }
    if (smoothing == PlayerRotationSmoothing.IMMEDIATE && halfLifeSeconds != 0.0) {
      throw new IllegalArgumentException("Immediate rotation cannot have a non-zero half-life");
    }
  }

  public static PlayerRotationParameters custom(
      Optional<LookRotation> rotation, double halfLifeSeconds, PlayerRotationSmoothing smoothing) {
    return new PlayerRotationParameters(
        PlayerRotationMode.CUSTOM, rotation, halfLifeSeconds, smoothing, false);
  }

  public static PlayerRotationParameters of(
      PlayerRotationMode mode, double halfLifeSeconds, PlayerRotationSmoothing smoothing) {
    if (Objects.requireNonNull(mode, "mode") == PlayerRotationMode.CUSTOM) {
      throw new IllegalArgumentException("Custom rotation requires an explicit optional target");
    }
    return new PlayerRotationParameters(mode, Optional.empty(), halfLifeSeconds, smoothing, false);
  }

  public PlayerRotationParameters withThreeDimensionalMovement(boolean value) {
    return new PlayerRotationParameters(mode, customRotation, halfLifeSeconds, smoothing, value);
  }
}
