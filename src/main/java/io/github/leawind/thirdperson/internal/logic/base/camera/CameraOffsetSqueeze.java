package io.github.leawind.thirdperson.internal.logic.base.camera;

import io.github.leawind.perspectiveapi.internal.utils.smooth.Blenders;
import java.util.Objects;
import java.util.function.DoubleUnaryOperator;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

/// Reduces screen-space composition offsets as the camera approaches a vertical look direction.
final class CameraOffsetSqueeze {
  static final double START_PITCH_DEGREES = 0.0;
  static final double CENTERED_PITCH_DEGREES = 89.8;

  /// Maps normalized squeeze progress to centered progress. Both input and output use [0, 1].
  static final DoubleUnaryOperator TRANSITION_FUNCTION = v -> Blenders.easeIn((float) v);

  private CameraOffsetSqueeze() {}

  /// Applies a transient post-smoothing correction without changing the stored camera offsets.
  static CameraParameters apply(CameraParameters parameters, Quaternionfc rotation) {
    return apply(parameters, rotation, TRANSITION_FUNCTION);
  }

  static CameraParameters apply(
      CameraParameters parameters, Quaternionfc rotation, DoubleUnaryOperator transitionFunction) {
    Objects.requireNonNull(parameters, "parameters");
    Objects.requireNonNull(rotation, "rotation");
    Objects.requireNonNull(transitionFunction, "transitionFunction");

    var forward = rotation.transform(new Vector3f(0.0f, 0.0f, 1.0f));
    double horizontalLength = Math.hypot(forward.x, forward.z);
    double absolutePitchDegrees =
        Math.toDegrees(Math.atan2(Math.abs((double) forward.y), horizontalLength));
    if (absolutePitchDegrees <= START_PITCH_DEGREES) {
      return parameters;
    }
    if (absolutePitchDegrees >= CENTERED_PITCH_DEGREES) {
      return new CameraParameters(parameters.distance(), 0.0, 0.0);
    }

    double progress =
        (absolutePitchDegrees - START_PITCH_DEGREES)
            / (CENTERED_PITCH_DEGREES - START_PITCH_DEGREES);
    double transitionedProgress = transitionFunction.applyAsDouble(progress);
    if (!Double.isFinite(transitionedProgress)) {
      throw new IllegalStateException("Camera offset transition must return a finite value");
    }
    double multiplier = 1.0 - Math.max(0.0, Math.min(1.0, transitionedProgress));
    return new CameraParameters(
        parameters.distance(),
        parameters.anchorNdcX() * multiplier,
        parameters.anchorNdcY() * multiplier);
  }
}
