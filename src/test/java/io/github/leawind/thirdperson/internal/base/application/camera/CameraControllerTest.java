package io.github.leawind.thirdperson.internal.base.application.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.base.application.BaseSession;
import io.github.leawind.thirdperson.internal.base.core.camera.CameraPose;
import io.github.leawind.thirdperson.internal.base.api.CameraProfile;
import io.github.leawind.thirdperson.internal.base.api.CameraSmoothingParameters;
import io.github.leawind.thirdperson.internal.base.core.camera.CameraSubjectDimensions;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class CameraControllerTest {
  private static final CameraProfile PROFILE =
      new CameraProfile(4.0, 0.0, 0.0, 0.0, 1.0, false);
  private static final CameraSmoothingParameters IMMEDIATE =
      new CameraSmoothingParameters(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

  @Test
  void resolvesTheIdealCameraPose() {
    var session = new BaseSession();
    var controller = new CameraController(session);
    CameraFrameInput frame = frameAt(0.0);

    CameraPose pose =
        controller
            .update(
                frame,
                PROFILE,
                IMMEDIATE,
                (pivot, desired) -> Optional.of(new Vector3d(desired)))
            .orElseThrow();
    assertEquals(new Vector3d(0.0, 0.0, -4.0), pose.copyPosition(new Vector3d()));
  }

  @Test
  void appliesCollisionToTheIdealCameraPose() {
    var controller = new CameraController(new BaseSession());

    CameraPose pose =
        controller
            .update(
                frameAt(0.0),
                PROFILE,
                IMMEDIATE,
                (pivot, desired) -> Optional.of(new Vector3d(pivot).lerp(desired, 0.25)))
            .orElseThrow();

    assertEquals(new Vector3d(0.0, 0.0, -1.0), pose.copyPosition(new Vector3d()));
  }

  @Test
  void rejectsInvalidFrameBeforeItReachesTheController() {
    assertTrue(
        CameraFrameInput.tryCreate(
                new Vector3d(),
                new Quaternionf(),
                Float.NaN,
                16.0 / 9.0,
                new CameraSubjectDimensions(0.0, 1.0),
                0.0)
            .isEmpty());
  }

  private static CameraFrameInput frameAt(double pivotX) {
    return CameraFrameInput.tryCreate(
            new Vector3d(pivotX, 0.0, 0.0),
            new Quaternionf(),
            70.0f,
            16.0 / 9.0,
            new CameraSubjectDimensions(0.0, 1.0),
            0.0)
        .orElseThrow();
  }
}
