package io.github.leawind.thirdperson.internal.core.movement;

import java.util.OptionalDouble;

/// Minecraft-independent conversion from camera-relative input to a world-facing yaw.
public final class MovementDirection {
  private static final double MIN_INPUT_LENGTH_SQUARED = 1.0e-7;

  private MovementDirection() {}

  public static boolean hasDirectionalImpulse(
      double leftImpulse, double forwardImpulse, double minimumMagnitude) {
    if (!Double.isFinite(leftImpulse)
        || !Double.isFinite(forwardImpulse)
        || !Double.isFinite(minimumMagnitude)
        || minimumMagnitude < 0.0) {
      return false;
    }
    double lengthSquared =
        leftImpulse * leftImpulse + forwardImpulse * forwardImpulse;
    return Double.isFinite(lengthSquared)
        && lengthSquared > 0.0
        && lengthSquared >= minimumMagnitude * minimumMagnitude;
  }

  public static OptionalDouble facingYawDegrees(
      double leftImpulse, double forwardImpulse, double cameraYawDegrees) {
    if (!Double.isFinite(leftImpulse)
        || !Double.isFinite(forwardImpulse)
        || !Double.isFinite(cameraYawDegrees)) {
      return OptionalDouble.empty();
    }
    double lengthSquared =
        leftImpulse * leftImpulse + forwardImpulse * forwardImpulse;
    if (lengthSquared < MIN_INPUT_LENGTH_SQUARED) {
      return OptionalDouble.empty();
    }

    double yawRadians = Math.toRadians(cameraYawDegrees);
    double sin = Math.sin(yawRadians);
    double cos = Math.cos(yawRadians);
    double worldX = leftImpulse * cos - forwardImpulse * sin;
    double worldZ = forwardImpulse * cos + leftImpulse * sin;
    double facingYaw = Math.toDegrees(Math.atan2(-worldX, worldZ));
    return Double.isFinite(facingYaw)
        ? OptionalDouble.of(facingYaw)
        : OptionalDouble.empty();
  }
}
