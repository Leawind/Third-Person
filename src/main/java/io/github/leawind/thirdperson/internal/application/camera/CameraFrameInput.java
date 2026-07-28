package io.github.leawind.thirdperson.internal.application.camera;

import io.github.leawind.thirdperson.internal.core.math.FiniteMath;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Immutable Minecraft-independent input for one camera update.
public final class CameraFrameInput {
  private static final float MIN_QUATERNION_LENGTH_SQUARED = 1.0e-12f;

  private final Vector3d pivot;
  private final Quaternionf rotation;
  private final float baseFovDegrees;
  private final double aspectRatio;
  private final boolean flyingOrSwimming;
  private final double deltaSeconds;

  private CameraFrameInput(
      Vector3dc pivot,
      Quaternionfc rotation,
      float baseFovDegrees,
      double aspectRatio,
      boolean flyingOrSwimming,
      double deltaSeconds) {
    this.pivot = new Vector3d(pivot);
    this.rotation = new Quaternionf(rotation).normalize();
    this.baseFovDegrees = baseFovDegrees;
    this.aspectRatio = aspectRatio;
    this.flyingOrSwimming = flyingOrSwimming;
    this.deltaSeconds = deltaSeconds;
  }

  public static Optional<CameraFrameInput> tryCreate(
      Vector3dc pivot,
      Quaternionfc rotation,
      float baseFovDegrees,
      double aspectRatio,
      boolean flyingOrSwimming,
      double deltaSeconds) {
    Objects.requireNonNull(pivot, "pivot");
    Objects.requireNonNull(rotation, "rotation");
    float rotationLengthSquared = rotation.lengthSquared();
    if (!FiniteMath.isFinite(pivot)
        || !FiniteMath.isFinite(rotation)
        || !Float.isFinite(rotationLengthSquared)
        || rotationLengthSquared <= MIN_QUATERNION_LENGTH_SQUARED
        || !Float.isFinite(baseFovDegrees)
        || baseFovDegrees <= 0.0f
        || baseFovDegrees >= 180.0f
        || !Double.isFinite(aspectRatio)
        || aspectRatio <= 0.0
        || !Double.isFinite(deltaSeconds)
        || deltaSeconds < 0.0) {
      return Optional.empty();
    }
    return Optional.of(
        new CameraFrameInput(
            pivot, rotation, baseFovDegrees, aspectRatio, flyingOrSwimming, deltaSeconds));
  }

  public Vector3d copyPivot(Vector3d destination) {
    return Objects.requireNonNull(destination, "destination").set(pivot);
  }

  public Quaternionf copyRotation(Quaternionf destination) {
    return Objects.requireNonNull(destination, "destination").set(rotation);
  }

  public float baseFovDegrees() {
    return baseFovDegrees;
  }

  public double aspectRatio() {
    return aspectRatio;
  }

  public boolean flyingOrSwimming() {
    return flyingOrSwimming;
  }

  public double deltaSeconds() {
    return deltaSeconds;
  }
}
