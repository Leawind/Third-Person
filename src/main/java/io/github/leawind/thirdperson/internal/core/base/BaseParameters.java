package io.github.leawind.thirdperson.internal.core.base;

import io.github.leawind.thirdperson.internal.core.base.camera.CameraProfile;
import io.github.leawind.thirdperson.internal.core.base.camera.CameraSmoothingParameters;
import io.github.leawind.thirdperson.internal.core.base.pivot.CameraPivotSmoothing;
import io.github.leawind.thirdperson.internal.core.base.rotation.PlayerRotationParameters;
import io.github.leawind.thirdperson.internal.core.base.rotation.PlayerRotationSmoothing;
import java.util.Objects;
import java.util.Optional;

/// Complete instantaneous input from the scheduling layer to the base layer.
public record BaseParameters(
    CameraProfile camera,
    CameraPivotSmoothing cameraPivotSmoothing,
    CameraSmoothingParameters cameraSmoothing,
    RaycastOrigin raycastOrigin,
    boolean centerCameraEntitySounds,
    PlayerRotationParameters playerRotation) {
  private static final BaseParameters DEFAULTS =
      new BaseParameters(
          new CameraProfile(1.5, -0.25, -0.25, -0.25, 1.0, false),
          new CameraPivotSmoothing(0.064, 0.08),
          new CameraSmoothingParameters(0.0, 0.06, 0.08, 0.04),
          RaycastOrigin.CAMERA,
          false,
          PlayerRotationParameters.custom(
              Optional.empty(), 0.0, PlayerRotationSmoothing.IMMEDIATE));

  public BaseParameters {
    Objects.requireNonNull(camera, "camera");
    Objects.requireNonNull(cameraPivotSmoothing, "cameraPivotSmoothing");
    Objects.requireNonNull(cameraSmoothing, "cameraSmoothing");
    Objects.requireNonNull(raycastOrigin, "raycastOrigin");
    Objects.requireNonNull(playerRotation, "playerRotation");
  }

  public static BaseParameters defaults() {
    return DEFAULTS;
  }

  public BaseParameters withPlayerRotation(PlayerRotationParameters value) {
    return new BaseParameters(
        camera,
        cameraPivotSmoothing,
        cameraSmoothing,
        raycastOrigin,
        centerCameraEntitySounds,
        value);
  }
}
