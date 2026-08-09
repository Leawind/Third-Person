package io.github.leawind.thirdperson.internal.core.base.rotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.core.base.pivot.PivotPose;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class MovementIntentTest {
  @Test
  void planarAndCameraSpaceDirectionsShareThePivotFrame() {
    PivotPose pivot =
        PivotPose.tryCreate(
                new Vector3d(), new Quaternionf().rotationY((float) Math.toRadians(-90.0)))
            .orElseThrow();
    Quaternionf pivotFromCamera =
        new Quaternionf().rotationX((float) Math.toRadians(45.0));
    MovementIntent intent =
        MovementIntent.tryCreate(0.0f, 1.0f, 0.0f, pivotFromCamera, pivot).orElseThrow();

    var planar = intent.pivotPlaneFacingRotation().orElseThrow();
    var spatial = intent.cameraSpaceFacingRotation().orElseThrow();
    assertEquals(0.0f, planar.pitchDegrees(), 1.0e-5f);
    assertEquals(45.0f, spatial.pitchDegrees(), 1.0e-5f);
    assertTrue(intent.hasDirectionalImpulse(1.0e-5));
  }

  @Test
  void zeroAndInvalidValuesAreHandledWithoutInventingFacing() {
    MovementIntent zero =
        MovementIntent.tryCreate(
                0.0f,
                0.0f,
                0.0f,
                new Quaternionf(),
                PivotPose.identity(new Vector3d()))
            .orElseThrow();

    assertFalse(zero.hasDirectionalImpulse(0.0));
    assertTrue(zero.pivotPlaneFacingRotation().isEmpty());
    assertTrue(
        MovementIntent.tryCreate(
                Float.NaN,
                0.0f,
                0.0f,
                new Quaternionf(),
                PivotPose.identity(new Vector3d()))
            .isEmpty());
  }
}
