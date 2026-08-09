package io.github.leawind.thirdperson.internal.core.base.rotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

class MovementInputProjectorTest {
  @Test
  void solvesRepresentableInputWithoutChangingMagnitude() {
    var input =
        MovementInputProjector.project(
                new Vector3f(-1.0f, 0.0f, 0.0f),
                new Quaternionf().rotationY((float) Math.toRadians(-90.0)))
            .orElseThrow();

    assertEquals(0.0f, input.leftImpulse(), 1.0e-5f);
    assertEquals(1.0f, input.forwardImpulse(), 1.0e-5f);
  }

  @Test
  void rejectsAComponentThatTwoVanillaImpulsesCannotRepresent() {
    assertTrue(
        MovementInputProjector.project(new Vector3f(0.0f, 1.0f, 0.0f), new Quaternionf())
            .isEmpty());
  }
}
