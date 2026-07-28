package io.github.leawind.thirdperson.internal.application.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.leawind.thirdperson.internal.core.camera.CameraProfile;
import io.github.leawind.thirdperson.internal.core.camera.CameraSubjectDimensions;
import io.github.leawind.thirdperson.internal.core.camera.ModeSmoothing;
import org.junit.jupiter.api.Test;

class CameraSettingsTest {
  @Test
  void ownsTheDefaultCameraState() {
    var settings = new CameraSettings();

    assertEquals(1.5625, settings.normalProfile().distanceFactor());
    assertEquals(-0.18, settings.normalProfile().offsetX());
    assertEquals(0.56, settings.aimingProfile().distanceFactor());
    assertEquals(0.0, settings.smoothing().rotationHalfLife());
    assertEquals(0.064, settings.smoothing().normal().horizontalPivotHalfLife());
    assertEquals(0.08, settings.smoothing().normal().verticalPivotHalfLife());
    assertEquals(0.08, settings.smoothing().normal().distanceHalfLife());
    assertEquals(0.08, settings.smoothing().aiming().distanceHalfLife());
    assertEquals(0.0, settings.smoothing().normal().fovHalfLife());
    assertEquals(0.0, settings.smoothing().aiming().fovHalfLife());
    assertEquals(0.24, settings.normalProfile().centeredOffsetY());
    var subjectDimensions = new CameraSubjectDimensions(0.0, 1.0);
    assertEquals(
        0.0,
        settings
            .normalProfile()
            .withCentered(true)
            .cameraParameters(subjectDimensions)
            .anchorNdcX());
    assertEquals(
        0.24,
        settings
            .normalProfile()
            .withCentered(true)
            .cameraParameters(subjectDimensions)
            .anchorNdcY());
  }

  @Test
  void cameraValuesRejectInvalidDirectConstruction() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new CameraProfile(Double.NaN, 0.0, 0.0, 0.0, 1.0, false));
    assertThrows(
        IllegalArgumentException.class,
        () -> new CameraProfile(4.0, 2.0, 0.0, 0.0, 1.0, false));
    assertThrows(
        IllegalArgumentException.class,
        () -> new ModeSmoothing(-0.1, 0.0, 0.0, 0.0, 0.0));
    assertThrows(
        IllegalArgumentException.class,
        () -> new ModeSmoothing(0.3, 0.0, 0.0, 0.0, 0.0));
  }
}
