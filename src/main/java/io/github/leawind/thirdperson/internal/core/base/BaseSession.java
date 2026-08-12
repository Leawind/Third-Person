package io.github.leawind.thirdperson.internal.core.base;

import io.github.leawind.thirdperson.internal.core.base.camera.CameraPose;
import io.github.leawind.thirdperson.internal.core.base.camera.CameraSmoother;
import io.github.leawind.thirdperson.internal.core.base.math.FiniteMath;
import io.github.leawind.thirdperson.internal.core.base.rotation.LookController;
import io.github.leawind.thirdperson.internal.core.base.rotation.LookRotation;
import io.github.leawind.thirdperson.internal.core.base.rotation.MovementIntent;
import io.github.leawind.thirdperson.internal.core.base.rotation.PlayerRotationController;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

/// Minecraft-independent mutable state for one active client session.
public final class BaseSession {
  private static final float MIN_QUATERNION_LENGTH_SQUARED = 1.0e-12f;

  private boolean perspectiveActive;
  private final LookController lookController = new LookController();
  private final PlayerRotationController playerRotationController = new PlayerRotationController();
  private final CameraSmoother cameraSmoother = new CameraSmoother();
  private MovementIntent movementIntent;
  private Quaternionf worldFromPivot;
  private CameraPose finalCameraPose;

  public boolean isPerspectiveActive() {
    return perspectiveActive;
  }

  public boolean isControllingCamera() {
    return perspectiveActive;
  }

  public LookController lookController() {
    return lookController;
  }

  public PlayerRotationController playerRotationController() {
    return playerRotationController;
  }

  public CameraSmoother cameraSmoother() {
    return cameraSmoother;
  }

  public Optional<MovementIntent> movementIntent() {
    return Optional.ofNullable(movementIntent);
  }

  public boolean copyWorldFromPivot(Quaternionf destination) {
    Objects.requireNonNull(destination, "destination");
    if (worldFromPivot == null) {
      return false;
    }
    destination.set(worldFromPivot);
    return true;
  }

  public Optional<LookRotation> cameraFacingRotation() {
    if (worldFromPivot == null) {
      return Optional.empty();
    }
    return lookController.facingRotation(worldFromPivot);
  }

  /// Records the externally supplied pivot frame without coupling it to a position strategy.
  public boolean recordPivotRotation(Quaternionfc value) {
    Objects.requireNonNull(value, "value");
    float lengthSquared = value.lengthSquared();
    if (!FiniteMath.isFinite(value)
        || !Float.isFinite(lengthSquared)
        || lengthSquared <= MIN_QUATERNION_LENGTH_SQUARED) {
      worldFromPivot = null;
      return false;
    }
    if (worldFromPivot == null) {
      worldFromPivot = new Quaternionf();
    }
    worldFromPivot.set(value).normalize();
    return true;
  }

  public void recordMovementIntent(MovementIntent value) {
    movementIntent = Objects.requireNonNull(value, "value");
  }

  public void clearMovementIntent() {
    movementIntent = null;
  }

  public Optional<CameraPose> finalCameraPose() {
    return Optional.ofNullable(finalCameraPose);
  }

  public void recordFinalCameraPose(CameraPose pose) {
    finalCameraPose = Objects.requireNonNull(pose, "pose");
  }

  /// Clears state tied to the identity and previous poses of the camera entity.
  public void resetCameraTracking() {
    lookController.reset();
    cameraSmoother.reset();
    clearMovementIntent();
    worldFromPivot = null;
    finalCameraPose = null;
  }

  public void activatePerspective() {
    perspectiveActive = true;
  }

  public void reset() {
    perspectiveActive = false;
    resetCameraTracking();
    playerRotationController.reset();
  }
}
