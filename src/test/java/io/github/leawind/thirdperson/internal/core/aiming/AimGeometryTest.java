package io.github.leawind.thirdperson.internal.core.aiming;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class AimGeometryTest {
  @Test
  void followsMinecraftYawAndPitchConventions() {
    LookRotation forward =
        AimGeometry.lookAt(new Vector3d(), new Vector3d(0.0, 0.0, 1.0)).orElseThrow();
    LookRotation left =
        AimGeometry.lookAt(new Vector3d(), new Vector3d(1.0, 0.0, 0.0)).orElseThrow();
    LookRotation up =
        AimGeometry.lookAt(new Vector3d(), new Vector3d(0.0, 1.0, 0.0)).orElseThrow();

    assertEquals(0.0f, forward.yawDegrees(), 1.0e-6f);
    assertEquals(0.0f, forward.pitchDegrees(), 1.0e-6f);
    assertEquals(-90.0f, left.yawDegrees(), 1.0e-6f);
    assertEquals(-90.0f, up.pitchDegrees(), 1.0e-6f);
  }

  @Test
  void rejectsZeroAndNonFiniteDirections() {
    assertTrue(AimGeometry.lookAt(new Vector3d(), new Vector3d()).isEmpty());
    assertTrue(
        AimGeometry.lookAt(new Vector3d(), new Vector3d(Double.NaN, 0.0, 1.0)).isEmpty());
  }
}
