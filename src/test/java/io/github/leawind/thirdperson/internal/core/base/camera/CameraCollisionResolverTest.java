package io.github.leawind.thirdperson.internal.core.base.camera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Optional;
import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

class CameraCollisionResolverTest {
  private static final Vector3d PIVOT = new Vector3d();
  private static final Vector3d DESIRED = new Vector3d(0.0, 0.0, -4.0);

  @Test
  void castsTheSameEightParallelProbeSegmentsAsVanilla() {
    var starts = new ArrayList<Vector3d>();
    var deltas = new ArrayList<Vector3d>();

    Vector3d resolved =
        CameraCollisionResolver.resolve(
                PIVOT,
                DESIRED,
                (from, to) -> {
                  starts.add(new Vector3d(from));
                  deltas.add(new Vector3d(to).sub(from));
                  return Optional.empty();
                })
            .orElseThrow();

    assertEquals(DESIRED, resolved);
    assertEquals(8, starts.size());
    for (int i = 0; i < 8; i++) {
      assertEquals(((i & 1) * 2 - 1) * 0.1F, starts.get(i).x, 0.0);
      assertEquals((((i >> 1) & 1) * 2 - 1) * 0.1F, starts.get(i).y, 0.0);
      assertEquals((((i >> 2) & 1) * 2 - 1) * 0.1F, starts.get(i).z, 0.0);
      assertEquals(DESIRED, deltas.get(i));
    }
  }

  @Test
  void measuresAHitFromTheUnshiftedPivotLikeVanilla() {
    int[] probe = {0};

    Vector3d resolved =
        CameraCollisionResolver.resolve(
                PIVOT,
                DESIRED,
                (from, to) ->
                    probe[0]++ == 0 ? Optional.of(new Vector3d(from)) : Optional.empty())
            .orElseThrow();

    // A probe beginning in a wall reports a hit at its shifted origin. Measuring from that origin,
    // as the old implementation did, collapses the camera to zero. Vanilla measures from PIVOT.
    double vanillaProbeOriginDistance = new Vector3d(0.1F, 0.1F, 0.1F).length();
    assertEquals(vanillaProbeOriginDistance, resolved.length(), 1.0e-9);
    assertTrue(resolved.z < 0.0);
  }

  @Test
  void shortensSubsequentProbesAfterAHit() {
    var lengths = new ArrayList<Double>();
    int[] probe = {0};

    Vector3d resolved =
        CameraCollisionResolver.resolve(
                PIVOT,
                DESIRED,
                (from, to) -> {
                  lengths.add(from.distance(to));
                  return probe[0]++ == 0
                      ? Optional.of(new Vector3d(0.0, 0.0, -2.0))
                      : Optional.empty();
                })
            .orElseThrow();

    assertEquals(4.0, lengths.get(0), 1.0e-9);
    assertEquals(2.0, lengths.get(1), 1.0e-9);
    assertEquals(new Vector3d(0.0, 0.0, -2.0), resolved);
  }
}
