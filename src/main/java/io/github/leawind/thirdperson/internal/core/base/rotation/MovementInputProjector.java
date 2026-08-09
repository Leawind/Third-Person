package io.github.leawind.thirdperson.internal.core.base.rotation;

import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import io.github.leawind.thirdperson.internal.core.base.math.FiniteMath;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/// Solves two movement impulses only when the destination basis can represent the desired vector.
public final class MovementInputProjector {
  private static final double ABSOLUTE_RESIDUAL_TOLERANCE = 1.0e-5;
  private static final double RELATIVE_RESIDUAL_TOLERANCE = 1.0e-4;

  private MovementInputProjector() {}

  public static Optional<LocalInput> project(
      Vector3fc desiredWorld, Quaternionfc worldFromInputFrame) {
    Objects.requireNonNull(desiredWorld, "desiredWorld");
    Objects.requireNonNull(worldFromInputFrame, "worldFromInputFrame");
    if (!FiniteMath.isFinite(desiredWorld) || !FiniteMath.isFinite(worldFromInputFrame)) {
      return Optional.empty();
    }

    var orientation = new Quaternionf(worldFromInputFrame).normalize();
    var left = PerspectiveMath.getLeft(orientation, new Vector3f());
    var forward = PerspectiveMath.getForward(orientation, new Vector3f());
    float leftImpulse = desiredWorld.dot(left);
    float forwardImpulse = desiredWorld.dot(forward);
    var reconstructed =
        new Vector3f(left).mul(leftImpulse).fma(forwardImpulse, forward);
    double residual = reconstructed.distance(desiredWorld);
    double tolerance =
        Math.max(
            ABSOLUTE_RESIDUAL_TOLERANCE,
            Math.sqrt(desiredWorld.lengthSquared()) * RELATIVE_RESIDUAL_TOLERANCE);
    if (!Float.isFinite(leftImpulse)
        || !Float.isFinite(forwardImpulse)
        || !Double.isFinite(residual)
        || residual > tolerance) {
      return Optional.empty();
    }
    return Optional.of(new LocalInput(leftImpulse, forwardImpulse));
  }

  public record LocalInput(float leftImpulse, float forwardImpulse) {}
}
