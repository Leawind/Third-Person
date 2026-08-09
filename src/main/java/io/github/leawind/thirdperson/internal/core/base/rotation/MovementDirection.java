package io.github.leawind.thirdperson.internal.core.base.rotation;

import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import io.github.leawind.thirdperson.internal.core.base.math.FiniteMath;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/// Minecraft-independent movement directions derived from explicit camera reference frames.
public final class MovementDirection {
  private static final double MIN_INPUT_LENGTH_SQUARED = 1.0e-10;

  private MovementDirection() {}

  public static boolean hasDirectionalImpulse(
      double leftImpulse, double forwardImpulse, double minimumMagnitude) {
    if (!Double.isFinite(leftImpulse)
        || !Double.isFinite(forwardImpulse)
        || !Double.isFinite(minimumMagnitude)
        || minimumMagnitude < 0.0) {
      return false;
    }
    double lengthSquared = leftImpulse * leftImpulse + forwardImpulse * forwardImpulse;
    return Double.isFinite(lengthSquared)
        && lengthSquared > 0.0
        && lengthSquared >= minimumMagnitude * minimumMagnitude;
  }

  public static Optional<Vector3f> pivotPlaneWorld(
      float leftImpulse,
      float forwardImpulse,
      float localYawDegrees,
      Quaternionfc worldFromPivot) {
    Objects.requireNonNull(worldFromPivot, "worldFromPivot");
    if (!Float.isFinite(leftImpulse)
        || !Float.isFinite(forwardImpulse)
        || !Float.isFinite(localYawDegrees)
        || !FiniteMath.isFinite(worldFromPivot)) {
      return Optional.empty();
    }
    var pivotFromHeading =
        PerspectiveMath.eulerDegToQuat(0.0f, localYawDegrees, 0.0f, new Quaternionf());
    var worldFromHeading = new Quaternionf(worldFromPivot).normalize().mul(pivotFromHeading);
    return combineInput(leftImpulse, forwardImpulse, worldFromHeading);
  }

  public static Optional<Vector3f> cameraSpaceWorld(
      float leftImpulse, float forwardImpulse, Quaternionfc worldFromCamera) {
    Objects.requireNonNull(worldFromCamera, "worldFromCamera");
    if (!Float.isFinite(leftImpulse)
        || !Float.isFinite(forwardImpulse)
        || !FiniteMath.isFinite(worldFromCamera)) {
      return Optional.empty();
    }
    return combineInput(
        leftImpulse, forwardImpulse, new Quaternionf(worldFromCamera).normalize());
  }

  public static Optional<LookRotation> facingRotation(Vector3fc directionWorld) {
    Objects.requireNonNull(directionWorld, "directionWorld");
    double lengthSquared = directionWorld.lengthSquared();
    if (!FiniteMath.isFinite(directionWorld)
        || !Double.isFinite(lengthSquared)
        || lengthSquared < MIN_INPUT_LENGTH_SQUARED) {
      return Optional.empty();
    }
    Vector2f pitchYaw = PerspectiveMath.directionToEulerDeg(directionWorld, new Vector2f());
    return Optional.of(new LookRotation(pitchYaw.y, pitchYaw.x));
  }

  private static Optional<Vector3f> combineInput(
      float leftImpulse, float forwardImpulse, Quaternionfc orientation) {
    var left = PerspectiveMath.getLeft(orientation, new Vector3f());
    var forward = PerspectiveMath.getForward(orientation, new Vector3f());
    var direction = left.mul(leftImpulse).fma(forwardImpulse, forward);
    return FiniteMath.isFinite(direction) ? Optional.of(direction) : Optional.empty();
  }
}
