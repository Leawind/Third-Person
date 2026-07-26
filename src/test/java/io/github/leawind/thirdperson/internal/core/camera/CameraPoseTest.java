package io.github.leawind.thirdperson.internal.core.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class CameraPoseTest {
  @Test
  void createsNormalizedDefensiveCopy() {
    var position = new Vector3d(1.0, 2.0, 3.0);
    var rotation = new Quaternionf(0.0f, 2.0f, 0.0f, 2.0f);
    var pose = CameraPose.tryCreate(position, rotation, 70.0f).orElseThrow();

    position.set(Double.NaN);
    rotation.set(0.0f, 0.0f, 0.0f, 0.0f);

    assertEquals(new Vector3d(1.0, 2.0, 3.0), pose.copyPosition(new Vector3d()));
    assertEquals(1.0f, pose.copyRotation(new Quaternionf()).lengthSquared(), 1.0e-6f);
    assertEquals(70.0f, pose.fovDegrees());
  }

  @Test
  void rejectsInvalidComponents() {
    assertFalse(
        CameraPose.tryCreate(
                new Vector3d(Double.NaN, 0.0, 0.0), new Quaternionf(), 70.0f)
            .isPresent());
    assertFalse(
        CameraPose.tryCreate(
                new Vector3d(), new Quaternionf(0.0f, 0.0f, 0.0f, 0.0f), 70.0f)
            .isPresent());
    assertFalse(CameraPose.tryCreate(new Vector3d(), new Quaternionf(), 180.0f).isPresent());
    assertTrue(CameraPose.tryCreate(new Vector3d(), new Quaternionf(), 70.0f).isPresent());
  }
}
