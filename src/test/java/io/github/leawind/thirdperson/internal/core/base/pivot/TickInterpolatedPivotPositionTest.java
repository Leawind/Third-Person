package io.github.leawind.thirdperson.internal.core.base.pivot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class TickInterpolatedPivotPositionTest {
  private static final double TICK_SECONDS = 0.05;
  private static final CameraPivotSmoothing SMOOTH = new CameraPivotSmoothing(0.05);

  @Test
  void renderSamplingDoesNotChangeTickStates() {
    var fewSamples = new TickInterpolatedPivotPosition();
    var manySamples = new TickInterpolatedPivotPosition();
    fewSamples.updateTick(position(0.0, 0.0, 0.0), TICK_SECONDS, SMOOTH).orElseThrow();
    manySamples.updateTick(position(0.0, 0.0, 0.0), TICK_SECONDS, SMOOTH).orElseThrow();

    for (int tick = 1; tick <= 20; tick++) {
      Vector3d target = position(tick, tick * 0.5, -tick);
      fewSamples.updateTick(target, TICK_SECONDS, SMOOTH).orElseThrow();
      manySamples.updateTick(target, TICK_SECONDS, SMOOTH).orElseThrow();
      fewSamples.sample(target, 0.5, SMOOTH).orElseThrow();
      for (int frame = 1; frame <= 12; frame++) {
        manySamples.sample(target, frame / 12.0, SMOOTH).orElseThrow();
      }
    }

    assertPositionEquals(
        fewSamples.sample(position(20.0, 10.0, -20.0), 1.0, SMOOTH).orElseThrow(),
        manySamples.sample(position(20.0, 10.0, -20.0), 1.0, SMOOTH).orElseThrow());
  }

  @Test
  void positionUsesOneWorldSpaceHalfLifeForAllAxes() {
    var tracker = new TickInterpolatedPivotPosition();
    tracker.updateTick(position(0.0, 0.0, 0.0), TICK_SECONDS, SMOOTH).orElseThrow();
    tracker.updateTick(position(10.0, 4.0, -2.0), TICK_SECONDS, SMOOTH).orElseThrow();

    assertPositionEquals(
        new Vector3d(2.5, 1.0, -0.5),
        tracker.sample(position(5.0, 2.0, -1.0), 0.5, SMOOTH).orElseThrow());
  }

  @Test
  void zeroPositionHalfLifeUsesTheRenderTimeTargetPosition() {
    var tracker = new TickInterpolatedPivotPosition();
    tracker.updateTick(position(0.0, 0.0, 0.0), TICK_SECONDS, SMOOTH).orElseThrow();

    Vector3d result =
        tracker
            .sample(position(8.0, -3.0, 4.0), 0.4, new CameraPivotSmoothing(0.0))
            .orElseThrow();

    assertPositionEquals(new Vector3d(8.0, -3.0, 4.0), result);
  }

  @Test
  void resetMakesTheNextSampleSnapToItsTarget() {
    var tracker = new TickInterpolatedPivotPosition();
    tracker.updateTick(position(0.0, 0.0, 0.0), TICK_SECONDS, SMOOTH).orElseThrow();
    tracker.updateTick(position(10.0, 10.0, 10.0), TICK_SECONDS, SMOOTH).orElseThrow();
    tracker.reset();

    assertPositionEquals(
        new Vector3d(30.0, 20.0, 10.0),
        tracker.sample(position(30.0, 20.0, 10.0), 0.3, SMOOTH).orElseThrow());
  }

  private static Vector3d position(double x, double y, double z) {
    return new Vector3d(x, y, z);
  }

  private static void assertPositionEquals(Vector3d expected, Vector3d actual) {
    assertEquals(expected, actual);
  }
}
