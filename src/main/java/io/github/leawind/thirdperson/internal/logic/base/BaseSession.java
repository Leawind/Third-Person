package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.logic.base.CameraPose;
import io.github.leawind.thirdperson.internal.logic.base.CameraSmoother;
import io.github.leawind.thirdperson.internal.logic.base.LookController;
import io.github.leawind.thirdperson.internal.logic.base.PlayerRotationController;
import java.util.Objects;
import java.util.Optional;

/// Minecraft-independent mutable state for one active client session.
public final class BaseSession {
  private boolean perspectiveActive;
  private final LookController lookController = new LookController();
  private final PlayerRotationController playerRotationController = new PlayerRotationController();
  private final CameraSmoother cameraSmoother = new CameraSmoother();
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
