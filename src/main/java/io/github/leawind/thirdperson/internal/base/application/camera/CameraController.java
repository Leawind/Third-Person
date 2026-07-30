package io.github.leawind.thirdperson.internal.base.application.camera;

import io.github.leawind.thirdperson.internal.base.api.CameraProfile;
import io.github.leawind.thirdperson.internal.base.api.CameraSmoothingParameters;
import io.github.leawind.thirdperson.internal.base.application.BaseSession;
import io.github.leawind.thirdperson.internal.base.application.port.CameraCollisionPort;
import io.github.leawind.thirdperson.internal.base.core.camera.CameraInput;
import io.github.leawind.thirdperson.internal.base.core.camera.CameraParameters;
import io.github.leawind.thirdperson.internal.base.core.camera.CameraPose;
import io.github.leawind.thirdperson.internal.base.core.camera.CameraRig;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Vector3d;

/// Coordinates the pure camera pipeline and delegates world collision through a port.
public final class CameraController {
  private final BaseSession session;

  public CameraController(BaseSession session) {
    this.session = Objects.requireNonNull(session, "session");
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

    Vector3d pivot = frame.copyPivot(new Vector3d());
    Quaternionf rotation = frame.copyRotation(new Quaternionf());
    float targetFovDegrees = (float) (frame.baseFovDegrees() * profile.fovMultiplier());
    double distance =
        frame.subjectDimensions().resolveDistance(
            profile.distanceFactor(), profile.fovMultiplier());
    CameraParameters parameters =
        profile.centered()
            ? new CameraParameters(distance, 0.0, profile.centeredOffsetY())
            : new CameraParameters(distance, profile.offsetX(), profile.offsetY());
    CameraInput targetInput =
        CameraInput.tryCreate(pivot, rotation, parameters, targetFovDegrees).orElse(null);
    if (targetInput == null) {
      return Optional.empty();
    }

    CameraInput smoothedInput =
        session.cameraSmoother().update(targetInput, frame.deltaSeconds(), smoothing).orElse(null);
    if (smoothedInput == null) {
      return Optional.empty();
    }

    Vector3d smoothedPivot = smoothedInput.copyPivot(new Vector3d());
    CameraPose idealPose =
        CameraRig.calculate(
                smoothedPivot,
                smoothedInput.copyRotation(new Quaternionf()),
                smoothedInput.parameters(),
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
