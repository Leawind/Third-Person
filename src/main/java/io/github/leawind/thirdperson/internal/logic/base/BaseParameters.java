package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.logic.base.camera.CameraProfile;
import io.github.leawind.thirdperson.internal.logic.base.camera.CameraSmoothingParameters;
import io.github.leawind.thirdperson.internal.logic.base.rotation.PlayerRotationParameters;
import io.github.leawind.thirdperson.internal.logic.base.rotation.PlayerRotationSmoothing;
import java.util.Objects;
import java.util.Optional;

/// Complete instantaneous input from the scheduling layer to the base layer.
public record BaseParameters(
    CameraProfile camera,
    CameraSmoothingParameters cameraSmoothing,
    RaycastOrigin raycastOrigin,
    PlayerRotationParameters playerRotation) {
  private static final BaseParameters DEFAULTS =
      new BaseParameters(
          new CameraProfile(1.5625, -0.18, 0.12, 0.24, 1.0, false),
          new CameraSmoothingParameters(0.064, 0.08, 0.0, 0.06, 0.08, 0.0),
          RaycastOrigin.CAMERA,
          PlayerRotationParameters.custom(
              Optional.empty(), 0.0, PlayerRotationSmoothing.IMMEDIATE));

  public BaseParameters {
    Objects.requireNonNull(camera, "camera");
    Objects.requireNonNull(cameraSmoothing, "cameraSmoothing");
    Objects.requireNonNull(raycastOrigin, "raycastOrigin");
    Objects.requireNonNull(playerRotation, "playerRotation");
  }

  public static BaseParameters defaults() {
    return DEFAULTS;
  }

  public BaseParameters withPlayerRotation(PlayerRotationParameters value) {
    return new BaseParameters(camera, cameraSmoothing, raycastOrigin, value);
  }
}
