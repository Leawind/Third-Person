package io.github.leawind.thirdperson.internal.logic.base;

import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;

/// Computes an ideal camera pose from a world-space pivot and a screen-space composition.
public final class CameraRig {
  private CameraRig() {}

  public static Optional<CameraPose> calculate(
      Vector3dc pivot,
      Quaternionfc rotation,
      CameraParameters parameters,
      float fovDegrees,
      double aspectRatio) {
    Objects.requireNonNull(pivot, "pivot");
    Objects.requireNonNull(rotation, "rotation");
    Objects.requireNonNull(parameters, "parameters");

    if (!FiniteMath.isFinite(pivot)
        || !FiniteMath.isFinite(rotation)
        || !Float.isFinite(fovDegrees)
        || fovDegrees <= 0.0f
        || fovDegrees >= 180.0f
        || !Double.isFinite(aspectRatio)
        || aspectRatio <= 0.0) {
      return Optional.empty();
    }

    double tanHalfVerticalFov = Math.tan(Math.toRadians(fovDegrees) * 0.5);
    double tanHalfHorizontalFov = tanHalfVerticalFov * aspectRatio;
    if (!Double.isFinite(tanHalfVerticalFov) || !Double.isFinite(tanHalfHorizontalFov)) {
      return Optional.empty();
    }

    // Perspective API uses +Z forward, +X left and +Y up. NDC X is positive to the right.
    var localPivotDirection =
        new Vector3f(
                (float) (-parameters.anchorNdcX() * tanHalfHorizontalFov),
                (float) (parameters.anchorNdcY() * tanHalfVerticalFov),
                1.0f)
            .normalize();
    var normalizedRotation = new Quaternionf(rotation).normalize();
    var worldPivotDirection = normalizedRotation.transform(localPivotDirection, new Vector3f());

    var cameraPosition =
        new Vector3d(pivot)
            .sub(
                worldPivotDirection.x * parameters.distance(),
                worldPivotDirection.y * parameters.distance(),
                worldPivotDirection.z * parameters.distance());
    return CameraPose.tryCreate(cameraPosition, normalizedRotation, fovDegrees);
  }
}
