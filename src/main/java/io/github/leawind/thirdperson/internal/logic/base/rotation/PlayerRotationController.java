package io.github.leawind.thirdperson.internal.logic.base.rotation;

import io.github.leawind.thirdperson.internal.logic.base.math.ExponentialSmoothing;
import java.util.Objects;
import java.util.Optional;

/// Maintains a smooth logical player rotation while taking the shortest path around yaw wraparound.
public final class PlayerRotationController {
  private boolean initialized;
  private float previousYawDegrees;
  private float previousPitchDegrees;
  private float yawDegrees;
  private float pitchDegrees;
  private PlayerRotationParameters parameters;

  public LookRotation update(
      LookRotation current,
      LookRotation target,
      double deltaSeconds,
      PlayerRotationParameters parameters) {
    Objects.requireNonNull(current, "current");
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(parameters, "parameters");
    if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0.0) {
      throw new IllegalArgumentException("Rotation delta must be finite and non-negative");
    }
    if (!initialized) {
      yawDegrees = current.yawDegrees();
      pitchDegrees = current.pitchDegrees();
      previousYawDegrees = yawDegrees;
      previousPitchDegrees = pitchDegrees;
      initialized = true;
    }

    previousYawDegrees = yawDegrees;
    previousPitchDegrees = pitchDegrees;
    this.parameters = parameters;
    if (parameters.smoothing() == PlayerRotationSmoothing.FRAME_EXPONENTIAL) {
      return new LookRotation(yawDegrees, pitchDegrees);
    }
    double alpha =
        parameters.smoothing() == PlayerRotationSmoothing.IMMEDIATE
            ? 1.0
            : ExponentialSmoothing.alpha(deltaSeconds, parameters.halfLifeSeconds());
    yawDegrees =
        wrapDegrees(
            yawDegrees + (float) (shortestDegrees(target.yawDegrees() - yawDegrees) * alpha));
    pitchDegrees =
        clampPitch(
            pitchDegrees + (float) ((target.pitchDegrees() - pitchDegrees) * alpha));
    return new LookRotation(yawDegrees, pitchDegrees);
  }

  /// Advances strategies whose exponential smoothing is evaluated at render-frame frequency.
  public LookRotation updateFrame(LookRotation target, double deltaSeconds) {
    Objects.requireNonNull(target, "target");
    if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0.0) {
      throw new IllegalArgumentException("Rotation frame delta must be finite and non-negative");
    }
    if (!initialized
        || parameters == null
        || parameters.smoothing() != PlayerRotationSmoothing.FRAME_EXPONENTIAL) {
      throw new IllegalStateException("Frame-exponential rotation has not been initialized");
    }
    double alpha = ExponentialSmoothing.alpha(deltaSeconds, parameters.halfLifeSeconds());
    yawDegrees =
        wrapDegrees(
            yawDegrees + (float) (shortestDegrees(target.yawDegrees() - yawDegrees) * alpha));
    pitchDegrees =
        clampPitch(
            pitchDegrees + (float) ((target.pitchDegrees() - pitchDegrees) * alpha));
    return new LookRotation(yawDegrees, pitchDegrees);
  }

  /// Samples the logical rotation between the previous and current client-tick endpoints.
  public Optional<LookRotation> sample(double partialTick) {
    if (!Double.isFinite(partialTick)) {
      throw new IllegalArgumentException("Rotation partial tick must be finite");
    }
    if (!initialized) {
      return Optional.empty();
    }
    double interpolation = Math.max(0.0, Math.min(1.0, partialTick));
    float yaw =
        wrapDegrees(
            previousYawDegrees
                + (float)
                    (shortestDegrees(yawDegrees - previousYawDegrees) * interpolation));
    float pitch =
        clampPitch(
            previousPitchDegrees
                + (float) ((pitchDegrees - previousPitchDegrees) * interpolation));
    return Optional.of(new LookRotation(yaw, pitch));
  }

  public Optional<PlayerRotationParameters> parameters() {
    return Optional.ofNullable(parameters);
  }

  public void reset() {
    initialized = false;
    previousYawDegrees = 0.0f;
    previousPitchDegrees = 0.0f;
    yawDegrees = 0.0f;
    pitchDegrees = 0.0f;
    parameters = null;
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
