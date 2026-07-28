package io.github.leawind.thirdperson.internal.application.camera;

import io.github.leawind.thirdperson.internal.application.ThirdPersonSession;
import io.github.leawind.thirdperson.internal.core.camera.CameraInput;
import io.github.leawind.thirdperson.internal.core.camera.CameraPose;
import io.github.leawind.thirdperson.internal.core.camera.CameraRig;
import io.github.leawind.thirdperson.internal.core.camera.CameraSmoothingParameters;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Vector3d;

/// Coordinates the pure camera pipeline.
public final class CameraController {
  private final ThirdPersonSession session;

  public CameraController(ThirdPersonSession session) {
    this.session = Objects.requireNonNull(session, "session");
  }

  public Optional<CameraPose> update(
      CameraFrameInput frame,
      ThirdPersonConfig.CameraProfile profile,
      CameraSmoothingParameters smoothing) {
    Objects.requireNonNull(frame, "frame");
    Objects.requireNonNull(profile, "profile");
    Objects.requireNonNull(smoothing, "smoothing");

    Vector3d pivot = frame.copyPivot(new Vector3d());
    Quaternionf rotation = frame.copyRotation(new Quaternionf());
    float targetFovDegrees = (float) (frame.baseFovDegrees() * profile.fovMultiplier());
    CameraInput targetInput =
        CameraInput.tryCreate(pivot, rotation, profile.cameraParameters(), targetFovDegrees)
            .orElse(null);
    if (targetInput == null) {
      return Optional.empty();
    }

    CameraInput smoothedInput =
        session.cameraSmoother().update(targetInput, frame.deltaSeconds(), smoothing).orElse(null);
    if (smoothedInput == null) {
      return Optional.empty();
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
      return Optional.empty();
    }
    return Optional.of(idealPose);
  }
}
