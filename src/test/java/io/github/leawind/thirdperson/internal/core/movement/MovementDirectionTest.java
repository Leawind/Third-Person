package io.github.leawind.thirdperson.internal.core.movement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class MovementDirectionTest {
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
}
