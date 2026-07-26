package io.github.leawind.thirdperson.internal.core.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.core.config.SmoothingPreset;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class CameraSmootherTest {
  @Test
  void exponentialPositionSmoothingIsFrameRateIndependent() {
    CameraPose start = pose(0.0, new Quaternionf(), 70.0f);
    CameraPose target = pose(10.0, new Quaternionf(), 90.0f);
    var oneFrame = new CameraSmoother();
    var twoFrames = new CameraSmoother();
    oneFrame.update(start, 0.0, SmoothingPreset.BALANCED).orElseThrow();
    twoFrames.update(start, 0.0, SmoothingPreset.BALANCED).orElseThrow();

    CameraPose first =
        oneFrame.update(target, 0.1, SmoothingPreset.BALANCED).orElseThrow();
    twoFrames.update(target, 0.05, SmoothingPreset.BALANCED).orElseThrow();
    CameraPose second =
        twoFrames.update(target, 0.05, SmoothingPreset.BALANCED).orElseThrow();

    assertEquals(
        first.copyPosition(new Vector3d()).x,
        second.copyPosition(new Vector3d()).x,
        1.0e-9);
    assertEquals(first.fovDegrees(), second.fovDegrees(), 1.0e-5f);
  }

  @Test
  void equivalentNegativeQuaternionUsesTheShortestArc() {
    var smoother = new CameraSmoother();
    smoother.update(pose(0.0, new Quaternionf(), 70.0f), 0.0, SmoothingPreset.BALANCED);

    CameraPose result =
        smoother
            .update(
                pose(0.0, new Quaternionf(0.0f, 0.0f, 0.0f, -1.0f), 70.0f),
                0.05,
                SmoothingPreset.BALANCED)
            .orElseThrow();

    Quaternionf rotation = result.copyRotation(new Quaternionf());
    assertTrue(Math.abs(rotation.w) > 0.99999f);
    assertEquals(0.0f, rotation.x, 1.0e-6f);
    assertEquals(0.0f, rotation.y, 1.0e-6f);
    assertEquals(0.0f, rotation.z, 1.0e-6f);
  }

  @Test
  void offPresetAppliesTargetImmediately() {
    var smoother = new CameraSmoother();
    smoother.update(pose(0.0, new Quaternionf(), 70.0f), 0.0, SmoothingPreset.BALANCED);

    CameraPose result =
        smoother
            .update(pose(8.0, new Quaternionf().rotationY(1.0f), 80.0f), 0.001, SmoothingPreset.OFF)
            .orElseThrow();

    assertEquals(8.0, result.copyPosition(new Vector3d()).x);
    assertEquals(80.0f, result.fovDegrees());
  }

  private static CameraPose pose(double x, Quaternionf rotation, float fovDegrees) {
    return CameraPose.tryCreate(new Vector3d(x, 0.0, 0.0), rotation, fovDegrees)
        .orElseThrow();
  }
}
