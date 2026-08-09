package io.github.leawind.thirdperson.internal.core.base.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import io.github.leawind.thirdperson.internal.core.base.pivot.PivotPose;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class CameraControllerTest {
  private static final CameraProfile PROFILE = new CameraProfile(4.0, 0.0, 0.0, 0.0, 1.0, false);
  private static final CameraSmoothingParameters IMMEDIATE =
      new CameraSmoothingParameters(0.0, 0.0, 0.0, 0.0);
  private static final CameraSmoothingParameters SMOOTH_FOV =
      new CameraSmoothingParameters(0.0, 0.08, 0.08, 0.08);

  @Test
  void resolvesTheIdealCameraPose() {
    var controller = new CameraController(new CameraSmoother());
    CameraFrameInput frame = frameAt(0.0);

    CameraPose pose =
        controller
            .update(
                frame, PROFILE, IMMEDIATE, (pivot, desired) -> Optional.of(new Vector3d(desired)))
            .orElseThrow();
    assertEquals(new Vector3d(0.0, 0.0, -4.0), pose.copyPosition(new Vector3d()));
  }

  @Test
  void composesPivotAndCameraRotationsBeforeEvaluatingTheRig() {
    var controller = new CameraController(new CameraSmoother());
    PivotPose pivot =
        PivotPose.tryCreate(
                new Vector3d(), new Quaternionf().rotationY((float) Math.toRadians(-90.0)))
            .orElseThrow();

    CameraPose pose =
        controller
            .update(
                frameAt(pivot, new Quaternionf()),
                PROFILE,
                IMMEDIATE,
                (collisionPivot, desired) -> Optional.of(new Vector3d(desired)))
            .orElseThrow();

    assertEquals(new Vector3d(4.0, 0.0, 0.0), pose.copyPosition(new Vector3d()));
  }

  @Test
  void appliesCollisionToTheIdealCameraPose() {
    var controller = new CameraController(new CameraSmoother());

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
  void compositionFovChangeDoesNotMoveCameraAtZeroElapsedTime() {
    var controller = new CameraController(new CameraSmoother());
    var shoulderProfile = new CameraProfile(4.0, -0.25, 0.1, 0.0, 1.0, false);
    var aimingProfile = shoulderProfile.withFovMultiplier(0.8);
    CameraCollisionPort noCollision = (pivot, desired) -> Optional.of(new Vector3d(desired));

    CameraPose before =
        controller.update(frameAt(0.0), shoulderProfile, SMOOTH_FOV, noCollision).orElseThrow();
    CameraPose after =
        controller.update(frameAt(0.0), aimingProfile, SMOOTH_FOV, noCollision).orElseThrow();

    assertEquals(before.copyPosition(new Vector3d()), after.copyPosition(new Vector3d()));
    assertEquals(before.fovDegrees(), after.fovDegrees());
  }

  @Test
  void extremePitchCentersTheSmoothedCompositionBeforeResolvingThePose() {
    var controller = new CameraController(new CameraSmoother());
    var shoulderProfile = new CameraProfile(4.0, -0.4, 0.2, 0.0, 1.0, false);
    var centeredProfile = new CameraProfile(4.0, 0.0, 0.0, 0.0, 1.0, false);
    double pitchPastConfiguredEnd =
        CameraOffsetSqueeze.CENTERED_PITCH_DEGREES
            + (90.0 - CameraOffsetSqueeze.CENTERED_PITCH_DEGREES) * 0.5;
    var nearVertical =
        new Quaternionf().rotationX((float) Math.toRadians(pitchPastConfiguredEnd));
    CameraCollisionPort noCollision = (pivot, desired) -> Optional.of(new Vector3d(desired));

    CameraPose shoulderPose =
        controller
            .update(frameAt(0.0, nearVertical), shoulderProfile, IMMEDIATE, noCollision)
            .orElseThrow();
    controller = new CameraController(new CameraSmoother());
    CameraPose centeredPose =
        controller
            .update(frameAt(0.0, nearVertical), centeredProfile, IMMEDIATE, noCollision)
            .orElseThrow();

    assertEquals(
        centeredPose.copyPosition(new Vector3d()), shoulderPose.copyPosition(new Vector3d()));
  }

  @Test
  void rejectsInvalidFrameBeforeItReachesTheController() {
    assertTrue(
        CameraFrameInput.tryCreate(
                PivotPose.identity(new Vector3d()),
                new Quaternionf(),
                Float.NaN,
                16.0 / 9.0,
                new CameraSubjectDimensions(0.0, 1.0),
                0.0)
            .isEmpty());
  }

  private static CameraFrameInput frameAt(double pivotX) {
    return frameAt(pivotX, new Quaternionf());
  }

  private static CameraFrameInput frameAt(double pivotX, Quaternionf rotation) {
    return frameAt(PivotPose.identity(new Vector3d(pivotX, 0.0, 0.0)), rotation);
  }

  private static CameraFrameInput frameAt(PivotPose pivot, Quaternionf rotation) {
    return CameraFrameInput.tryCreate(
            pivot,
            rotation,
            70.0f,
            16.0 / 9.0,
            new CameraSubjectDimensions(0.0, 1.0),
            0.0)
        .orElseThrow();
  }
}
