package io.github.leawind.thirdperson.internal.core.base.camera;

import io.github.leawind.thirdperson.internal.core.base.math.FiniteMath;
import io.github.leawind.thirdperson.internal.core.base.pivot.PivotPose;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

/// Immutable Minecraft-independent input for one camera update.
public final class CameraFrameInput {
  private static final float MIN_QUATERNION_LENGTH_SQUARED = 1.0e-12f;

  private final PivotPose pivotPose;
  private final Quaternionf pivotFromCamera;
  private final float baseFovDegrees;
  private final double aspectRatio;
  private final CameraSubjectDimensions subjectDimensions;
  private final double deltaSeconds;

  private CameraFrameInput(
      PivotPose pivotPose,
      Quaternionfc pivotFromCamera,
      float baseFovDegrees,
      double aspectRatio,
      CameraSubjectDimensions subjectDimensions,
      double deltaSeconds) {
    this.pivotPose = pivotPose;
    this.pivotFromCamera = new Quaternionf(pivotFromCamera).normalize();
    this.baseFovDegrees = baseFovDegrees;
    this.aspectRatio = aspectRatio;
    this.subjectDimensions = subjectDimensions;
    this.deltaSeconds = deltaSeconds;
  }

  public static Optional<CameraFrameInput> tryCreate(
      PivotPose pivotPose,
      Quaternionfc pivotFromCamera,
      float baseFovDegrees,
      double aspectRatio,
      CameraSubjectDimensions subjectDimensions,
      double deltaSeconds) {
    Objects.requireNonNull(pivotPose, "pivotPose");
    Objects.requireNonNull(pivotFromCamera, "pivotFromCamera");
    Objects.requireNonNull(subjectDimensions, "subjectDimensions");
    float rotationLengthSquared = pivotFromCamera.lengthSquared();
    if (!FiniteMath.isFinite(pivotFromCamera)
        || !Float.isFinite(rotationLengthSquared)
        || rotationLengthSquared <= MIN_QUATERNION_LENGTH_SQUARED
        || !Float.isFinite(baseFovDegrees)
        || baseFovDegrees <= 0.0f
        || baseFovDegrees >= 180.0f
        || !Double.isFinite(aspectRatio)
        || aspectRatio <= 0.0
        || !Double.isFinite(deltaSeconds)
        || deltaSeconds < 0.0) {
      return Optional.empty();
    }
    return Optional.of(
        new CameraFrameInput(
            pivotPose,
            pivotFromCamera,
            baseFovDegrees,
            aspectRatio,
            subjectDimensions,
            deltaSeconds));
  }

  public PivotPose pivotPose() {
    return pivotPose;
  }

  public Quaternionf copyPivotFromCamera(Quaternionf destination) {
    return Objects.requireNonNull(destination, "destination").set(pivotFromCamera);
  }

  public float baseFovDegrees() {
    return baseFovDegrees;
  }

  public double aspectRatio() {
    return aspectRatio;
  }

  public CameraSubjectDimensions subjectDimensions() {
    return subjectDimensions;
  }

  public double deltaSeconds() {
    return deltaSeconds;
  }
}
