package io.github.leawind.thirdperson.internal.logic.base.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.joml.Quaternionf;
import org.junit.jupiter.api.Test;

class CameraOffsetSqueezeTest {
  private static final CameraParameters PARAMETERS = new CameraParameters(4.0, -0.4, 0.2);

  @Test
  void preservesOffsetsBeforeOrAtTheConfiguredStartPitch() {
    assertEquals(PARAMETERS, applyAtPitch(CameraOffsetSqueeze.START_PITCH_DEGREES * 0.5));
  }

  @Test
  void appliesTheConfiguredTransitionSymmetrically() {
    double halfwayPitch =
        (CameraOffsetSqueeze.START_PITCH_DEGREES
                + CameraOffsetSqueeze.CENTERED_PITCH_DEGREES)
            * 0.5;
    CameraParameters upward = applyAtPitch(-halfwayPitch);
    CameraParameters downward = applyAtPitch(halfwayPitch);
    double transitionedProgress =
        CameraOffsetSqueeze.TRANSITION_FUNCTION.applyAsDouble(0.5);
    double expectedMultiplier =
        1.0 - Math.max(0.0, Math.min(1.0, transitionedProgress));

    assertEquals(PARAMETERS.distance(), upward.distance());
    assertEquals(
        PARAMETERS.anchorNdcX() * expectedMultiplier, upward.anchorNdcX(), 1.0e-5);
    assertEquals(
        PARAMETERS.anchorNdcY() * expectedMultiplier, upward.anchorNdcY(), 1.0e-5);
    assertEquals(upward.anchorNdcX(), downward.anchorNdcX(), 1.0e-6);
    assertEquals(upward.anchorNdcY(), downward.anchorNdcY(), 1.0e-6);
  }

  @Test
  void acceptsACustomNormalizedTransitionFunction() {
    double halfwayPitch =
        (CameraOffsetSqueeze.START_PITCH_DEGREES
                + CameraOffsetSqueeze.CENTERED_PITCH_DEGREES)
            * 0.5;
    var rotation = new Quaternionf().rotationX((float) Math.toRadians(halfwayPitch));

    CameraParameters result =
        CameraOffsetSqueeze.apply(PARAMETERS, rotation, progress -> progress * progress);

    assertEquals(PARAMETERS.anchorNdcX() * 0.75, result.anchorNdcX(), 1.0e-5);
    assertEquals(PARAMETERS.anchorNdcY() * 0.75, result.anchorNdcY(), 1.0e-5);
  }

  @Test
  void fullyCentersOffsetsPastTheConfiguredEndPitch() {
    double pitch = pitchPastConfiguredEnd();
    CameraParameters upward = applyAtPitch(-pitch);
    CameraParameters downward = applyAtPitch(pitch);

    assertEquals(new CameraParameters(PARAMETERS.distance(), 0.0, 0.0), upward);
    assertEquals(upward, downward);
  }

  private static CameraParameters applyAtPitch(double pitchDegrees) {
    var rotation = new Quaternionf().rotationX((float) Math.toRadians(pitchDegrees));
    return CameraOffsetSqueeze.apply(PARAMETERS, rotation);
  }

  private static double pitchPastConfiguredEnd() {
    return CameraOffsetSqueeze.CENTERED_PITCH_DEGREES
        + (90.0 - CameraOffsetSqueeze.CENTERED_PITCH_DEGREES) * 0.5;
  }
}
