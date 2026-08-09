package io.github.leawind.thirdperson.internal.core.base.pivot;

import io.github.leawind.thirdperson.internal.core.base.math.FiniteMath;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Immutable pose whose rotation maps pivot-space vectors into world space.
public final class PivotPose {
  private static final float MIN_QUATERNION_LENGTH_SQUARED = 1.0e-12f;

  private final Vector3d positionWorld;
  private final Quaternionf worldFromPivot;

  private PivotPose(Vector3dc positionWorld, Quaternionfc worldFromPivot) {
    this.positionWorld = new Vector3d(positionWorld);
    this.worldFromPivot = new Quaternionf(worldFromPivot).normalize();
  }

  public static PivotPose identity(Vector3dc positionWorld) {
    return tryCreate(positionWorld, new Quaternionf()).orElseThrow();
  }

  public static Optional<PivotPose> tryCreate(
      Vector3dc positionWorld, Quaternionfc worldFromPivot) {
    Objects.requireNonNull(positionWorld, "positionWorld");
    Objects.requireNonNull(worldFromPivot, "worldFromPivot");
    float lengthSquared = worldFromPivot.lengthSquared();
    if (!FiniteMath.isFinite(positionWorld)
        || !FiniteMath.isFinite(worldFromPivot)
        || !Float.isFinite(lengthSquared)
        || lengthSquared <= MIN_QUATERNION_LENGTH_SQUARED) {
      return Optional.empty();
    }
    return Optional.of(new PivotPose(positionWorld, worldFromPivot));
  }

  public Vector3d copyPositionWorld(Vector3d destination) {
    return Objects.requireNonNull(destination, "destination").set(positionWorld);
  }

  public Quaternionf copyWorldFromPivot(Quaternionf destination) {
    return Objects.requireNonNull(destination, "destination").set(worldFromPivot);
  }

  public PivotPose withPositionWorld(Vector3dc value) {
    return tryCreate(value, worldFromPivot).orElseThrow();
  }

  public PivotPose withWorldFromPivot(Quaternionfc value) {
    return tryCreate(positionWorld, value).orElseThrow();
  }
}
