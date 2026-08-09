package io.github.leawind.thirdperson.internal.core.schedule.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.leawind.thirdperson.internal.core.base.camera.CameraProfile;
import org.junit.jupiter.api.Test;

class CameraSettingsTest {
  @Test
  void ownsTheDefaultCameraState() {
    var settings = new CameraSettings();

    assertEquals(CameraSettings.defaultNormalProfile(), settings.normalProfile());
    assertEquals(CameraSettings.defaultAimingProfile(), settings.aimingProfile());
    assertEquals(CameraSettings.defaultSmoothing(), settings.smoothing());
  }

  @Test
  void cameraValuesRejectInvalidDirectConstruction() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new CameraProfile(Double.NaN, 0.0, 0.0, 0.0, 1.0, false));
    assertThrows(
        IllegalArgumentException.class, () -> new CameraProfile(4.0, 2.0, 0.0, 0.0, 1.0, false));
    assertThrows(IllegalArgumentException.class, () -> new ModeSmoothing(-0.1, 0.0, 0.0, 0.0, 0.0));
    assertThrows(IllegalArgumentException.class, () -> new ModeSmoothing(0.3, 0.0, 0.0, 0.0, 0.0));
  }
}
