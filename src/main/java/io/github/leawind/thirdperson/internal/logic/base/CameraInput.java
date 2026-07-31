package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.logic.base.FiniteMath;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Immutable input to the camera rig.
public final class CameraInput {
  private static final float MIN_QUATERNION_LENGTH_SQUARED = 1.0e-12f;

  private final Vector3d pivot;
  private final Quaternionf rotation;
  private final CameraParameters parameters;
  private final float fovDegrees;

  private CameraInput(
      Vector3dc pivot, Quaternionfc rotation, CameraParameters parameters, float fovDegrees) {
    this.pivot = new Vector3d(pivot);
    this.rotation = new Quaternionf(rotation).normalize();
    this.parameters = parameters;
    this.fovDegrees = fovDegrees;
  }

  public static Optional<CameraInput> tryCreate(
      Vector3dc pivot, Quaternionfc rotation, CameraParameters parameters, float fovDegrees) {
    Objects.requireNonNull(pivot, "pivot");
    Objects.requireNonNull(rotation, "rotation");
    Objects.requireNonNull(parameters, "parameters");
    if (!FiniteMath.isFinite(pivot)
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
    return Optional.of(new CameraInput(pivot, rotation, parameters, fovDegrees));
  }

  public Vector3d copyPivot(Vector3d destination) {
    return Objects.requireNonNull(destination, "destination").set(pivot);
  }

  public Quaternionf copyRotation(Quaternionf destination) {
    return Objects.requireNonNull(destination, "destination").set(rotation);
  }

  public CameraParameters parameters() {
    return parameters;
  }

  public float fovDegrees() {
    return fovDegrees;
  }
}
