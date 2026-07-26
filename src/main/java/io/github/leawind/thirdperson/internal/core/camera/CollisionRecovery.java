package io.github.leawind.thirdperson.internal.core.camera;

import io.github.leawind.thirdperson.internal.core.math.ExponentialSmoothing;
import io.github.leawind.thirdperson.internal.core.math.FiniteMath;
import java.util.Objects;
import java.util.Optional;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Contracts immediately after collision and expands with a fixed internal half-life.
public final class CollisionRecovery {
  private static final double RECOVERY_HALF_LIFE_SECONDS = 0.08;
  private static final double MIN_DIRECTION_LENGTH = 1.0e-9;

  private boolean initialized;
  private double distance;

  public Optional<Vector3d> resolve(
      Vector3dc pivot, Vector3dc collisionResolvedPosition, double deltaSeconds) {
    Objects.requireNonNull(pivot, "pivot");
    Objects.requireNonNull(collisionResolvedPosition, "collisionResolvedPosition");
    if (!FiniteMath.isFinite(pivot)
        || !FiniteMath.isFinite(collisionResolvedPosition)
        || !Double.isFinite(deltaSeconds)
        || deltaSeconds < 0.0) {
      return Optional.empty();
    }

    var direction = new Vector3d(collisionResolvedPosition).sub(pivot);
    double availableDistance = direction.length();
    if (!Double.isFinite(availableDistance)) {
      return Optional.empty();
    }
    if (availableDistance <= MIN_DIRECTION_LENGTH) {
      initialized = true;
      distance = 0.0;
      return Optional.of(new Vector3d(pivot));
    }
    direction.div(availableDistance);

    if (!initialized || availableDistance <= distance) {
      distance = availableDistance;
      initialized = true;
    } else {
      distance =
          ExponentialSmoothing.interpolate(
              distance,
              availableDistance,
              Math.min(deltaSeconds, 0.1),
              RECOVERY_HALF_LIFE_SECONDS);
    }
    return Optional.of(new Vector3d(pivot).fma(distance, direction));
  }

  public void reset() {
    initialized = false;
    distance = 0.0;
  }
}
