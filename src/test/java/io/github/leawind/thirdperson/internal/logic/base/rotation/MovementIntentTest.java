package io.github.leawind.thirdperson.internal.logic.base.rotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MovementIntentTest {
  @Test
  void inputBecomesForwardWhenPlayerFacesTheWorldMovementDirection() {
    MovementIntent right = new MovementIntent(-1.0f, 0.0f, 0.0f, 0.0f);
    MovementIntent backward = new MovementIntent(0.0f, -1.0f, 0.0f, 0.0f);

    var rightLocal = right.relativeToPlayerYaw(90.0f).orElseThrow();
    var backwardLocal = backward.relativeToPlayerYaw(180.0f).orElseThrow();

    assertEquals(0.0f, rightLocal.leftImpulse(), 1.0e-5f);
    assertEquals(1.0f, rightLocal.forwardImpulse(), 1.0e-5f);
    assertEquals(0.0f, backwardLocal.leftImpulse(), 1.0e-5f);
    assertEquals(1.0f, backwardLocal.forwardImpulse(), 1.0e-5f);
  }

  @Test
  void remappingPreservesInputMagnitude() {
    MovementIntent intent = new MovementIntent(0.6f, 0.8f, 135.0f, 0.0f);

    var local = intent.relativeToPlayerYaw(-47.0f).orElseThrow();

    assertEquals(
        Math.hypot(intent.leftImpulse(), intent.forwardImpulse()),
        Math.hypot(local.leftImpulse(), local.forwardImpulse()),
        1.0e-6);
  }

  @Test
  void facingTargetsUseTheUnmodifiedCameraRelativeIntent() {
    MovementIntent intent = new MovementIntent(-1.0f, 0.0f, 30.0f, 45.0f);

    assertEquals(120.0, intent.facingYawDegrees().orElseThrow(), 1.0e-5);
    assertEquals(120.0f, intent.facingRotation().orElseThrow().yawDegrees(), 1.0e-5f);
    assertEquals(0.0f, intent.facingRotation().orElseThrow().pitchDegrees(), 1.0e-5f);
  }

  @Test
  void zeroAndInvalidValuesAreHandledWithoutInventingMovement() {
    MovementIntent zero = new MovementIntent(0.0f, 0.0f, 90.0f, 0.0f);

    assertFalse(zero.hasDirectionalImpulse(0.0));
    assertFalse(zero.facingYawDegrees().isPresent());
    assertTrue(MovementIntent.tryCreate(Float.NaN, 0.0f, 0.0f, 0.0f).isEmpty());
    assertTrue(zero.relativeToPlayerYaw(Float.NaN).isEmpty());
  }
}
