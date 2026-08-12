package io.github.leawind.thirdperson.internal.bridge.entity;

import io.github.leawind.thirdperson.internal.core.base.math.FiniteMath;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Immutable rendered eye position and environmental reference frame of an entity.
///
/// This is source data about the entity, not a decision about where the camera pivot should be.
public final class EntityReferencePose {
  private static final float MIN_QUATERNION_LENGTH_SQUARED = 1.0e-12f;

  private final Vector3d eyePositionWorld;
  private final Quaternionf worldFromReference;

  private EntityReferencePose(Vector3dc eyePositionWorld, Quaternionfc worldFromReference) {
    this.eyePositionWorld = new Vector3d(eyePositionWorld);
    this.worldFromReference = new Quaternionf(worldFromReference).normalize();
  }

  public static EntityReferencePose identity(Vector3dc eyePositionWorld) {
    return tryCreate(eyePositionWorld, new Quaternionf()).orElseThrow();
  }

  public static Optional<EntityReferencePose> tryCreate(
      Vector3dc eyePositionWorld, Quaternionfc worldFromReference) {
    Objects.requireNonNull(eyePositionWorld, "eyePositionWorld");
    Objects.requireNonNull(worldFromReference, "worldFromReference");
    float lengthSquared = worldFromReference.lengthSquared();
    if (!FiniteMath.isFinite(eyePositionWorld)
        || !FiniteMath.isFinite(worldFromReference)
        || !Float.isFinite(lengthSquared)
        || lengthSquared <= MIN_QUATERNION_LENGTH_SQUARED) {
      return Optional.empty();
    }
    return Optional.of(new EntityReferencePose(eyePositionWorld, worldFromReference));
  }

  public Vector3d copyEyePositionWorld(Vector3d destination) {
    return Objects.requireNonNull(destination, "destination").set(eyePositionWorld);
  }

  public Quaternionf copyWorldFromReference(Quaternionf destination) {
    return Objects.requireNonNull(destination, "destination").set(worldFromReference);
  }
}
