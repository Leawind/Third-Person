package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.logic.base.FiniteMath;
import java.util.Objects;
import java.util.Optional;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Mirrors Minecraft's eight-probe third-person camera zoom clipping.
public final class CameraCollisionResolver {
  private static final float PROBE_OFFSET = 0.1F;
  private static final double MIN_DIRECTION_LENGTH = 1.0e-9;

  private CameraCollisionResolver() {}

  public static Optional<Vector3d> resolve(
      Vector3dc pivot, Vector3dc desiredCamera, RayClipper clipper) {
    Objects.requireNonNull(pivot, "pivot");
    Objects.requireNonNull(desiredCamera, "desiredCamera");
    Objects.requireNonNull(clipper, "clipper");
    if (!FiniteMath.isFinite(pivot) || !FiniteMath.isFinite(desiredCamera)) {
      return Optional.empty();
    }

    var direction = new Vector3d(desiredCamera).sub(pivot);
    double allowedDistance = direction.length();
    if (!Double.isFinite(allowedDistance)) {
      return Optional.empty();
    }
    if (allowedDistance <= MIN_DIRECTION_LENGTH) {
      return Optional.of(new Vector3d(pivot));
    }
    direction.div(allowedDistance);

    for (int i = 0; i < 8; i++) {
      float offsetX = ((i & 1) * 2 - 1) * PROBE_OFFSET;
      float offsetY = (((i >> 1) & 1) * 2 - 1) * PROBE_OFFSET;
      float offsetZ = (((i >> 2) & 1) * 2 - 1) * PROBE_OFFSET;
      var from = new Vector3d(pivot).add(offsetX, offsetY, offsetZ);
      var to = new Vector3d(from).fma(allowedDistance, direction);
      Optional<Vector3d> hit = clipper.clip(from, to);
      if (hit == null) {
        return Optional.empty();
      }
      if (hit.isEmpty()) {
        continue;
      }
      Vector3d hitPosition = hit.orElseThrow();
      if (!FiniteMath.isFinite(hitPosition)) {
        return Optional.empty();
      }

      // Vanilla measures from the unshifted camera pivot, not from this probe's origin.
      double hitDistance = hitPosition.distance(pivot);
      if (hitDistance < allowedDistance) {
        allowedDistance = hitDistance;
      }
    }

    return Optional.of(new Vector3d(pivot).fma(allowedDistance, direction));
  }

  @FunctionalInterface
  public interface RayClipper {
    /// Returns the first hit position, or empty when the segment misses.
    Optional<Vector3d> clip(Vector3dc from, Vector3dc to);
  }
}
