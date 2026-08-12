package io.github.leawind.thirdperson.internal.core.base.pivot;

import io.github.leawind.thirdperson.internal.core.base.math.ExponentialSmoothing;
import io.github.leawind.thirdperson.internal.core.base.math.FiniteMath;
import java.util.Objects;
import java.util.Optional;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Optional pivot-position mechanism that advances on client ticks and interpolates for rendering.
///
/// This class has no opinion about the source of the target position or when an application should
/// select this mechanism instead of an immediate, frame-driven, spring, or constrained strategy.
public final class TickInterpolatedPivotPosition {
  private final Vector3d previousPosition = new Vector3d();
  private final Vector3d currentPosition = new Vector3d();
  private boolean initialized;

  public Optional<Vector3d> updateTick(
      Vector3dc target, double deltaSeconds, CameraPivotSmoothing smoothing) {
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(smoothing, "smoothing");
    if (!FiniteMath.isFinite(target) || !Double.isFinite(deltaSeconds) || deltaSeconds < 0.0) {
      return Optional.empty();
    }
    if (!initialized) {
      previousPosition.set(target);
      currentPosition.set(target);
      initialized = true;
      return Optional.of(new Vector3d(currentPosition));
    }

    previousPosition.set(currentPosition);
    double alpha = ExponentialSmoothing.alpha(deltaSeconds, smoothing.positionHalfLife());
    currentPosition.lerp(target, alpha);
    return Optional.of(new Vector3d(currentPosition));
  }

  public Optional<Vector3d> sample(
      Vector3dc renderTarget, double partialTick, CameraPivotSmoothing smoothing) {
    Objects.requireNonNull(renderTarget, "renderTarget");
    Objects.requireNonNull(smoothing, "smoothing");
    if (!FiniteMath.isFinite(renderTarget) || !Double.isFinite(partialTick)) {
      return Optional.empty();
    }
    if (!initialized) {
      previousPosition.set(renderTarget);
      currentPosition.set(renderTarget);
      initialized = true;
    }

    double interpolation = Math.max(0.0, Math.min(1.0, partialTick));
    var result = new Vector3d(previousPosition).lerp(currentPosition, interpolation);
    if (smoothing.positionHalfLife() == 0.0) {
      result.set(renderTarget);
    }
    return Optional.of(result);
  }

  public void reset() {
    initialized = false;
    previousPosition.zero();
    currentPosition.zero();
  }
}
