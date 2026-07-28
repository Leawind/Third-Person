package io.github.leawind.thirdperson.internal.core.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.application.camera.CameraSettings;
import io.github.leawind.thirdperson.internal.core.camera.CameraProfile;
import org.junit.jupiter.api.Test;

class CameraAdjustmentControllerTest {
  @Test
  void adjustsOffsetsAndDistanceWithinPublicBounds() {
    var controller = new CameraAdjustmentController();
    CameraProfile defaults = CameraSettings.defaultNormalProfile();
    controller.begin(defaults);

    CameraProfile turned = controller.turn(1000.0, -1000.0).orElseThrow();
    assertEquals(-1.0, turned.offsetX());
    assertEquals(-1.0, turned.offsetY());

    CameraProfile zoomedIn = controller.scroll(1.0).orElseThrow();
    assertEquals(defaults.distance() / 1.25, zoomedIn.distance(), 1.0e-12);
    CameraProfile zoomedOut = controller.scroll(-1.0).orElseThrow();
    assertEquals(defaults.distance(), zoomedOut.distance(), 1.0e-12);
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
  void centeredAdjustmentChangesOnlyItsVerticalOffset() {
    var controller = new CameraAdjustmentController();
    CameraProfile side = CameraSettings.defaultNormalProfile();
    controller.begin(side.withCentered(true));

    CameraProfile centered = controller.turn(1000.0, 100.0).orElseThrow();

    assertTrue(centered.centered());
    assertEquals(side.offsetX(), centered.offsetX());
    assertEquals(side.offsetY(), centered.offsetY());
    assertEquals(side.centeredOffsetY() + 0.25, centered.centeredOffsetY());
  }

  @Test
  void unchangedGestureDoesNotRequestACommit() {
    var controller = new CameraAdjustmentController();
    controller.begin(CameraSettings.defaultNormalProfile());

    assertTrue(controller.turn(0.0, 0.0).isEmpty());
    assertTrue(controller.finish().isEmpty());
  }
}
