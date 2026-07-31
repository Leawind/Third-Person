package io.github.leawind.thirdperson.internal.logic.base.rotation;

import java.util.Optional;
import java.util.OptionalDouble;

/// One raw camera-relative movement input together with the camera rotation that defined it.
public record MovementIntent(
    float leftImpulse, float forwardImpulse, float cameraYawDegrees, float cameraPitchDegrees) {
  public MovementIntent {
    if (!Float.isFinite(leftImpulse)
        || !Float.isFinite(forwardImpulse)
        || !Float.isFinite(cameraYawDegrees)
        || !Float.isFinite(cameraPitchDegrees)) {
      throw new IllegalArgumentException("Movement intent values must be finite");
    }
  }

  public static Optional<MovementIntent> tryCreate(
      float leftImpulse, float forwardImpulse, float cameraYawDegrees, float cameraPitchDegrees) {
    try {
      return Optional.of(
          new MovementIntent(leftImpulse, forwardImpulse, cameraYawDegrees, cameraPitchDegrees));
    } catch (IllegalArgumentException ignored) {
      return Optional.empty();
    }
  }

  public boolean hasDirectionalImpulse(double minimumMagnitude) {
    return MovementDirection.hasDirectionalImpulse(leftImpulse, forwardImpulse, minimumMagnitude);
  }

  public OptionalDouble facingYawDegrees() {
    return MovementDirection.facingYawDegrees(leftImpulse, forwardImpulse, cameraYawDegrees);
  }

  public Optional<LookRotation> facingRotation() {
    return MovementDirection.facingRotation(
        leftImpulse, forwardImpulse, cameraYawDegrees, cameraPitchDegrees);
  }

  public Optional<LocalInput> relativeToPlayerYaw(float playerYawDegrees) {
    if (!Float.isFinite(playerYawDegrees)) {
      return Optional.empty();
    }
    double angleRadians = Math.toRadians(cameraYawDegrees - playerYawDegrees);
    double sin = Math.sin(angleRadians);
    double cos = Math.cos(angleRadians);
    double localLeft = leftImpulse * cos - forwardImpulse * sin;
    double localForward = leftImpulse * sin + forwardImpulse * cos;
    if (!Double.isFinite(localLeft) || !Double.isFinite(localForward)) {
      return Optional.empty();
    }
    return Optional.of(new LocalInput((float) localLeft, (float) localForward));
  }

  public record LocalInput(float leftImpulse, float forwardImpulse) {}
}
