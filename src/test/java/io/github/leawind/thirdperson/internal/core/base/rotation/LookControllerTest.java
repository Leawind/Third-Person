package io.github.leawind.thirdperson.internal.core.base.rotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

class LookControllerTest {
  @Test
  void appliesVanillaTurnScaleAndPitchClamp() {
    var controller = new LookController();
    controller.initialize(0.0f, 0.0f);

    assertTrue(controller.turn(600.0, 1000.0));
    assertEquals(90.0f, controller.yawDegrees(), 1.0e-5f);
    assertEquals(90.0f, controller.pitchDegrees(), 1.0e-5f);
  }

  @Test
  void quaternionUsesPerspectiveConvention() {
    var controller = new LookController();
    controller.initialize(0.0f, 90.0f);
    var rotation = new Quaternionf();

    assertTrue(controller.copyRotation(rotation));
    var forward = rotation.transform(new Vector3f(0.0f, 0.0f, 1.0f));
    assertEquals(-1.0f, forward.x, 1.0e-5f);
    assertEquals(0.0f, forward.z, 1.0e-5f);
  }

  @Test
  void convertsBetweenWorldAndPivotFacingDirections() {
    var controller = new LookController();
    var worldFromPivot = new Quaternionf().rotateYXZ(0.7f, -0.4f, 0.9f);
    var originalWorldFromCamera = new Quaternionf().rotateYXZ(-1.1f, 0.3f, 0.0f);

    controller.initializeFromWorldRotation(originalWorldFromCamera, worldFromPivot);

    LookRotation facing = controller.facingRotation(worldFromPivot).orElseThrow();
    var reconstructed =
        PerspectiveMath.eulerDegToQuat(
            facing.pitchDegrees(), facing.yawDegrees(), 0.0f, new Quaternionf());
    var expectedForward = originalWorldFromCamera.transform(new Vector3f(0.0f, 0.0f, 1.0f));
    var actualForward = reconstructed.transform(new Vector3f(0.0f, 0.0f, 1.0f));
    assertEquals(expectedForward.x, actualForward.x, 1.0e-5f);
    assertEquals(expectedForward.y, actualForward.y, 1.0e-5f);
    assertEquals(expectedForward.z, actualForward.z, 1.0e-5f);
  }

  @Test
  void invalidOrUninitializedInputIsNotConsumed() {
    var controller = new LookController();
    assertFalse(controller.turn(1.0, 1.0));
    controller.initialize(0.0f, 0.0f);
    assertFalse(controller.turn(Double.NaN, 1.0));
  }
}
