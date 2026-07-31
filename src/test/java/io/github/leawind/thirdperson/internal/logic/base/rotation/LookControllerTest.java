package io.github.leawind.thirdperson.internal.logic.base.rotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
  void invalidOrUninitializedInputIsNotConsumed() {
    var controller = new LookController();
    assertFalse(controller.turn(1.0, 1.0));
    controller.initialize(0.0f, 0.0f);
    assertFalse(controller.turn(Double.NaN, 1.0));
  }
}
