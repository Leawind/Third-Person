package io.github.leawind.thirdperson.internal.core.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import org.junit.jupiter.api.Test;

class CameraAdjustmentControllerTest {
  @Test
  void adjustsOffsetsAndDistanceWithinPublicBounds() {
    var controller = new CameraAdjustmentController();
    ThirdPersonConfig.CameraProfile defaults = ThirdPersonConfig.defaults().camera().normal();
    controller.begin(defaults);

    ThirdPersonConfig.CameraProfile turned = controller.turn(1000.0, -1000.0).orElseThrow();
    assertEquals(1.0, turned.offsetX());
    assertEquals(1.0, turned.offsetY());

    ThirdPersonConfig.CameraProfile zoomed = controller.scroll(1000.0).orElseThrow();
    assertEquals(0.0, zoomed.distance());
    assertTrue(controller.finish().isPresent());
    assertFalse(controller.isAdjusting());
  }

  @Test
  void ignoresInputOutsideAnAdjustmentGesture() {
    var controller = new CameraAdjustmentController();

    assertTrue(controller.turn(1.0, 1.0).isEmpty());
    assertTrue(controller.scroll(1.0).isEmpty());
    assertTrue(controller.finish().isEmpty());
  }

  @Test
  void unchangedGestureDoesNotRequestACommit() {
    var controller = new CameraAdjustmentController();
    controller.begin(ThirdPersonConfig.defaults().camera().normal());

    assertTrue(controller.turn(0.0, 0.0).isEmpty());
    assertTrue(controller.finish().isEmpty());
  }
}
