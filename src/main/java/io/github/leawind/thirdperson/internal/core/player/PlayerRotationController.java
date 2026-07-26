package io.github.leawind.thirdperson.internal.core.player;

import io.github.leawind.thirdperson.internal.core.aiming.LookRotation;
import io.github.leawind.thirdperson.internal.core.math.ExponentialSmoothing;
import java.util.Objects;

/// Maintains a smooth logical player rotation while taking the shortest path around yaw wraparound.
public final class PlayerRotationController {
  private boolean initialized;
  private float yawDegrees;
  private float pitchDegrees;

  public LookRotation update(
      LookRotation current,
      LookRotation target,
      double deltaSeconds,
      PlayerRotationDecision decision) {
    Objects.requireNonNull(current, "current");
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(decision, "decision");
    if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0.0) {
      throw new IllegalArgumentException("Rotation delta must be finite and non-negative");
    }
    if (!initialized) {
      yawDegrees = current.yawDegrees();
      pitchDegrees = current.pitchDegrees();
      initialized = true;
    }

    double alpha =
        decision.immediate()
            ? 1.0
            : ExponentialSmoothing.alpha(deltaSeconds, decision.halfLifeSeconds());
    yawDegrees =
        wrapDegrees(
            yawDegrees + (float) (shortestDegrees(target.yawDegrees() - yawDegrees) * alpha));
    pitchDegrees =
        clampPitch(
            pitchDegrees + (float) ((target.pitchDegrees() - pitchDegrees) * alpha));
    return new LookRotation(yawDegrees, pitchDegrees);
  }

  public void reset() {
    initialized = false;
    yawDegrees = 0.0f;
    pitchDegrees = 0.0f;
  }

  private static float shortestDegrees(float degrees) {
    return wrapDegrees(degrees);
  }

  private static float clampPitch(float value) {
    return Math.max(-90.0f, Math.min(90.0f, value));
  }

  private static float wrapDegrees(float value) {
    float wrapped = value % 360.0f;
    if (wrapped >= 180.0f) {
      wrapped -= 360.0f;
    } else if (wrapped < -180.0f) {
      wrapped += 360.0f;
    }
    return wrapped;
  }
}
