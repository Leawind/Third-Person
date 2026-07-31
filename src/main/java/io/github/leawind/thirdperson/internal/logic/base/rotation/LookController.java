package io.github.leawind.thirdperson.internal.logic.base.rotation;

import org.joml.Quaternionf;

/// Stores two-axis look input and exposes it as a Perspective API-convention quaternion.
public final class LookController {
  private static final float TURN_SCALE = 0.15f;

  private boolean initialized;
  private float pitchDegrees;
  private float yawDegrees;

  public boolean isInitialized() {
    return initialized;
  }

  public void initialize(float pitchDegrees, float yawDegrees) {
    if (!Float.isFinite(pitchDegrees) || !Float.isFinite(yawDegrees)) {
      reset();
      return;
    }
    this.pitchDegrees = clampPitch(pitchDegrees);
    this.yawDegrees = wrapDegrees(yawDegrees);
    initialized = true;
  }

  /// Applies the raw arguments received by `Entity.turn`.
  ///
  /// Returns false when the controller is not initialized or the input is invalid, allowing the
  /// caller to preserve vanilla handling.
  public boolean turn(double rawYaw, double rawPitch) {
    if (!initialized || !Double.isFinite(rawYaw) || !Double.isFinite(rawPitch)) {
      return false;
    }
    yawDegrees = wrapDegrees(yawDegrees + (float) rawYaw * TURN_SCALE);
    pitchDegrees = clampPitch(pitchDegrees + (float) rawPitch * TURN_SCALE);
    return true;
  }

  public boolean copyRotation(Quaternionf destination) {
    if (!initialized) {
      return false;
    }
    destination.rotationYXZ(
        (float) Math.toRadians(-yawDegrees),
        (float) Math.toRadians(pitchDegrees),
        0.0f);
    return true;
  }

  public float pitchDegrees() {
    return pitchDegrees;
  }

  public float yawDegrees() {
    return yawDegrees;
  }

  public void reset() {
    initialized = false;
    pitchDegrees = 0.0f;
    yawDegrees = 0.0f;
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
