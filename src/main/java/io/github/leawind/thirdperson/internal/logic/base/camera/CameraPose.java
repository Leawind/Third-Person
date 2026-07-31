package io.github.leawind.thirdperson.internal.logic.base.camera;

import io.github.leawind.thirdperson.internal.logic.base.math.FiniteMath;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// An immutable, Minecraft-independent camera pose.
///
/// Mutable JOML values are copied on both input and output.
public final class CameraPose {
  private static final float MIN_QUATERNION_LENGTH_SQUARED = 1.0e-12f;

  private final Vector3d position;
  private final Quaternionf rotation;
  private final float fovDegrees;

  private CameraPose(Vector3dc position, Quaternionfc rotation, float fovDegrees) {
    this.position = new Vector3d(position);
    this.rotation = new Quaternionf(rotation).normalize();
    this.fovDegrees = fovDegrees;
  }

  /// Creates a validated pose, or returns empty when any component is unusable.
  public static Optional<CameraPose> tryCreate(
      Vector3dc position, Quaternionfc rotation, float fovDegrees) {
    Objects.requireNonNull(position, "position");
    Objects.requireNonNull(rotation, "rotation");

    if (!FiniteMath.isFinite(position)
        || !FiniteMath.isFinite(rotation)
        || !Float.isFinite(fovDegrees)
        || fovDegrees <= 0.0f
        || fovDegrees >= 180.0f) {
      return Optional.empty();
    }

    float lengthSquared = rotation.lengthSquared();
    if (!Float.isFinite(lengthSquared) || lengthSquared <= MIN_QUATERNION_LENGTH_SQUARED) {
      return Optional.empty();
    }

    return Optional.of(new CameraPose(position, rotation, fovDegrees));
  }

  public Vector3d copyPosition(Vector3d destination) {
    return Objects.requireNonNull(destination, "destination").set(position);
  }

  public Quaternionf copyRotation(Quaternionf destination) {
    return Objects.requireNonNull(destination, "destination").set(rotation);
  }

  public float fovDegrees() {
    return fovDegrees;
  }

  /// Returns the same orientation and FOV at a replacement position.
  public Optional<CameraPose> withPosition(Vector3dc replacement) {
    return tryCreate(Objects.requireNonNull(replacement, "replacement"), rotation, fovDegrees);
  }
}
