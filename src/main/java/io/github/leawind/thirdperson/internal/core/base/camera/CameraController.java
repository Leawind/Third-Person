package io.github.leawind.thirdperson.internal.core.base.camera;

import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Vector3d;

/// Coordinates the pure camera pipeline and delegates world collision through a port.
public final class CameraController {
  private final CameraSmoother smoother;

  public CameraController(CameraSmoother smoother) {
    this.smoother = Objects.requireNonNull(smoother, "smoother");
  }

  public Optional<CameraPose> update(
      CameraFrameInput frame,
      CameraProfile profile,
      CameraSmoothingParameters smoothing,
      CameraCollisionPort collision) {
    Objects.requireNonNull(frame, "frame");
    Objects.requireNonNull(profile, "profile");
    Objects.requireNonNull(smoothing, "smoothing");
    Objects.requireNonNull(collision, "collision");

    var pivotPose = frame.pivotPose();
    Vector3d pivot = pivotPose.copyPositionWorld(new Vector3d());
    Quaternionf pivotFromCamera = frame.copyPivotFromCamera(new Quaternionf());
    double distance =
        frame
            .subjectDimensions()
            .resolveDistance(profile.distanceFactor(), profile.fovMultiplier());
    CameraParameters parameters =
        profile.centered()
            ? new CameraParameters(distance, 0.0, profile.centeredOffsetY())
            : new CameraParameters(distance, profile.offsetX(), profile.offsetY());
    CameraInput targetInput =
        CameraInput.tryCreate(
                pivot,
                pivotFromCamera,
                parameters,
                frame.baseFovDegrees(),
                profile.fovMultiplier())
            .orElse(null);
    if (targetInput == null) {
      return Optional.empty();
    }

    CameraInput smoothedInput =
        smoother.update(targetInput, frame.deltaSeconds(), smoothing).orElse(null);
    if (smoothedInput == null) {
      return Optional.empty();
    }

    Vector3d smoothedPivot = smoothedInput.copyPivot(new Vector3d());
    Quaternionf smoothedPivotFromCamera = smoothedInput.copyRotation(new Quaternionf());
    Quaternionf worldFromCamera =
        pivotPose
            .copyWorldFromPivot(new Quaternionf())
            .mul(smoothedPivotFromCamera)
            .normalize();
    CameraParameters effectiveParameters =
        CameraOffsetSqueeze.apply(smoothedInput.parameters(), smoothedPivotFromCamera);
    CameraPose idealPose =
        CameraRig.calculate(
                smoothedPivot,
                worldFromCamera,
                effectiveParameters,
                smoothedInput.fovDegrees(),
                frame.aspectRatio())
            .orElse(null);
    if (idealPose == null) {
      return Optional.empty();
    }
    Optional<Vector3d> resolvedPosition =
        collision.resolve(smoothedPivot, idealPose.copyPosition(new Vector3d()));
    if (resolvedPosition == null || resolvedPosition.isEmpty()) {
      return Optional.empty();
    }
    return idealPose.withPosition(resolvedPosition.orElseThrow());
  }
}
