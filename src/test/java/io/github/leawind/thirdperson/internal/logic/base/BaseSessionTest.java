package io.github.leawind.thirdperson.internal.logic.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.logic.base.camera.CameraPose;
import io.github.leawind.thirdperson.internal.logic.base.rotation.MovementIntent;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class BaseSessionTest {
  @Test
  void lifecycleContainsNoNormalOrAimingMode() {
    var session = new BaseSession();

    assertFalse(session.isPerspectiveActive());
    assertFalse(session.isControllingCamera());

    session.activatePerspective();
    session.recordFinalCameraPose(
        CameraPose.tryCreate(new Vector3d(4.0, 5.0, 6.0), new Quaternionf(), 75.0f).orElseThrow());
    assertTrue(session.isPerspectiveActive());
    assertTrue(session.isControllingCamera());
    assertTrue(session.finalCameraPose().isPresent());

    session.reset();
    assertFalse(session.isPerspectiveActive());
    assertFalse(session.isControllingCamera());
    assertTrue(session.finalCameraPose().isEmpty());
  }

  @Test
  void resettingCameraTrackingPreservesTheActivePerspective() {
    var session = new BaseSession();
    session.activatePerspective();
    session.lookController().initialize(15.0f, 30.0f);
    session.recordMovementIntent(new MovementIntent(1.0f, 0.0f, 30.0f, 15.0f));
    session.recordFinalCameraPose(
        CameraPose.tryCreate(new Vector3d(), new Quaternionf(), 70.0f).orElseThrow());
    session.cameraEntityOpacity().setTarget(0.0);
    session.cameraEntityOpacity().update(0.0, 0.0);

    session.resetCameraTracking();

    assertTrue(session.isPerspectiveActive());
    assertFalse(session.lookController().isInitialized());
    assertTrue(session.movementIntent().isEmpty());
    assertTrue(session.finalCameraPose().isEmpty());
    assertEquals(1.0f, session.cameraEntityOpacity().sample(1.0f));
  }
}
