package io.github.leawind.thirdperson.internal.core.base.camera;

import io.github.leawind.thirdperson.internal.core.base.math.FiniteMath;
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
  private final float baseFovDegrees;
  private final double fovMultiplier;

  private CameraInput(
      Vector3dc pivot,
      Quaternionfc rotation,
      CameraParameters parameters,
      float baseFovDegrees,
      double fovMultiplier) {
    this.pivot = new Vector3d(pivot);
    this.rotation = new Quaternionf(rotation).normalize();
    this.parameters = parameters;
    this.baseFovDegrees = baseFovDegrees;
    this.fovMultiplier = fovMultiplier;
  }

  public static Optional<CameraInput> tryCreate(
      Vector3dc pivot,
      Quaternionfc rotation,
      CameraParameters parameters,
      float baseFovDegrees,
      double fovMultiplier) {
    Objects.requireNonNull(pivot, "pivot");
    Objects.requireNonNull(rotation, "rotation");
    Objects.requireNonNull(parameters, "parameters");
    double fovDegrees = baseFovDegrees * fovMultiplier;
    if (!FiniteMath.isFinite(pivot)
        || !FiniteMath.isFinite(rotation)
        || !Float.isFinite(baseFovDegrees)
        || !Double.isFinite(fovMultiplier)
        || baseFovDegrees <= 0.0f
        || fovMultiplier <= 0.0
        || fovDegrees <= 0.0f
        || fovDegrees >= 180.0f) {
      return Optional.empty();
    }
    float lengthSquared = rotation.lengthSquared();
    if (!Float.isFinite(lengthSquared) || lengthSquared <= MIN_QUATERNION_LENGTH_SQUARED) {
      return Optional.empty();
    }
    return Optional.of(new CameraInput(pivot, rotation, parameters, baseFovDegrees, fovMultiplier));
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

  public float baseFovDegrees() {
    return baseFovDegrees;
  }

  public double fovMultiplier() {
    return fovMultiplier;
  }

  public float fovDegrees() {
    return (float) (baseFovDegrees * fovMultiplier);
  }
}
