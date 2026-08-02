package io.github.leawind.thirdperson.internal.logic.base.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class CameraSmootherTest {
  private static final CameraSmoothingParameters BALANCED =
      new CameraSmoothingParameters(0.1, 0.1, 0.1, 0.1, 0.1, 0.1);

  @Test
  void independentSmoothingIsFrameRateIndependent() {
    CameraInput start = input(0.0, new Quaternionf(), 2.0, 0.0, 0.0, 70.0f);
    CameraInput target =
        input(10.0, new Quaternionf().rotationY(0.5f), 6.0, 0.5, -0.25, 90.0f, 0.8);
    var oneFrame = new CameraSmoother();
    var twoFrames = new CameraSmoother();
    oneFrame.update(start, 0.0, BALANCED).orElseThrow();
    twoFrames.update(start, 0.0, BALANCED).orElseThrow();

    CameraInput first = oneFrame.update(target, 0.1, BALANCED).orElseThrow();
    twoFrames.update(target, 0.05, BALANCED).orElseThrow();
    CameraInput second = twoFrames.update(target, 0.05, BALANCED).orElseThrow();

    assertEquals(first.copyPivot(new Vector3d()).x, second.copyPivot(new Vector3d()).x, 1.0e-9);
    assertEquals(first.parameters().distance(), second.parameters().distance(), 1.0e-9);
    assertEquals(first.parameters().anchorNdcX(), second.parameters().anchorNdcX(), 1.0e-9);
    assertEquals(first.fovDegrees(), second.fovDegrees(), 1.0e-5f);
  }

  @Test
  void equivalentNegativeQuaternionUsesTheShortestArc() {
    var smoother = new CameraSmoother();
    smoother.update(input(0.0, new Quaternionf(), 2.0, 0.0, 0.0, 70.0f), 0.0, BALANCED);

    CameraInput result =
        smoother
            .update(
                input(0.0, new Quaternionf(0.0f, 0.0f, 0.0f, -1.0f), 2.0, 0.0, 0.0, 70.0f),
                0.05,
                BALANCED)
            .orElseThrow();

    Quaternionf rotation = result.copyRotation(new Quaternionf());
    assertTrue(Math.abs(rotation.w) > 0.99999f);
    assertEquals(0.0f, rotation.x, 1.0e-6f);
    assertEquals(0.0f, rotation.y, 1.0e-6f);
    assertEquals(0.0f, rotation.z, 1.0e-6f);
  }

  @Test
  void zeroRotationHalfLifeAppliesMouseRotationImmediately() {
    var smoother = new CameraSmoother();
    smoother.update(input(0.0, new Quaternionf(), 2.0, 0.0, 0.0, 70.0f), 0.0, BALANCED);
    var targetRotation = new Quaternionf().rotationYXZ(0.8f, -0.3f, 0.0f);
    var immediateRotation = new CameraSmoothingParameters(0.25, 0.25, 0.0, 0.25, 0.25, 0.25);

    CameraInput result =
        smoother
            .update(input(10.0, targetRotation, 6.0, 0.5, -0.25, 90.0f), 0.01, immediateRotation)
            .orElseThrow();

    Quaternionf actual = result.copyRotation(new Quaternionf());
    assertEquals(1.0f, Math.abs(actual.dot(targetRotation)), 1.0e-6f);
    assertEquals(10.0, result.copyPivot(new Vector3d()).x);
  }

  @Test
  void acceptsTheTickSmoothedPivotWithoutApplyingFrameSmoothingAgain() {
    var smoother = new CameraSmoother();
    smoother.update(input(0.0, new Quaternionf(), 2.0, 0.0, 0.0, 70.0f), 0.0, BALANCED);

    CameraInput result =
        smoother
            .update(input(10.0, new Quaternionf(), 2.0, 0.0, 0.0, 70.0f), 0.001, BALANCED)
            .orElseThrow();

    assertEquals(10.0, result.copyPivot(new Vector3d()).x);
  }

  @Test
  void vanillaFovEffectsRemainImmediateIndependentlyOfFovSmoothing() {
    var smoother = new CameraSmoother();
    smoother.update(input(0.0, new Quaternionf(), 2.0, 0.0, 0.0, 70.0f), 0.0, BALANCED);

    CameraInput result =
        smoother
            .update(input(0.0, new Quaternionf(), 6.0, 0.0, 0.0, 80.5f), 0.01, BALANCED)
            .orElseThrow();

    assertEquals(80.5f, result.fovDegrees());
    assertTrue(result.parameters().distance() < 6.0);
  }

  @Test
  void compositionFovMultiplierUsesItsOwnSmoothingState() {
    var smoother = new CameraSmoother();
    smoother.update(input(0.0, new Quaternionf(), 2.0, 0.0, 0.0, 70.0f), 0.0, BALANCED);

    CameraInput unchangedAtZeroElapsed =
        smoother
            .update(input(0.0, new Quaternionf(), 2.0, 0.0, 0.0, 70.0f, 0.8), 0.0, BALANCED)
            .orElseThrow();
    CameraInput halfway =
        smoother
            .update(input(0.0, new Quaternionf(), 2.0, 0.0, 0.0, 70.0f, 0.8), 0.1, BALANCED)
            .orElseThrow();

    assertEquals(1.0, unchangedAtZeroElapsed.fovMultiplier(), 1.0e-12);
    assertEquals(0.9, halfway.fovMultiplier(), 1.0e-12);
  }

  @Test
  void zeroHalfLivesApplyEveryTargetImmediately() {
    var smoother = new CameraSmoother();
    smoother.update(input(0.0, new Quaternionf(), 2.0, 0.0, 0.0, 70.0f), 0.0, BALANCED);
    var immediate = new CameraSmoothingParameters(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

    CameraInput result =
        smoother
            .update(
                input(8.0, new Quaternionf().rotationY(1.0f), 5.0, 0.4, 0.2, 80.0f),
                0.001,
                immediate)
            .orElseThrow();

    assertEquals(8.0, result.copyPivot(new Vector3d()).x);
    assertEquals(5.0, result.parameters().distance());
    assertEquals(0.4, result.parameters().anchorNdcX());
    assertEquals(80.0f, result.fovDegrees());
  }

  private static CameraInput input(
      double pivotX,
      Quaternionf rotation,
      double distance,
      double offsetX,
      double offsetY,
      float fovDegrees) {
    return CameraInput.tryCreate(
            new Vector3d(pivotX, 0.0, 0.0),
            rotation,
            new CameraParameters(distance, offsetX, offsetY),
            fovDegrees,
            1.0)
        .orElseThrow();
  }

  private static CameraInput input(
      double pivotX,
      Quaternionf rotation,
      double distance,
      double offsetX,
      double offsetY,
      float baseFovDegrees,
      double fovMultiplier) {
    return CameraInput.tryCreate(
            new Vector3d(pivotX, 0.0, 0.0),
            rotation,
            new CameraParameters(distance, offsetX, offsetY),
            baseFovDegrees,
            fovMultiplier)
        .orElseThrow();
  }
}
