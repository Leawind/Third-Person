package io.github.leawind.thirdperson.internal.core.base.pivot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

class CameraPivotTrackerTest {
  private static final double TICK_SECONDS = 0.05;
  private static final CameraPivotSmoothing SMOOTH = new CameraPivotSmoothing(0.05);

  @Test
  void renderSamplingDoesNotChangeTickStates() {
    var fewSamples = new CameraPivotTracker();
    var manySamples = new CameraPivotTracker();
    fewSamples.updateTick(pose(0.0, 0.0, 0.0), TICK_SECONDS, SMOOTH).orElseThrow();
    manySamples.updateTick(pose(0.0, 0.0, 0.0), TICK_SECONDS, SMOOTH).orElseThrow();

    for (int tick = 1; tick <= 20; tick++) {
      PivotPose target = pose(tick, tick * 0.5, -tick);
      fewSamples.updateTick(target, TICK_SECONDS, SMOOTH).orElseThrow();
      manySamples.updateTick(target, TICK_SECONDS, SMOOTH).orElseThrow();
      fewSamples.sample(target, 0.5, SMOOTH).orElseThrow();
      for (int frame = 1; frame <= 12; frame++) {
        manySamples.sample(target, frame / 12.0, SMOOTH).orElseThrow();
      }
    }

    assertPositionEquals(
        fewSamples.sample(pose(20.0, 10.0, -20.0), 1.0, SMOOTH).orElseThrow(),
        manySamples.sample(pose(20.0, 10.0, -20.0), 1.0, SMOOTH).orElseThrow());
  }

  @Test
  void positionUsesOneWorldSpaceHalfLifeForAllAxes() {
    var tracker = new CameraPivotTracker();
    tracker.updateTick(pose(0.0, 0.0, 0.0), TICK_SECONDS, SMOOTH).orElseThrow();
    tracker.updateTick(pose(10.0, 4.0, -2.0), TICK_SECONDS, SMOOTH).orElseThrow();

    assertPositionEquals(
        new Vector3d(2.5, 1.0, -0.5),
        tracker.sample(pose(5.0, 2.0, -1.0), 0.5, SMOOTH).orElseThrow());
  }

  @Test
  void rotationAlwaysUsesTheExternallyControlledTarget() {
    var tracker = new CameraPivotTracker();
    tracker.updateTick(poseWithYaw(0.0f), TICK_SECONDS, SMOOTH).orElseThrow();
    PivotPose result =
        tracker.updateTick(poseWithYaw(90.0f), TICK_SECONDS, SMOOTH).orElseThrow();

    Vector3f forward =
        result.copyWorldFromPivot(new Quaternionf()).transform(new Vector3f(0.0f, 0.0f, 1.0f));
    assertEquals(-1.0f, forward.x, 1.0e-5f);
    assertEquals(0.0f, forward.z, 1.0e-5f);
  }

  @Test
  void renderSamplingUsesTheExternalRotationWithoutTickInterpolation() {
    var tracker = new CameraPivotTracker();
    tracker.updateTick(poseWithYaw(0.0f), TICK_SECONDS, SMOOTH).orElseThrow();
    tracker.updateTick(poseWithYaw(90.0f), TICK_SECONDS, SMOOTH).orElseThrow();

    PivotPose target =
        PivotPose.tryCreate(
                new Vector3d(8.0, -3.0, 4.0),
                new Quaternionf().rotationY((float) Math.toRadians(-135.0)))
            .orElseThrow();
    PivotPose result = tracker.sample(target, 0.4, SMOOTH).orElseThrow();

    assertEquals(
        1.0f,
        Math.abs(
            target
                .copyWorldFromPivot(new Quaternionf())
                .dot(result.copyWorldFromPivot(new Quaternionf()))),
        1.0e-6f);
  }

  @Test
  void zeroPositionHalfLifeUsesTheRenderTimeTargetPosition() {
    var tracker = new CameraPivotTracker();
    tracker.updateTick(pose(0.0, 0.0, 0.0), TICK_SECONDS, SMOOTH).orElseThrow();

    PivotPose result =
        tracker
            .sample(pose(8.0, -3.0, 4.0), 0.4, new CameraPivotSmoothing(0.0))
            .orElseThrow();

    assertPositionEquals(new Vector3d(8.0, -3.0, 4.0), result);
  }

  @Test
  void resetMakesTheNextSampleSnapToItsTarget() {
    var tracker = new CameraPivotTracker();
    tracker.updateTick(pose(0.0, 0.0, 0.0), TICK_SECONDS, SMOOTH).orElseThrow();
    tracker.updateTick(pose(10.0, 10.0, 10.0), TICK_SECONDS, SMOOTH).orElseThrow();
    tracker.reset();

    assertPositionEquals(
        new Vector3d(30.0, 20.0, 10.0),
        tracker.sample(pose(30.0, 20.0, 10.0), 0.3, SMOOTH).orElseThrow());
  }

  private static PivotPose pose(double x, double y, double z) {
    return PivotPose.identity(new Vector3d(x, y, z));
  }

  private static PivotPose poseWithYaw(float yawDegrees) {
    return PivotPose.tryCreate(
            new Vector3d(),
            new Quaternionf().rotationY((float) Math.toRadians(-yawDegrees)))
        .orElseThrow();
  }

  private static void assertPositionEquals(Vector3d expected, PivotPose actual) {
    assertEquals(expected, actual.copyPositionWorld(new Vector3d()));
  }

  private static void assertPositionEquals(PivotPose expected, PivotPose actual) {
    assertPositionEquals(expected.copyPositionWorld(new Vector3d()), actual);
  }
}
