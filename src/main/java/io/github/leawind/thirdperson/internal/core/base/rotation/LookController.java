package io.github.leawind.thirdperson.internal.core.base.rotation;

import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import io.github.leawind.thirdperson.internal.core.base.math.FiniteMath;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import org.joml.Vector3f;

/// Stores two-axis look input and exposes it as a Perspective API-convention quaternion.
public final class LookController {
  private static final float TURN_SCALE = 0.15f;

  private boolean initialized;
  private float pitchDegrees;
  private float yawDegrees;

  public boolean isInitialized() {
    return initialized;
  }

  public void initialize(float pitchDegrees, float yawDegrees) {
    if (!Float.isFinite(pitchDegrees) || !Float.isFinite(yawDegrees)) {
      reset();
      return;
    }
    this.pitchDegrees = PlayerRotationGeometry.clampPitch(pitchDegrees);
    this.yawDegrees = PlayerRotationGeometry.wrapDegrees(yawDegrees);
    initialized = true;
  }

  /// Initializes the local look direction while preserving a world-space camera direction.
  public void initializeFromWorldRotation(
      Quaternionfc worldFromCamera, Quaternionfc worldFromPivot) {
    Objects.requireNonNull(worldFromCamera, "worldFromCamera");
    Objects.requireNonNull(worldFromPivot, "worldFromPivot");
    if (!FiniteMath.isFinite(worldFromCamera) || !FiniteMath.isFinite(worldFromPivot)) {
      reset();
      return;
    }
    var directionWorld = PerspectiveMath.getForward(worldFromCamera, new Vector3f());
    var directionPivot =
        new Quaternionf(worldFromPivot)
            .normalize()
            .conjugate()
            .transform(directionWorld, new Vector3f());
    Vector2f pitchYaw = PerspectiveMath.directionToEulerDeg(directionPivot, new Vector2f());
    initialize(pitchYaw.x, pitchYaw.y);
  }

  /// Applies the raw arguments received by `Entity.turn`.
  ///
  /// Returns false when the controller is not initialized or the input is invalid, allowing the
  /// caller to preserve vanilla handling.
  public boolean turn(double rawYaw, double rawPitch) {
    if (!initialized || !Double.isFinite(rawYaw) || !Double.isFinite(rawPitch)) {
      return false;
    }
    yawDegrees = PlayerRotationGeometry.wrapDegrees(yawDegrees + (float) rawYaw * TURN_SCALE);
    pitchDegrees = PlayerRotationGeometry.clampPitch(pitchDegrees + (float) rawPitch * TURN_SCALE);
    return true;
  }

  public boolean copyRotation(Quaternionf destination) {
    if (!initialized) {
      return false;
    }
    PerspectiveMath.eulerDegToQuat(pitchDegrees, yawDegrees, 0.0f, destination);
    return true;
  }

  /// Returns the world-space facing direction after composing the pivot and local rotations.
  public Optional<LookRotation> facingRotation(Quaternionfc worldFromPivot) {
    Objects.requireNonNull(worldFromPivot, "worldFromPivot");
    if (!initialized || !FiniteMath.isFinite(worldFromPivot)) {
      return Optional.empty();
    }
    var pivotFromCamera = new Quaternionf();
    copyRotation(pivotFromCamera);
    var worldFromCamera =
        new Quaternionf(worldFromPivot).normalize().mul(pivotFromCamera).normalize();
    return MovementDirection.facingRotation(
        PerspectiveMath.getForward(worldFromCamera, new Vector3f()));
  }

  public float pitchDegrees() {
    return pitchDegrees;
  }

  public float yawDegrees() {
    return yawDegrees;
  }

  public void reset() {
    initialized = false;
    pitchDegrees = 0.0f;
    yawDegrees = 0.0f;
  }
}
