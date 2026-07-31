package io.github.leawind.thirdperson.internal.logic.base.rotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PlayerRotationGeometryTest {
  @Test
  void clampsHeadYawToTheVanillaFiftyDegreeLimit() {
    assertEquals(80.0f, PlayerRotationGeometry.clampYawAround(120.0f, 30.0f, 50.0f));
    assertEquals(-20.0f, PlayerRotationGeometry.clampYawAround(-100.0f, 30.0f, 50.0f));
  }

  @Test
  void clampsCorrectlyAcrossTheWrappedAngleBoundary() {
    assertEquals(-140.0f, PlayerRotationGeometry.clampYawAround(-100.0f, 170.0f, 50.0f));
    assertEquals(120.0f, PlayerRotationGeometry.clampYawAround(80.0f, 170.0f, 50.0f));
  }

  @Test
  void rejectsInvalidClampArguments() {
    assertThrows(
        IllegalArgumentException.class,
        () -> PlayerRotationGeometry.clampYawAround(Float.NaN, 0.0f, 50.0f));
    assertThrows(
        IllegalArgumentException.class,
        () -> PlayerRotationGeometry.clampYawAround(0.0f, 0.0f, 181.0f));
  }
}
