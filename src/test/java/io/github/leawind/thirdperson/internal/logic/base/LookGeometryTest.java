package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.logic.base.LookRotation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class LookGeometryTest {
  @Test
  void followsMinecraftYawAndPitchConventions() {
    LookRotation forward =
        LookGeometry.lookAt(new Vector3d(), new Vector3d(0.0, 0.0, 1.0)).orElseThrow();
    LookRotation left =
        LookGeometry.lookAt(new Vector3d(), new Vector3d(1.0, 0.0, 0.0)).orElseThrow();
    LookRotation up =
        LookGeometry.lookAt(new Vector3d(), new Vector3d(0.0, 1.0, 0.0)).orElseThrow();

    assertEquals(0.0f, forward.yawDegrees(), 1.0e-6f);
    assertEquals(0.0f, forward.pitchDegrees(), 1.0e-6f);
    assertEquals(-90.0f, left.yawDegrees(), 1.0e-6f);
    assertEquals(-90.0f, up.pitchDegrees(), 1.0e-6f);
  }

  @Test
  void rejectsZeroAndNonFiniteDirections() {
    assertTrue(LookGeometry.lookAt(new Vector3d(), new Vector3d()).isEmpty());
    assertTrue(
        LookGeometry.lookAt(new Vector3d(), new Vector3d(Double.NaN, 0.0, 1.0)).isEmpty());
  }
}
