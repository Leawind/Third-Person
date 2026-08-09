package io.github.leawind.thirdperson.internal.core.base.rotation;

import io.github.leawind.thirdperson.internal.core.base.pivot.PivotPose;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

/// One raw movement input with both pivot-plane and full-camera world-space interpretations.
public final class MovementIntent {
  private final float leftImpulse;
  private final float forwardImpulse;
  private final Vector3f pivotPlaneDirectionWorld;
  private final Vector3f cameraSpaceDirectionWorld;

  private MovementIntent(
      float leftImpulse,
      float forwardImpulse,
      Vector3f pivotPlaneDirectionWorld,
      Vector3f cameraSpaceDirectionWorld) {
    this.leftImpulse = leftImpulse;
    this.forwardImpulse = forwardImpulse;
    this.pivotPlaneDirectionWorld = new Vector3f(pivotPlaneDirectionWorld);
    this.cameraSpaceDirectionWorld = new Vector3f(cameraSpaceDirectionWorld);
  }

  public static Optional<MovementIntent> tryCreate(
      float leftImpulse,
      float forwardImpulse,
      float localYawDegrees,
      Quaternionfc pivotFromCamera,
      PivotPose pivotPose) {
    Objects.requireNonNull(pivotFromCamera, "pivotFromCamera");
    Objects.requireNonNull(pivotPose, "pivotPose");
    var worldFromPivot = pivotPose.copyWorldFromPivot(new Quaternionf());
    var worldFromCamera = new Quaternionf(worldFromPivot).mul(pivotFromCamera).normalize();
    var pivotPlane =
        MovementDirection.pivotPlaneWorld(
                leftImpulse, forwardImpulse, localYawDegrees, worldFromPivot)
            .orElse(null);
    var cameraSpace =
        MovementDirection.cameraSpaceWorld(leftImpulse, forwardImpulse, worldFromCamera)
            .orElse(null);
    if (pivotPlane == null || cameraSpace == null) {
      return Optional.empty();
    }
    return Optional.of(
        new MovementIntent(leftImpulse, forwardImpulse, pivotPlane, cameraSpace));
  }

  public float leftImpulse() {
    return leftImpulse;
  }

  public float forwardImpulse() {
    return forwardImpulse;
  }

  public Vector3f copyPivotPlaneDirectionWorld(Vector3f destination) {
    return Objects.requireNonNull(destination, "destination").set(pivotPlaneDirectionWorld);
  }

  public Vector3f copyCameraSpaceDirectionWorld(Vector3f destination) {
    return Objects.requireNonNull(destination, "destination").set(cameraSpaceDirectionWorld);
  }

  public boolean hasDirectionalImpulse(double minimumMagnitude) {
    return MovementDirection.hasDirectionalImpulse(leftImpulse, forwardImpulse, minimumMagnitude);
  }

  public Optional<LookRotation> pivotPlaneFacingRotation() {
    return MovementDirection.facingRotation(pivotPlaneDirectionWorld);
  }

  public Optional<LookRotation> cameraSpaceFacingRotation() {
    return MovementDirection.facingRotation(cameraSpaceDirectionWorld);
  }
}
