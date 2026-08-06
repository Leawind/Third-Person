package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.logic.base.math.FiniteMath;
import java.util.Objects;
import java.util.Optional;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// An immutable, Minecraft-independent ray in world space.
///
/// Mutable JOML values are copied on both input and output.
public final class WorldRay {
  private static final double MIN_DIRECTION_LENGTH_SQUARED = 1.0e-12;

  private final Vector3d origin;
  private final Vector3d direction;

  private WorldRay(Vector3dc origin, Vector3dc direction) {
    this.origin = new Vector3d(origin);
    this.direction = new Vector3d(direction).normalize();
  }

  public static Optional<WorldRay> tryCreate(Vector3dc origin, Vector3dc direction) {
    Objects.requireNonNull(origin, "origin");
    Objects.requireNonNull(direction, "direction");
    if (!FiniteMath.isFinite(origin) || !FiniteMath.isFinite(direction)) {
      return Optional.empty();
    }
    double lengthSquared = direction.lengthSquared();
    if (!Double.isFinite(lengthSquared) || lengthSquared <= MIN_DIRECTION_LENGTH_SQUARED) {
      return Optional.empty();
    }
    return Optional.of(new WorldRay(origin, direction));
  }

  public static Optional<WorldRay> toward(Vector3dc origin, Vector3dc target) {
    Objects.requireNonNull(origin, "origin");
    Objects.requireNonNull(target, "target");
    if (!FiniteMath.isFinite(origin) || !FiniteMath.isFinite(target)) {
      return Optional.empty();
    }
    return tryCreate(origin, new Vector3d(target).sub(origin));
  }

  /// Returns a defensive copy of this ray's origin.
  public Vector3d origin() {
    return new Vector3d(origin);
  }

  /// Returns a defensive copy of this ray's normalized direction.
  public Vector3d direction() {
    return new Vector3d(direction);
  }

  public Optional<Vector3d> pointAt(double distance) {
    if (!Double.isFinite(distance) || distance < 0.0) {
      return Optional.empty();
    }
    Vector3d point = new Vector3d(origin).fma(distance, direction);
    return FiniteMath.isFinite(point) ? Optional.of(point) : Optional.empty();
  }
}
