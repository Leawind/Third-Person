package io.github.leawind.thirdperson.internal.logic.base.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class CameraPivotSmootherTest {
  private static final double TICK_SECONDS = 0.05;
  private static final CameraSmoothingParameters SMOOTH = smoothing(0.05, 0.05);

  @Test
  void renderSamplingDoesNotChangeMovingTargetTickStates() {
    var fewSamples = new CameraPivotSmoother();
    var manySamples = new CameraPivotSmoother();
    fewSamples.updateTick(new Vector3d(), TICK_SECONDS, SMOOTH).orElseThrow();
    manySamples.updateTick(new Vector3d(), TICK_SECONDS, SMOOTH).orElseThrow();

    for (int tick = 1; tick <= 20; tick++) {
      var previousTarget = new Vector3d(tick - 1, (tick - 1) * 0.5, -(tick - 1));
      var tickTarget = new Vector3d(tick, tick * 0.5, -tick);
      fewSamples.updateTick(tickTarget, TICK_SECONDS, SMOOTH).orElseThrow();
      manySamples.updateTick(tickTarget, TICK_SECONDS, SMOOTH).orElseThrow();

      fewSamples
          .sample(new Vector3d(previousTarget).lerp(tickTarget, 0.5), 0.5, SMOOTH)
          .orElseThrow();
      for (int frame = 1; frame <= 12; frame++) {
        double partialTick = frame / 12.0;
        manySamples
            .sample(new Vector3d(previousTarget).lerp(tickTarget, partialTick), partialTick, SMOOTH)
            .orElseThrow();
      }
    }

    assertEquals(
        fewSamples.sample(new Vector3d(20.0, 10.0, -20.0), 1.0, SMOOTH).orElseThrow(),
        manySamples.sample(new Vector3d(20.0, 10.0, -20.0), 1.0, SMOOTH).orElseThrow());
  }

  @Test
  void interpolatesSmoothedTickStatesAlongsideTheMovingEntity() {
    var smoother = new CameraPivotSmoother();
    smoother.updateTick(new Vector3d(), TICK_SECONDS, SMOOTH).orElseThrow();
    smoother.updateTick(new Vector3d(10.0, 4.0, -2.0), TICK_SECONDS, SMOOTH).orElseThrow();

    assertEquals(
        new Vector3d(1.25, 0.5, -0.25),
        smoother.sample(new Vector3d(2.5, 1.0, -0.5), 0.25, SMOOTH).orElseThrow());
    assertEquals(
        new Vector3d(2.5, 1.0, -0.5),
        smoother.sample(new Vector3d(5.0, 2.0, -1.0), 0.5, SMOOTH).orElseThrow());
    assertEquals(
        new Vector3d(3.75, 1.5, -0.75),
        smoother.sample(new Vector3d(7.5, 3.0, -1.5), 0.75, SMOOTH).orElseThrow());
  }

  @Test
  void zeroHalfLifeUsesTheCurrentInterpolatedTargetPerAxis() {
    var smoother = new CameraPivotSmoother();
    smoother.updateTick(new Vector3d(), TICK_SECONDS, SMOOTH).orElseThrow();
    smoother.updateTick(new Vector3d(10.0, 10.0, 10.0), TICK_SECONDS, SMOOTH).orElseThrow();

    var immediateHorizontal = smoothing(0.0, 0.05);
    assertEquals(
        new Vector3d(4.0, 2.0, -4.0),
        smoother.sample(new Vector3d(4.0, 8.0, -4.0), 0.4, immediateHorizontal).orElseThrow());

    var immediateVertical = smoothing(0.05, 0.0);
    assertEquals(
        new Vector3d(2.0, 8.0, 2.0),
        smoother.sample(new Vector3d(8.0, 8.0, 8.0), 0.4, immediateVertical).orElseThrow());
  }

  @Test
  void resetMakesTheNextRenderSampleSnapToItsTarget() {
    var smoother = new CameraPivotSmoother();
    smoother.updateTick(new Vector3d(), TICK_SECONDS, SMOOTH).orElseThrow();
    smoother.updateTick(new Vector3d(10.0, 10.0, 10.0), TICK_SECONDS, SMOOTH).orElseThrow();

    smoother.reset();

    assertEquals(
        new Vector3d(30.0, 20.0, 10.0),
        smoother.sample(new Vector3d(30.0, 20.0, 10.0), 0.3, SMOOTH).orElseThrow());
  }

  private static CameraSmoothingParameters smoothing(double horizontal, double vertical) {
    return new CameraSmoothingParameters(horizontal, vertical, 0.0, 0.0, 0.0, 0.0);
  }
}
