package io.github.leawind.thirdperson.internal.logic.base.camera;

import io.github.leawind.thirdperson.internal.logic.base.math.ExponentialSmoothing;
import io.github.leawind.thirdperson.internal.logic.base.math.FiniteMath;
import java.util.Objects;
import java.util.Optional;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Smooths the camera pivot on client ticks and interpolates the resulting states for rendering.
public final class CameraPivotSmoother {
  private final Vector3d previous = new Vector3d();
  private final Vector3d current = new Vector3d();
  private boolean initialized;

  public Optional<Vector3d> updateTick(
      Vector3dc target, double deltaSeconds, CameraSmoothingParameters smoothing) {
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(smoothing, "smoothing");
    if (!FiniteMath.isFinite(target) || !Double.isFinite(deltaSeconds) || deltaSeconds < 0.0) {
      return Optional.empty();
    }
    if (!initialized) {
      previous.set(target);
      current.set(target);
      initialized = true;
      return snapshot(current);
    }

    previous.set(current);
    double horizontalAlpha =
        ExponentialSmoothing.alpha(deltaSeconds, smoothing.horizontalPivotHalfLife());
    double verticalAlpha =
        ExponentialSmoothing.alpha(deltaSeconds, smoothing.verticalPivotHalfLife());
    current.x += (target.x() - current.x) * horizontalAlpha;
    current.y += (target.y() - current.y) * verticalAlpha;
    current.z += (target.z() - current.z) * horizontalAlpha;
    return snapshot(current);
  }

  public Optional<Vector3d> sample(
      Vector3dc interpolatedTarget, double partialTick, CameraSmoothingParameters smoothing) {
    Objects.requireNonNull(interpolatedTarget, "interpolatedTarget");
    Objects.requireNonNull(smoothing, "smoothing");
    if (!FiniteMath.isFinite(interpolatedTarget) || !Double.isFinite(partialTick)) {
      return Optional.empty();
    }
    if (!initialized) {
      previous.set(interpolatedTarget);
      current.set(interpolatedTarget);
      initialized = true;
    }

    double interpolation = Math.max(0.0, Math.min(1.0, partialTick));
    var result = new Vector3d(previous).lerp(current, interpolation);
    if (smoothing.horizontalPivotHalfLife() == 0.0) {
      result.x = interpolatedTarget.x();
      result.z = interpolatedTarget.z();
    }
    if (smoothing.verticalPivotHalfLife() == 0.0) {
      result.y = interpolatedTarget.y();
    }
    return Optional.of(result);
  }

  public void reset() {
    initialized = false;
    previous.zero();
    current.zero();
  }

  private static Optional<Vector3d> snapshot(Vector3dc value) {
    return Optional.of(new Vector3d(value));
  }
}
