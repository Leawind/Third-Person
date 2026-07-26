package io.github.leawind.thirdperson.internal.core.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class CollisionRecoveryTest {
  private static final Vector3d PIVOT = new Vector3d();

  @Test
  void collisionContractionIsImmediate() {
    var recovery = new CollisionRecovery();
    recovery.resolve(PIVOT, new Vector3d(0.0, 0.0, 4.0), 0.0).orElseThrow();

    Vector3d result =
        recovery.resolve(PIVOT, new Vector3d(0.0, 0.0, 0.5), 0.001).orElseThrow();

    assertEquals(0.5, result.length(), 1.0e-9);
  }

  @Test
  void expansionIsSmoothAndFrameRateIndependent() {
    var oneFrame = initializedAt(0.5);
    var twoFrames = initializedAt(0.5);

    Vector3d first =
        oneFrame.resolve(PIVOT, new Vector3d(0.0, 0.0, 4.0), 0.1).orElseThrow();
    twoFrames.resolve(PIVOT, new Vector3d(0.0, 0.0, 4.0), 0.05).orElseThrow();
    Vector3d second =
        twoFrames.resolve(PIVOT, new Vector3d(0.0, 0.0, 4.0), 0.05).orElseThrow();

    assertTrue(first.length() > 0.5 && first.length() < 4.0);
    assertEquals(first.length(), second.length(), 1.0e-9);
  }

  @Test
  void zeroDistanceAndInvalidInputFailSafely() {
    var recovery = new CollisionRecovery();

    assertEquals(
        PIVOT,
        recovery.resolve(PIVOT, new Vector3d(), 0.0).orElseThrow());
    assertTrue(
        recovery.resolve(PIVOT, new Vector3d(Double.NaN, 0.0, 0.0), 0.1).isEmpty());
  }

  private static CollisionRecovery initializedAt(double distance) {
    var recovery = new CollisionRecovery();
    recovery.resolve(PIVOT, new Vector3d(0.0, 0.0, distance), 0.0).orElseThrow();
    return recovery;
  }
}
