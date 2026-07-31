package io.github.leawind.thirdperson.internal.logic.base.camera;

import io.github.leawind.thirdperson.internal.logic.base.math.ExponentialSmoothing;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Vector3d;

/// Smooths independent camera-rig inputs without mixing mouse rotation into world position.
public final class CameraSmoother {
  private final Vector3d pivot = new Vector3d();
  private final Quaternionf rotation = new Quaternionf();
  private boolean initialized;
  private double distance;
  private double offsetX;
  private double offsetY;
  private float fovDegrees;

  public Optional<CameraInput> update(
      CameraInput target,
      double deltaSeconds,
      CameraSmoothingParameters smoothing) {
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(smoothing, "smoothing");
    if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0.0) {
      return Optional.empty();
    }

    double elapsed = Math.min(deltaSeconds, 0.1);
    var targetPivot = target.copyPivot(new Vector3d());
    var targetRotation = target.copyRotation(new Quaternionf());
    CameraParameters targetParameters = target.parameters();
    if (!initialized) {
      pivot.set(targetPivot);
      rotation.set(targetRotation);
      distance = targetParameters.distance();
      offsetX = targetParameters.anchorNdcX();
      offsetY = targetParameters.anchorNdcY();
      fovDegrees = target.fovDegrees();
      initialized = true;
      return snapshot();
    }

    double horizontalAlpha =
        ExponentialSmoothing.alpha(elapsed, smoothing.horizontalPivotHalfLife());
    double verticalAlpha =
        ExponentialSmoothing.alpha(elapsed, smoothing.verticalPivotHalfLife());
    pivot.x += (targetPivot.x - pivot.x) * horizontalAlpha;
    pivot.y += (targetPivot.y - pivot.y) * verticalAlpha;
    pivot.z += (targetPivot.z - pivot.z) * horizontalAlpha;

    double rotationAlpha =
        ExponentialSmoothing.alpha(elapsed, smoothing.rotationHalfLife());
    if (rotationAlpha >= 1.0) {
      rotation.set(targetRotation);
    } else if (rotationAlpha > 0.0) {
      // q and -q represent the same rotation. Select the shortest-arc representation.
      if (rotation.dot(targetRotation) < 0.0f) {
        targetRotation.set(
            -targetRotation.x(),
            -targetRotation.y(),
            -targetRotation.z(),
            -targetRotation.w());
      }
      rotation.slerp(targetRotation, (float) rotationAlpha).normalize();
    }

    double offsetAlpha = ExponentialSmoothing.alpha(elapsed, smoothing.offsetHalfLife());
    double distanceAlpha = ExponentialSmoothing.alpha(elapsed, smoothing.distanceHalfLife());
    double fovAlpha = ExponentialSmoothing.alpha(elapsed, smoothing.fovHalfLife());
    offsetX += (targetParameters.anchorNdcX() - offsetX) * offsetAlpha;
    offsetY += (targetParameters.anchorNdcY() - offsetY) * offsetAlpha;
    distance += (targetParameters.distance() - distance) * distanceAlpha;
    fovDegrees += (target.fovDegrees() - fovDegrees) * fovAlpha;
    return snapshot();
  }

  public void reset() {
    initialized = false;
    pivot.zero();
    rotation.identity();
    distance = 0.0;
    offsetX = 0.0;
    offsetY = 0.0;
    fovDegrees = 0.0f;
  }

  private Optional<CameraInput> snapshot() {
    CameraParameters parameters;
    try {
      parameters = new CameraParameters(distance, offsetX, offsetY);
    } catch (IllegalArgumentException ignored) {
      return Optional.empty();
    }
    return CameraInput.tryCreate(pivot, rotation, parameters, fovDegrees);
  }
}
