package io.github.leawind.thirdperson.internal.logic.base;

import java.util.Objects;
import java.util.Optional;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Converts a world-space target into Minecraft yaw and pitch at the player-eye boundary.
public final class LookGeometry {
  private static final double MIN_DIRECTION_LENGTH_SQUARED = 1.0e-12;

  private LookGeometry() {}

  public static Optional<LookRotation> lookAt(Vector3dc origin, Vector3dc target) {
    Objects.requireNonNull(origin, "origin");
    Objects.requireNonNull(target, "target");
    if (!FiniteMath.isFinite(origin) || !FiniteMath.isFinite(target)) {
      return Optional.empty();
    }

    var direction = new Vector3d(target).sub(origin);
    double lengthSquared = direction.lengthSquared();
    if (!Double.isFinite(lengthSquared) || lengthSquared <= MIN_DIRECTION_LENGTH_SQUARED) {
      return Optional.empty();
    }
    double horizontalLength = Math.hypot(direction.x, direction.z);
    double yaw = Math.toDegrees(Math.atan2(-direction.x, direction.z));
    double pitch = Math.toDegrees(Math.atan2(-direction.y, horizontalLength));
    if (!Double.isFinite(yaw) || !Double.isFinite(pitch)) {
      return Optional.empty();
    }
    return Optional.of(new LookRotation((float) yaw, (float) pitch));
  }
}
