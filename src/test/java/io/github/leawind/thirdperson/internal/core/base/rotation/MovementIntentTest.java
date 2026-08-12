package io.github.leawind.thirdperson.internal.core.base.rotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Quaternionf;
import org.junit.jupiter.api.Test;

class MovementIntentTest {
  @Test
  void planarAndCameraSpaceDirectionsShareThePivotFrame() {
    Quaternionf worldFromPivot = new Quaternionf().rotationY((float) Math.toRadians(-90.0));
    Quaternionf pivotFromCamera =
        new Quaternionf().rotationX((float) Math.toRadians(45.0));
    MovementIntent intent =
        MovementIntent.tryCreate(0.0f, 1.0f, 0.0f, pivotFromCamera, worldFromPivot).orElseThrow();

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
                new Quaternionf())
            .orElseThrow();

    assertFalse(zero.hasDirectionalImpulse(0.0));
    assertTrue(zero.pivotPlaneFacingRotation().isEmpty());
    assertTrue(
        MovementIntent.tryCreate(
                Float.NaN,
                0.0f,
                0.0f,
                new Quaternionf(),
                new Quaternionf())
            .isEmpty());
    assertTrue(
        MovementIntent.tryCreate(
                0.0f, 1.0f, 0.0f, new Quaternionf(), new Quaternionf(0.0f, 0.0f, 0.0f, 0.0f))
            .isEmpty());
  }
}
