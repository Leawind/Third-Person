package io.github.leawind.thirdperson.internal.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.core.camera.CameraMode;
import io.github.leawind.thirdperson.internal.core.camera.CameraPose;
import io.github.leawind.thirdperson.internal.core.config.CameraProfileSlot;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class ThirdPersonSessionTest {
  @Test
  void lifecycleResetsMode() {
    var session = new ThirdPersonSession();

    assertFalse(session.isPerspectiveActive());
    assertEquals(CameraMode.BYPASS, session.mode());

    session.activatePerspective();
    session.setMode(CameraMode.AIMING);
    session.beginCameraAdjustment(
        CameraProfileSlot.AIMING, ThirdPersonConfig.defaults().camera().aiming());
    session.recordFinalCameraPose(
        CameraPose.tryCreate(new Vector3d(4.0, 5.0, 6.0), new Quaternionf(), 75.0f).orElseThrow());
    assertTrue(session.isPerspectiveActive());
    assertTrue(session.isControllingCamera());
    assertEquals(CameraMode.AIMING, session.mode());
    assertTrue(session.finalCameraPose().isPresent());
    assertEquals(CameraProfileSlot.AIMING, session.cameraAdjustmentSlot().orElseThrow());

    session.reset();
    assertFalse(session.isPerspectiveActive());
    assertFalse(session.isControllingCamera());
    assertEquals(CameraMode.BYPASS, session.mode());
    assertTrue(session.finalCameraPose().isEmpty());
    assertTrue(session.cameraAdjustmentSlot().isEmpty());
  }

  @Test
  void inactiveSessionCannotEnterActiveMode() {
    var session = new ThirdPersonSession();
    assertThrows(IllegalStateException.class, () -> session.setMode(CameraMode.NORMAL));
  }

  @Test
  void resettingCameraTrackingPreservesTheActivePerspective() {
    var session = new ThirdPersonSession();
    session.activatePerspective();
    session.lookController().initialize(15.0f, 30.0f);
    session.recordFinalCameraPose(
        CameraPose.tryCreate(new Vector3d(), new Quaternionf(), 70.0f).orElseThrow());

    session.resetCameraTracking();

    assertTrue(session.isPerspectiveActive());
    assertFalse(session.lookController().isInitialized());
    assertTrue(session.finalCameraPose().isEmpty());
  }
}
