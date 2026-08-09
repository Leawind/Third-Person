package io.github.leawind.thirdperson.internal.core.base.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class CameraRigTest {
  @Test
  void identityRotationPlacesCameraBehindPivot() {
    CameraPose pose =
        CameraRig.calculate(
                new Vector3d(1.0, 2.0, 3.0),
                new Quaternionf(),
                new CameraParameters(4.0, 0.0, 0.0),
                70.0f,
                16.0 / 9.0)
            .orElseThrow();

    assertEquals(new Vector3d(1.0, 2.0, -1.0), pose.copyPosition(new Vector3d()));
  }

  @Test
  void shoulderCompositionMirrorsAcrossPivot() {
    CameraPose left = calculateWithHorizontalAnchor(-0.25);
    CameraPose right = calculateWithHorizontalAnchor(0.25);
    Vector3d leftPosition = left.copyPosition(new Vector3d());
    Vector3d rightPosition = right.copyPosition(new Vector3d());

    assertEquals(-leftPosition.x, rightPosition.x, 1.0e-6);
    assertEquals(leftPosition.y, rightPosition.y, 1.0e-6);
    assertEquals(leftPosition.z, rightPosition.z, 1.0e-6);
  }

  @Test
  void rejectsInvalidProjection() {
    assertFalse(
        CameraRig.calculate(
                new Vector3d(),
                new Quaternionf(),
                new CameraParameters(4.0, 0.0, 0.0),
                70.0f,
                Double.NaN)
            .isPresent());
  }

  private static CameraPose calculateWithHorizontalAnchor(double anchor) {
    return CameraRig.calculate(
            new Vector3d(),
            new Quaternionf(),
            new CameraParameters(4.0, anchor, 0.0),
            70.0f,
            16.0 / 9.0)
        .orElseThrow();
  }
}
