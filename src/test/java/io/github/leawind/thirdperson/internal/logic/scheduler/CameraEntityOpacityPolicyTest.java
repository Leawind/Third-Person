package io.github.leawind.thirdperson.internal.logic.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CameraEntityOpacityPolicyTest {
  @Test
  void mapsDistanceFromEntityBoundsIntoOpacity() {
    assertEquals(0.0, CameraEntityOpacityPolicy.targetOpacity(0.0), 1.0e-12);
    assertEquals(
        0.5,
        CameraEntityOpacityPolicy.targetOpacity(CameraEntityOpacityPolicy.FADE_DISTANCE * 0.5),
        1.0e-12);
    assertEquals(
        1.0,
        CameraEntityOpacityPolicy.targetOpacity(CameraEntityOpacityPolicy.FADE_DISTANCE),
        1.0e-12);
    assertEquals(1.0, CameraEntityOpacityPolicy.targetOpacity(10.0), 1.0e-12);
  }

  @Test
  void handlesInvalidAndNegativeDistancesSafely() {
    assertEquals(0.0, CameraEntityOpacityPolicy.targetOpacity(-1.0), 1.0e-12);
    assertEquals(1.0, CameraEntityOpacityPolicy.targetOpacity(Double.NaN), 1.0e-12);
  }
}
