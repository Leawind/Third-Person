package io.github.leawind.thirdperson.internal.logic.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class WorldRayTest {
  @Test
  void createsNormalizedDefensiveCopies() {
    var origin = new Vector3d(1.0, 2.0, 3.0);
    var direction = new Vector3d(0.0, 0.0, 4.0);
    WorldRay ray = WorldRay.tryCreate(origin, direction).orElseThrow();

    origin.set(Double.NaN);
    direction.zero();
    Vector3d copiedOrigin = ray.copyOrigin(new Vector3d());
    Vector3d copiedDirection = ray.copyDirection(new Vector3d());
    copiedOrigin.zero();
    copiedDirection.set(Double.NaN);

    assertEquals(new Vector3d(1.0, 2.0, 3.0), ray.copyOrigin(new Vector3d()));
    assertEquals(new Vector3d(0.0, 0.0, 1.0), ray.copyDirection(new Vector3d()));
    assertEquals(new Vector3d(1.0, 2.0, 8.0), ray.pointAt(5.0).orElseThrow());
  }

  @Test
  void towardPassesThroughTarget() {
    Vector3d origin = new Vector3d(-2.0, 1.0, 3.0);
    Vector3d target = new Vector3d(4.0, 5.0, 6.0);
    WorldRay ray = WorldRay.toward(origin, target).orElseThrow();

    assertEquals(target, ray.pointAt(origin.distance(target)).orElseThrow());
  }

  @Test
  void rejectsInvalidOrDegenerateGeometry() {
    assertTrue(WorldRay.tryCreate(new Vector3d(), new Vector3d()).isEmpty());
    assertTrue(
        WorldRay.tryCreate(new Vector3d(Double.NaN, 0.0, 0.0), new Vector3d(0.0, 0.0, 1.0))
            .isEmpty());
    assertTrue(
        WorldRay.tryCreate(
                new Vector3d(), new Vector3d(0.0, Double.POSITIVE_INFINITY, 1.0))
            .isEmpty());
    assertTrue(WorldRay.toward(new Vector3d(1.0, 2.0, 3.0), new Vector3d(1.0, 2.0, 3.0)).isEmpty());

    WorldRay ray =
        WorldRay.tryCreate(new Vector3d(), new Vector3d(0.0, 0.0, 1.0)).orElseThrow();
    assertTrue(ray.pointAt(Double.NaN).isEmpty());
    assertTrue(ray.pointAt(Double.POSITIVE_INFINITY).isEmpty());
    assertTrue(ray.pointAt(-1.0).isEmpty());
  }
}
