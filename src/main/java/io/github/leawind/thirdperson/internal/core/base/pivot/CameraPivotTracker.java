package io.github.leawind.thirdperson.internal.core.base.pivot;

import io.github.leawind.thirdperson.internal.core.base.math.ExponentialSmoothing;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Vector3d;

/// Tracks a target pivot pose on client ticks and samples it for rendering.
public final class CameraPivotTracker {
  private final Vector3d previousPosition = new Vector3d();
  private final Vector3d currentPosition = new Vector3d();
  private boolean initialized;

  public Optional<PivotPose> updateTick(
      PivotPose target, double deltaSeconds, CameraPivotSmoothing smoothing) {
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(smoothing, "smoothing");
    if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0.0) {
      return Optional.empty();
    }
    var targetPosition = target.copyPositionWorld(new Vector3d());
    if (!initialized) {
      previousPosition.set(targetPosition);
      currentPosition.set(targetPosition);
      initialized = true;
      return snapshot(currentPosition, target);
    }

    previousPosition.set(currentPosition);

    double positionAlpha =
        ExponentialSmoothing.alpha(deltaSeconds, smoothing.positionHalfLife());
    currentPosition.lerp(targetPosition, positionAlpha);
    return snapshot(currentPosition, target);
  }

  public Optional<PivotPose> sample(
      PivotPose interpolatedTarget, double partialTick, CameraPivotSmoothing smoothing) {
    Objects.requireNonNull(interpolatedTarget, "interpolatedTarget");
    Objects.requireNonNull(smoothing, "smoothing");
    if (!Double.isFinite(partialTick)) {
      return Optional.empty();
    }
    var targetPosition = interpolatedTarget.copyPositionWorld(new Vector3d());
    if (!initialized) {
      previousPosition.set(targetPosition);
      currentPosition.set(targetPosition);
      initialized = true;
    }

    double interpolation = Math.max(0.0, Math.min(1.0, partialTick));
    var resultPosition = new Vector3d(previousPosition).lerp(currentPosition, interpolation);
    if (smoothing.positionHalfLife() == 0.0) {
      resultPosition.set(targetPosition);
    }
    return snapshot(resultPosition, interpolatedTarget);
  }

  public void reset() {
    initialized = false;
    previousPosition.zero();
    currentPosition.zero();
  }

  private static Optional<PivotPose> snapshot(Vector3d position, PivotPose rotationSource) {
    return PivotPose.tryCreate(position, rotationSource.copyWorldFromPivot(new Quaternionf()));
  }
}
