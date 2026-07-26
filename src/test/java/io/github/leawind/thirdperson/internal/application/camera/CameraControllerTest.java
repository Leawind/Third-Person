package io.github.leawind.thirdperson.internal.application.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.application.ThirdPersonSession;
import io.github.leawind.thirdperson.internal.core.camera.CameraPose;
import io.github.leawind.thirdperson.internal.core.camera.CameraSmoothingParameters;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.junit.jupiter.api.Test;

class CameraControllerTest {
  private static final ThirdPersonConfig.CameraProfile PROFILE =
      new ThirdPersonConfig.CameraProfile(4.0, 0.0, 0.0, 0.0, 1.0, false);
  private static final CameraSmoothingParameters IMMEDIATE =
      new CameraSmoothingParameters(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

  @Test
  void resolvesCameraAndFallsBackToLastSafePose() {
    var session = new ThirdPersonSession();
    var controller = new CameraController(session);
    CameraFrameInput frame = frameAt(0.0);

    CameraPose safe =
        controller
            .update(
                frame,
                PROFILE,
                IMMEDIATE,
                (pivot, desired) -> Optional.of(new Vector3d(desired)))
            .orElseThrow();
    assertEquals(new Vector3d(0.0, 0.0, -4.0), safe.copyPosition(new Vector3d()));

    CameraPose fallback =
        controller
            .update(frameAt(10.0), PROFILE, IMMEDIATE, (pivot, desired) -> Optional.empty())
            .orElseThrow();
    assertEquals(
        safe.copyPosition(new Vector3d()), fallback.copyPosition(new Vector3d()));
  }

  @Test
  void delegatesTightSpaceMeasurementThroughCollisionPort() {
    var session = new ThirdPersonSession();
    var controller = new CameraController(session);
    CameraFrameInput frame = frameAt(0.0);

    assertFalse(
        controller.updateTightSpace(frame, PROFILE, CameraControllerTest::contractToTenPercent));
    assertTrue(
        controller.updateTightSpace(frame, PROFILE, CameraControllerTest::contractToTenPercent));
  }

  @Test
  void rejectsInvalidFrameBeforeItReachesTheController() {
    assertTrue(
        CameraFrameInput.tryCreate(
                new Vector3d(), new Quaternionf(), Float.NaN, 16.0 / 9.0, false, 0.0)
            .isEmpty());
  }

  private static CameraFrameInput frameAt(double pivotX) {
    return CameraFrameInput.tryCreate(
            new Vector3d(pivotX, 0.0, 0.0),
            new Quaternionf(),
            70.0f,
            16.0 / 9.0,
            false,
            0.0)
        .orElseThrow();
  }

  private static Optional<Vector3d> contractToTenPercent(
      Vector3dc pivot, Vector3dc desired) {
    return Optional.of(new Vector3d(pivot).lerp(desired, 0.1));
  }
}
