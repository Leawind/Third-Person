package io.github.leawind.thirdperson.internal.logic.scheduler;

import io.github.leawind.thirdperson.internal.logic.base.PlayerRotationSmoothing;
import java.util.Objects;

/// Selected rotation target, timing model, and the half-life used to approach it.
public record PlayerRotationDecision(
    PlayerRotationTarget target,
    double halfLifeSeconds,
    PlayerRotationSmoothing smoothing) {
  public PlayerRotationDecision {
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(smoothing, "smoothing");
    if (!Double.isFinite(halfLifeSeconds) || halfLifeSeconds < 0.0) {
      throw new IllegalArgumentException("Rotation half-life must be finite and non-negative");
    }
    if (smoothing == PlayerRotationSmoothing.IMMEDIATE && halfLifeSeconds != 0.0) {
      throw new IllegalArgumentException("Immediate rotation cannot have a non-zero half-life");
    }
  }

  public boolean immediate() {
    return smoothing == PlayerRotationSmoothing.IMMEDIATE;
  }
}
