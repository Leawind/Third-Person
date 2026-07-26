package io.github.leawind.thirdperson.internal.application.camera;

import io.github.leawind.thirdperson.internal.application.ThirdPersonSession;
import io.github.leawind.thirdperson.internal.application.port.CameraCollisionPort;
import io.github.leawind.thirdperson.internal.core.camera.CameraInput;
import io.github.leawind.thirdperson.internal.core.camera.CameraPose;
import io.github.leawind.thirdperson.internal.core.camera.CameraRig;
import io.github.leawind.thirdperson.internal.core.camera.CameraSmoothingParameters;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Vector3d;

/// Coordinates the pure camera pipeline and delegates world collision through a port.
public final class CameraController {
  private final ThirdPersonSession session;

  public CameraController(ThirdPersonSession session) {
    this.session = Objects.requireNonNull(session, "session");
  }

  public Optional<CameraPose> update(
      CameraFrameInput frame,
      ThirdPersonConfig.CameraProfile profile,
      CameraSmoothingParameters smoothing,
      CameraCollisionPort collision) {
    Objects.requireNonNull(frame, "frame");
    Objects.requireNonNull(profile, "profile");
    Objects.requireNonNull(smoothing, "smoothing");
    Objects.requireNonNull(collision, "collision");

    Vector3d pivot = frame.copyPivot(new Vector3d());
    Quaternionf rotation = frame.copyRotation(new Quaternionf());
    float targetFovDegrees = (float) (frame.baseFovDegrees() * profile.fovMultiplier());
    CameraInput targetInput =
        CameraInput.tryCreate(pivot, rotation, profile.cameraParameters(), targetFovDegrees)
            .orElse(null);
    if (targetInput == null) {
      return fallbackPose();
    }

    CameraInput smoothedInput =
        session
            .cameraSmoother()
            .update(targetInput, frame.deltaSeconds(), smoothing)
            .orElse(null);
    if (smoothedInput == null) {
      return fallbackPose();
    }

    CameraPose idealPose =
        CameraRig.calculate(
                smoothedInput.copyPivot(new Vector3d()),
                smoothedInput.copyRotation(new Quaternionf()),
                smoothedInput.parameters(),
                smoothedInput.fovDegrees(),
                frame.aspectRatio())
            .orElse(null);
    if (idealPose == null) {
      return fallbackPose();
    }

    Optional<Vector3d> collisionResult =
        collision.resolve(pivot, idealPose.copyPosition(new Vector3d()));
    if (collisionResult == null || collisionResult.isEmpty()) {
      return fallbackPose();
    }
    Vector3d resolvedPosition =
        session
            .collisionRecovery()
            .resolve(pivot, collisionResult.orElseThrow(), frame.deltaSeconds())
            .orElse(null);
    if (resolvedPosition == null) {
      return fallbackPose();
    }

    CameraPose resolvedPose = idealPose.withPosition(resolvedPosition).orElse(null);
    if (resolvedPose == null) {
      return fallbackPose();
    }
    session.recordSafeCameraPose(resolvedPose);
    return Optional.of(resolvedPose);
  }

  public boolean updateTightSpace(
      CameraFrameInput frame,
      ThirdPersonConfig.CameraProfile profile,
      CameraCollisionPort collision) {
    Objects.requireNonNull(frame, "frame");
    Objects.requireNonNull(profile, "profile");
    Objects.requireNonNull(collision, "collision");

    Vector3d pivot = frame.copyPivot(new Vector3d());
    CameraPose idealPose =
        CameraRig.calculate(
                pivot,
                frame.copyRotation(new Quaternionf()),
                profile.cameraParameters(),
                frame.baseFovDegrees(),
                frame.aspectRatio())
            .orElse(null);
    if (idealPose == null) {
      return resetTightSpace();
    }

    Optional<Vector3d> collisionResult =
        collision.resolve(pivot, idealPose.copyPosition(new Vector3d()));
    if (collisionResult == null || collisionResult.isEmpty()) {
      return resetTightSpace();
    }
    return session
        .tightSpaceDetector()
        .update(collisionResult.orElseThrow().distance(pivot), profile.distance());
  }

  private Optional<CameraPose> fallbackPose() {
    return session.lastSafeCameraPose();
  }

  private boolean resetTightSpace() {
    session.tightSpaceDetector().reset();
    return false;
  }
}
