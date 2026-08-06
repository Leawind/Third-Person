package io.github.leawind.thirdperson.internal.logic.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.logic.base.camera.CameraPose;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class MinecraftCameraRaycastingTest {
  @Test
  void cameraRayComesOnlyFromTheFinalCameraPose() {
    var session = new BaseSession();
    session.recordFinalCameraPose(
        CameraPose.tryCreate(
                new Vector3d(3.0, 4.0, 5.0),
                new Quaternionf().rotationY((float) (Math.PI * 0.5)),
                70.0f)
            .orElseThrow());

    WorldRay ray = MinecraftCameraRaycasting.cameraRay(session).orElseThrow();

    assertEquals(new Vector3d(3.0, 4.0, 5.0), new Vector3d(ray.origin()));
    assertEquals(1.0, ray.direction().x(), 1.0e-6);
    assertEquals(0.0, ray.direction().y(), 1.0e-6);
    assertEquals(0.0, ray.direction().z(), 1.0e-6);
  }

  @Test
  void missingFinalPoseProducesNoCameraRay() {
    assertTrue(MinecraftCameraRaycasting.cameraRay(new BaseSession()).isEmpty());
  }
}
