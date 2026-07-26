package io.github.leawind.thirdperson.internal.core.player;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.leawind.thirdperson.internal.core.aiming.LookRotation;
import org.junit.jupiter.api.Test;

class PlayerRotationControllerTest {
  @Test
  void immediateDecisionAppliesTheTargetExactly() {
    var controller = new PlayerRotationController();
    var decision =
        new PlayerRotationDecision(PlayerRotationTarget.CAMERA_ROTATION, 0.0, true);

    LookRotation result =
        controller.update(
            new LookRotation(20.0f, 5.0f),
            new LookRotation(80.0f, -30.0f),
            0.05,
            decision);

    assertEquals(80.0f, result.yawDegrees());
    assertEquals(-30.0f, result.pitchDegrees());
  }

  @Test
  void smoothingTakesTheShortestPathAcrossWrappedYaw() {
    var controller = new PlayerRotationController();
    var decision =
        new PlayerRotationDecision(
            PlayerRotationTarget.HORIZONTAL_IMPULSE_DIRECTION, 0.05, false);

    LookRotation result =
        controller.update(
            new LookRotation(179.0f, 0.0f),
            new LookRotation(-179.0f, 20.0f),
            0.05,
            decision);

    assertEquals(-180.0f, result.yawDegrees(), 1.0e-5f);
    assertEquals(10.0f, result.pitchDegrees(), 1.0e-5f);
  }

  @Test
  void resetInitializesFromTheNewCurrentRotation() {
    var controller = new PlayerRotationController();
    var decision =
        new PlayerRotationDecision(
            PlayerRotationTarget.HORIZONTAL_IMPULSE_DIRECTION, 0.05, false);
    controller.update(
        new LookRotation(0.0f, 0.0f), new LookRotation(90.0f, 0.0f), 0.05, decision);

    controller.reset();
    LookRotation result =
        controller.update(
            new LookRotation(-90.0f, 40.0f),
            new LookRotation(-90.0f, 40.0f),
            0.05,
            decision);

    assertEquals(-90.0f, result.yawDegrees());
    assertEquals(40.0f, result.pitchDegrees());
  }
}
