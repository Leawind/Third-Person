package io.github.leawind.thirdperson.internal.core.player;

import java.util.Objects;

/// Selected rotation target and the half-life used to approach it.
public record PlayerRotationDecision(
    PlayerRotationTarget target, double halfLifeSeconds, boolean immediate) {
  public PlayerRotationDecision {
    Objects.requireNonNull(target, "target");
    if (!Double.isFinite(halfLifeSeconds) || halfLifeSeconds < 0.0) {
      throw new IllegalArgumentException("Rotation half-life must be finite and non-negative");
    }
  }
}
