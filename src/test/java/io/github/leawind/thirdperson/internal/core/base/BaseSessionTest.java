package io.github.leawind.thirdperson.internal.core.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.core.base.camera.CameraPose;
import io.github.leawind.thirdperson.internal.core.base.rotation.MovementIntent;
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
  void pivotRotationIsNormalizedAndIndependentOfAnyPosition() {
    var session = new BaseSession();
    var source = new Quaternionf().rotationZ(0.75f).scale(4.0f);

    assertTrue(session.recordPivotRotation(source));
    source.identity();

    var stored = new Quaternionf();
    assertTrue(session.copyWorldFromPivot(stored));
    assertEquals(1.0f, stored.lengthSquared(), 1.0e-6f);
    stored.identity();
    var copiedAgain = new Quaternionf();
    assertTrue(session.copyWorldFromPivot(copiedAgain));
    assertFalse(copiedAgain.equals(new Quaternionf(), 1.0e-6f));
    assertFalse(
        session.recordPivotRotation(new Quaternionf(Float.NaN, 0.0f, 0.0f, 1.0f)));
    assertFalse(session.copyWorldFromPivot(stored));
  }

  @Test
  void resettingCameraTrackingPreservesTheActivePerspective() {
    var session = new BaseSession();
    session.activatePerspective();
    session.lookController().initialize(15.0f, 30.0f);
    session.recordMovementIntent(
        MovementIntent.tryCreate(1.0f, 0.0f, 30.0f, new Quaternionf(), new Quaternionf())
            .orElseThrow());
    session.recordPivotRotation(new Quaternionf());
    session.recordFinalCameraPose(
        CameraPose.tryCreate(new Vector3d(), new Quaternionf(), 70.0f).orElseThrow());

    session.resetCameraTracking();

    assertTrue(session.isPerspectiveActive());
    assertFalse(session.lookController().isInitialized());
    assertTrue(session.movementIntent().isEmpty());
    assertFalse(session.copyWorldFromPivot(new Quaternionf()));
    assertTrue(session.finalCameraPose().isEmpty());
  }
}
