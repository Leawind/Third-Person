package io.github.leawind.thirdperson.internal.logic.base.rotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class PlayerRotationControllerTest {
  @Test
  void immediateDecisionAppliesTheTargetExactly() {
    var controller = new PlayerRotationController();
    var decision =
        PlayerRotationParameters.of(
            PlayerRotationMode.PARALLEL_WITH_CAMERA, 0.0, PlayerRotationSmoothing.IMMEDIATE);

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
        PlayerRotationParameters.of(
            PlayerRotationMode.PARALLEL_WITH_CAMERA,
            0.05,
            PlayerRotationSmoothing.TICK_INTERPOLATED);

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
        PlayerRotationParameters.of(
            PlayerRotationMode.PARALLEL_WITH_CAMERA,
            0.05,
            PlayerRotationSmoothing.TICK_INTERPOLATED);
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

  @Test
  void samplesBetweenClientTickEndpoints() {
    var controller = new PlayerRotationController();
    var decision =
        PlayerRotationParameters.of(
            PlayerRotationMode.PARALLEL_WITH_CAMERA,
            0.05,
            PlayerRotationSmoothing.TICK_INTERPOLATED);

    controller.update(
        new LookRotation(0.0f, 0.0f), new LookRotation(90.0f, 40.0f), 0.05, decision);

    LookRotation previous = controller.sample(0.0).orElseThrow();
    LookRotation halfway = controller.sample(0.5).orElseThrow();
    LookRotation current = controller.sample(1.0).orElseThrow();
    assertEquals(0.0f, previous.yawDegrees());
    assertEquals(22.5f, halfway.yawDegrees());
    assertEquals(10.0f, halfway.pitchDegrees());
    assertEquals(45.0f, current.yawDegrees());
    assertEquals(20.0f, current.pitchDegrees());
  }

  @Test
  void samplingUsesTheShortestPathAcrossWrappedYaw() {
    var controller = new PlayerRotationController();
    var decision =
        PlayerRotationParameters.of(
            PlayerRotationMode.PARALLEL_WITH_CAMERA,
            0.05,
            PlayerRotationSmoothing.TICK_INTERPOLATED);

    controller.update(
        new LookRotation(179.0f, 0.0f), new LookRotation(-177.0f, 0.0f), 0.05, decision);

    assertEquals(-180.0f, controller.sample(0.5).orElseThrow().yawDegrees(), 1.0e-5f);
  }

  @Test
  void resetClearsInterpolationAndTheLastDecision() {
    var controller = new PlayerRotationController();
    var decision =
        PlayerRotationParameters.of(
            PlayerRotationMode.PARALLEL_WITH_CAMERA, 0.0, PlayerRotationSmoothing.IMMEDIATE);
    controller.update(
        new LookRotation(0.0f, 0.0f), new LookRotation(90.0f, 0.0f), 0.05, decision);

    controller.reset();

    assertFalse(controller.sample(0.5).isPresent());
    assertFalse(controller.parameters().isPresent());
  }

  @Test
  void frameExponentialSmoothingUsesTheActualFrameDelta() {
    var controller = new PlayerRotationController();
    var decision =
        PlayerRotationParameters.of(
            PlayerRotationMode.PARALLEL_WITH_CAMERA,
            0.1,
            PlayerRotationSmoothing.FRAME_EXPONENTIAL);
    controller.update(
        new LookRotation(0.0f, 0.0f), new LookRotation(90.0f, 40.0f), 0.05, decision);

    LookRotation result = controller.updateFrame(new LookRotation(90.0f, 40.0f), 0.1);

    assertEquals(45.0f, result.yawDegrees(), 1.0e-5f);
    assertEquals(20.0f, result.pitchDegrees(), 1.0e-5f);
  }
}
