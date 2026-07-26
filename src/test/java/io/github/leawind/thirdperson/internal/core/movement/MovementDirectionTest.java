package io.github.leawind.thirdperson.internal.core.movement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MovementDirectionTest {
  @Test
  void sprintingImpulseAcceptsEveryDirectionAtTheSameMagnitude() {
    assertTrue(MovementDirection.hasDirectionalImpulse(0.0, 1.0, 0.8));
    assertTrue(MovementDirection.hasDirectionalImpulse(0.0, -1.0, 0.8));
    assertTrue(MovementDirection.hasDirectionalImpulse(1.0, 0.0, 0.8));
    assertTrue(MovementDirection.hasDirectionalImpulse(-1.0, 0.0, 0.8));
    assertTrue(MovementDirection.hasDirectionalImpulse(0.8, 0.0, 0.8));
    assertFalse(MovementDirection.hasDirectionalImpulse(0.5, 0.5, 0.8));
    assertFalse(MovementDirection.hasDirectionalImpulse(0.0, 0.0, 0.0));
    assertFalse(MovementDirection.hasDirectionalImpulse(Double.NaN, 1.0, 0.8));
  }

  @Test
  void forwardInputFacesCameraYaw() {
    assertEquals(
        0.0,
        MovementDirection.facingYawDegrees(0.0, 1.0, 0.0).orElseThrow(),
        1.0e-9);
    assertEquals(
        90.0,
        MovementDirection.facingYawDegrees(0.0, 1.0, 90.0).orElseThrow(),
        1.0e-9);
  }

  @Test
  void lateralInputIsRotatedByCameraYaw() {
    assertEquals(
        -90.0,
        MovementDirection.facingYawDegrees(1.0, 0.0, 0.0).orElseThrow(),
        1.0e-9);
    assertEquals(
        0.0,
        MovementDirection.facingYawDegrees(1.0, 0.0, 90.0).orElseThrow(),
        1.0e-9);
  }

  @Test
  void zeroOrInvalidInputHasNoFacing() {
    assertFalse(MovementDirection.facingYawDegrees(0.0, 0.0, 0.0).isPresent());
    assertFalse(MovementDirection.facingYawDegrees(Double.NaN, 1.0, 0.0).isPresent());
  }

  @Test
  void threeDimensionalFacingFollowsCameraPitchForForwardMovement() {
    var forward = MovementDirection.facingRotation(0.0, 1.0, 45.0, 30.0).orElseThrow();
    var lateral = MovementDirection.facingRotation(1.0, 0.0, 0.0, 60.0).orElseThrow();

    assertEquals(45.0f, forward.yawDegrees(), 1.0e-5f);
    assertEquals(30.0f, forward.pitchDegrees(), 1.0e-5f);
    assertEquals(-90.0f, lateral.yawDegrees(), 1.0e-5f);
    assertEquals(0.0f, lateral.pitchDegrees(), 1.0e-5f);
  }
}
