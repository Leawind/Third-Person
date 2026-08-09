package io.github.leawind.thirdperson.internal.core.base.rotation;

/// Pure angle helpers shared by player-rotation strategy integrations.
public final class PlayerRotationGeometry {
  private PlayerRotationGeometry() {}

  public static float clampYawAround(float yaw, float centerYaw, float maxDifferenceDegrees) {
    if (!Float.isFinite(yaw)
        || !Float.isFinite(centerYaw)
        || !Float.isFinite(maxDifferenceDegrees)
        || maxDifferenceDegrees < 0.0f
        || maxDifferenceDegrees > 180.0f) {
      throw new IllegalArgumentException("Invalid yaw clamp arguments");
    }
    float difference = shortestDifference(yaw, centerYaw);
    float clamped = Math.max(-maxDifferenceDegrees, Math.min(maxDifferenceDegrees, difference));
    return wrapDegrees(centerYaw + clamped);
  }

  public static float shortestDifference(float yaw, float referenceYaw) {
    return wrapDegrees(yaw - referenceYaw);
  }

  public static float clampPitch(float value) {
    return Math.max(-90.0f, Math.min(90.0f, value));
  }

  public static float wrapDegrees(float value) {
    float wrapped = value % 360.0f;
    if (wrapped >= 180.0f) {
      wrapped -= 360.0f;
    } else if (wrapped < -180.0f) {
      wrapped += 360.0f;
    }
    return wrapped;
  }
}
