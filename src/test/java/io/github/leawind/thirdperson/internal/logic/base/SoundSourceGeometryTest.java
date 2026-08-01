package io.github.leawind.thirdperson.internal.logic.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.logic.base.camera.CameraPose;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

class SoundSourceGeometryTest {
  private static final double EPSILON = 1.0e-6;

  @Test
  void removesOnlyTheIdentityViewsLateralDisplacement() {
    CameraPose camera =
        CameraPose.tryCreate(new Vector3d(10.0, 20.0, 30.0), new Quaternionf(), 70.0f)
            .orElseThrow();

    Vector3d projected =
        SoundSourceGeometry.projectToViewCenter(new Vector3d(14.0, 23.0, 38.0), camera)
            .orElseThrow();

    assertEquals(10.0, projected.x, EPSILON);
    assertEquals(23.0, projected.y, EPSILON);
    assertEquals(38.0, projected.z, EPSILON);
  }

  @Test
  void followsTheRotatedViewsCenterPlane() {
    Quaternionf rotation = new Quaternionf().rotateY((float) Math.toRadians(63.0));
    Vector3d cameraPosition = new Vector3d(-2.0, 4.0, 7.0);
    CameraPose camera = CameraPose.tryCreate(cameraPosition, rotation, 70.0f).orElseThrow();
    Vector3d source = new Vector3d(5.0, 8.0, 13.0);

    Vector3d projected = SoundSourceGeometry.projectToViewCenter(source, camera).orElseThrow();
    Vector3d leftAxis = new Vector3d(rotation.transform(new Vector3f(1.0f, 0.0f, 0.0f)));
    Vector3d forwardAxis = new Vector3d(rotation.transform(new Vector3f(0.0f, 0.0f, 1.0f)));
    Vector3d before = new Vector3d(source).sub(cameraPosition);
    Vector3d after = new Vector3d(projected).sub(cameraPosition);

    assertEquals(0.0, after.dot(leftAxis), EPSILON);
    assertEquals(before.dot(forwardAxis), after.dot(forwardAxis), EPSILON);
    assertEquals(before.y, after.y, EPSILON);
  }

  @Test
  void rejectsNonFiniteSourcePositions() {
    CameraPose camera =
        CameraPose.tryCreate(new Vector3d(), new Quaternionf(), 70.0f).orElseThrow();

    assertTrue(
        SoundSourceGeometry.projectToViewCenter(new Vector3d(Double.NaN, 0.0, 0.0), camera)
            .isEmpty());
  }
}
