package io.github.leawind.thirdperson.internal.core.movement;

import io.github.leawind.thirdperson.internal.core.aiming.LookRotation;
import java.util.Optional;
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

  public static Optional<LookRotation> facingRotation(
      double leftImpulse,
      double forwardImpulse,
      double cameraYawDegrees,
      double cameraPitchDegrees) {
    if (!Double.isFinite(leftImpulse)
        || !Double.isFinite(forwardImpulse)
        || !Double.isFinite(cameraYawDegrees)
        || !Double.isFinite(cameraPitchDegrees)) {
      return Optional.empty();
    }
    double lengthSquared = leftImpulse * leftImpulse + forwardImpulse * forwardImpulse;
    if (!Double.isFinite(lengthSquared) || lengthSquared < MIN_INPUT_LENGTH_SQUARED) {
      return Optional.empty();
    }

    double yawRadians = Math.toRadians(cameraYawDegrees);
    double pitchRadians = Math.toRadians(cameraPitchDegrees);
    double cosPitch = Math.cos(pitchRadians);
    double worldX =
        leftImpulse * Math.cos(yawRadians)
            - forwardImpulse * Math.sin(yawRadians) * cosPitch;
    double worldY = -forwardImpulse * Math.sin(pitchRadians);
    double worldZ =
        leftImpulse * Math.sin(yawRadians)
            + forwardImpulse * Math.cos(yawRadians) * cosPitch;
    double horizontalLength = Math.hypot(worldX, worldZ);
    double yaw = Math.toDegrees(Math.atan2(-worldX, worldZ));
    double pitch = Math.toDegrees(Math.atan2(-worldY, horizontalLength));
    if (!Double.isFinite(yaw) || !Double.isFinite(pitch)) {
      return Optional.empty();
    }
    return Optional.of(new LookRotation((float) yaw, (float) pitch));
  }
}
