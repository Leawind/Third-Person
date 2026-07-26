package io.github.leawind.thirdperson.internal.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.core.camera.CameraMode;
import org.junit.jupiter.api.Test;

class ThirdPersonSessionTest {
  @Test
  void lifecycleResetsMode() {
    var session = new ThirdPersonSession();

    assertFalse(session.isPerspectiveActive());
    assertEquals(CameraMode.BYPASS, session.mode());

    session.activatePerspective();
    session.setMode(CameraMode.AIMING);
    assertTrue(session.isPerspectiveActive());
    assertEquals(CameraMode.AIMING, session.mode());

    session.reset();
    assertFalse(session.isPerspectiveActive());
    assertEquals(CameraMode.BYPASS, session.mode());
  }

  @Test
  void inactiveSessionCannotEnterActiveMode() {
    var session = new ThirdPersonSession();
    assertThrows(IllegalStateException.class, () -> session.setMode(CameraMode.NORMAL));
  }
}
