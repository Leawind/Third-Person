package io.github.leawind.thirdperson.internal.core.camera;

import io.github.leawind.thirdperson.internal.core.config.SmoothingPreset;
import io.github.leawind.thirdperson.internal.core.math.ExponentialSmoothing;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Vector3d;

/// Smooths an ideal camera pose without depending on render-frame timing sources.
public final class CameraSmoother {
  private final Vector3d position = new Vector3d();
  private final Quaternionf rotation = new Quaternionf();
  private boolean initialized;
  private float fovDegrees;

  public Optional<CameraPose> update(
      CameraPose target, double deltaSeconds, SmoothingPreset preset) {
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(preset, "preset");
    if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0.0) {
      return Optional.empty();
    }

    var targetPosition = target.copyPosition(new Vector3d());
    var targetRotation = target.copyRotation(new Quaternionf());
    if (!initialized || preset == SmoothingPreset.OFF) {
      position.set(targetPosition);
      rotation.set(targetRotation);
      fovDegrees = target.fovDegrees();
      initialized = true;
      return CameraPose.tryCreate(position, rotation, fovDegrees);
    }

    double alpha =
        ExponentialSmoothing.alpha(
            Math.min(deltaSeconds, 0.1), preset.halfLifeSeconds());
    position.lerp(targetPosition, alpha);

    // q and -q represent the same rotation. Select the representation on the shortest arc.
    if (rotation.dot(targetRotation) < 0.0f) {
      targetRotation.set(
          -targetRotation.x(),
          -targetRotation.y(),
          -targetRotation.z(),
          -targetRotation.w());
    }
    rotation.slerp(targetRotation, (float) alpha).normalize();
    fovDegrees += (target.fovDegrees() - fovDegrees) * alpha;
    return CameraPose.tryCreate(position, rotation, fovDegrees);
  }

  public void reset() {
    initialized = false;
    position.zero();
    rotation.identity();
    fovDegrees = 0.0f;
  }
}
