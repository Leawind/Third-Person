package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.logic.base.camera.CameraPose;
import io.github.leawind.thirdperson.internal.logic.base.camera.CameraSmoother;
import io.github.leawind.thirdperson.internal.logic.base.pivot.CameraPivotTracker;
import io.github.leawind.thirdperson.internal.logic.base.rotation.LookController;
import io.github.leawind.thirdperson.internal.logic.base.rotation.MovementIntent;
import io.github.leawind.thirdperson.internal.logic.base.rotation.PlayerRotationController;
import java.util.Objects;
import java.util.Optional;

/// Minecraft-independent mutable state for one active client session.
public final class BaseSession {
  private boolean perspectiveActive;
  private final LookController lookController = new LookController();
  private final PlayerRotationController playerRotationController = new PlayerRotationController();
  private final CameraPivotTracker cameraPivotTracker = new CameraPivotTracker();
  private final CameraSmoother cameraSmoother = new CameraSmoother();
  private MovementIntent movementIntent;
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

  public CameraPivotTracker cameraPivotTracker() {
    return cameraPivotTracker;
  }

  public Optional<MovementIntent> movementIntent() {
    return Optional.ofNullable(movementIntent);
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
    cameraPivotTracker.reset();
    cameraSmoother.reset();
    clearMovementIntent();
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
