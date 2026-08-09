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
  private final Quaternionf previousRotation = new Quaternionf();
  private final Quaternionf currentRotation = new Quaternionf();
  private boolean initialized;

  public Optional<PivotPose> updateTick(
      PivotPose target, double deltaSeconds, CameraPivotSmoothing smoothing) {
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(smoothing, "smoothing");
    if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0.0) {
      return Optional.empty();
    }
    var targetPosition = target.copyPositionWorld(new Vector3d());
    var targetRotation = target.copyWorldFromPivot(new Quaternionf());
    if (!initialized) {
      previousPosition.set(targetPosition);
      currentPosition.set(targetPosition);
      previousRotation.set(targetRotation);
      currentRotation.set(targetRotation);
      initialized = true;
      return snapshot(currentPosition, currentRotation);
    }

    previousPosition.set(currentPosition);
    previousRotation.set(currentRotation);

    double positionAlpha =
        ExponentialSmoothing.alpha(deltaSeconds, smoothing.positionHalfLife());
    currentPosition.lerp(targetPosition, positionAlpha);

    double rotationAlpha =
        ExponentialSmoothing.alpha(deltaSeconds, smoothing.rotationHalfLife());
    applyPivotSpaceRotationDelta(targetRotation, rotationAlpha);
    return snapshot(currentPosition, currentRotation);
  }

  public Optional<PivotPose> sample(
      PivotPose interpolatedTarget, double partialTick, CameraPivotSmoothing smoothing) {
    Objects.requireNonNull(interpolatedTarget, "interpolatedTarget");
    Objects.requireNonNull(smoothing, "smoothing");
    if (!Double.isFinite(partialTick)) {
      return Optional.empty();
    }
    var targetPosition = interpolatedTarget.copyPositionWorld(new Vector3d());
    var targetRotation = interpolatedTarget.copyWorldFromPivot(new Quaternionf());
    if (!initialized) {
      previousPosition.set(targetPosition);
      currentPosition.set(targetPosition);
      previousRotation.set(targetRotation);
      currentRotation.set(targetRotation);
      initialized = true;
    }

    double interpolation = Math.max(0.0, Math.min(1.0, partialTick));
    var resultPosition = new Vector3d(previousPosition).lerp(currentPosition, interpolation);
    var resultRotation =
        shortestArcSlerp(previousRotation, currentRotation, (float) interpolation, new Quaternionf());
    if (smoothing.positionHalfLife() == 0.0) {
      resultPosition.set(targetPosition);
    }
    if (smoothing.rotationHalfLife() == 0.0) {
      resultRotation.set(targetRotation);
    }
    return PivotPose.tryCreate(resultPosition, resultRotation);
  }

  public void reset() {
    initialized = false;
    previousPosition.zero();
    currentPosition.zero();
    previousRotation.identity();
    currentRotation.identity();
  }

  private void applyPivotSpaceRotationDelta(Quaternionf target, double alpha) {
    if (alpha >= 1.0) {
      currentRotation.set(target);
      return;
    }
    if (alpha <= 0.0) {
      return;
    }

    // Delta is expressed in the current pivot frame: target = current * delta.
    var localDelta = new Quaternionf(currentRotation).conjugate().mul(target).normalize();
    if (localDelta.w < 0.0f) {
      localDelta.set(-localDelta.x, -localDelta.y, -localDelta.z, -localDelta.w);
    }
    var localStep = new Quaternionf().slerp(localDelta, (float) alpha).normalize();
    currentRotation.mul(localStep).normalize();
  }

  private static Quaternionf shortestArcSlerp(
      Quaternionf from, Quaternionf to, float alpha, Quaternionf destination) {
    var target = new Quaternionf(to);
    if (from.dot(target) < 0.0f) {
      target.set(-target.x, -target.y, -target.z, -target.w);
    }
    return destination.set(from).slerp(target, alpha).normalize();
  }

  private static Optional<PivotPose> snapshot(Vector3d position, Quaternionf rotation) {
    return PivotPose.tryCreate(position, rotation);
  }
}
