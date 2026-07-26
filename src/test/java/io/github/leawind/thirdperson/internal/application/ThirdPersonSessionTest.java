package io.github.leawind.thirdperson.internal.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.core.camera.CameraMode;
import io.github.leawind.thirdperson.internal.core.camera.CameraPose;
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
    session.recordSafeCameraPose(
        CameraPose.tryCreate(new Vector3d(1.0, 2.0, 3.0), new Quaternionf(), 70.0f)
            .orElseThrow());
    assertTrue(session.isPerspectiveActive());
    assertTrue(session.isControllingCamera());
    assertEquals(CameraMode.AIMING, session.mode());
    assertTrue(session.lastSafeCameraPose().isPresent());

    session.reset();
    assertFalse(session.isPerspectiveActive());
    assertFalse(session.isControllingCamera());
    assertEquals(CameraMode.BYPASS, session.mode());
    assertTrue(session.lastSafeCameraPose().isEmpty());
  }

  @Test
  void inactiveSessionCannotEnterActiveMode() {
    var session = new ThirdPersonSession();
    assertThrows(IllegalStateException.class, () -> session.setMode(CameraMode.NORMAL));
  }
}
