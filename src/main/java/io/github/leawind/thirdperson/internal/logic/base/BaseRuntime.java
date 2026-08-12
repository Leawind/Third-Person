package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.core.base.BaseParameters;
import io.github.leawind.thirdperson.internal.core.base.BaseSession;
import io.github.leawind.thirdperson.internal.core.base.ThirdPersonBase;
import io.github.leawind.thirdperson.internal.core.base.camera.CameraCollisionPort;
import io.github.leawind.thirdperson.internal.core.base.camera.CameraController;
import io.github.leawind.thirdperson.internal.core.base.camera.CameraFrameInput;
import io.github.leawind.thirdperson.internal.core.base.camera.CameraPose;
import io.github.leawind.thirdperson.internal.core.base.pivot.CameraPivotSmoothing;
import io.github.leawind.thirdperson.internal.core.base.rotation.LookRotation;
import io.github.leawind.thirdperson.internal.logic.base.pivot.MinecraftCameraPivotIntegration;
import java.util.Objects;
import java.util.Optional;

/// Process-wide owner of the mode-independent camera and player-control implementation.
public final class BaseRuntime implements ThirdPersonBase {
  private static final BaseRuntime INSTANCE = new BaseRuntime();

  private final BaseSession session = new BaseSession();
  private final CameraController cameraController = new CameraController(session.cameraSmoother());
  private BaseParameters parameters = BaseParameters.defaults();
  private boolean initialized;

  private BaseRuntime() {}

  public static BaseRuntime getInstance() {
    return INSTANCE;
  }

  public BaseSession session() {
    return session;
  }

  @Override
  public void applyParameters(BaseParameters parameters) {
    this.parameters = Objects.requireNonNull(parameters, "parameters");
  }

  @Override
  public void commitInteractionRotation(LookRotation rotation) {
    MinecraftClientIntegration.commitInteractionRotation(
        this, Objects.requireNonNull(rotation, "rotation"));
  }

  BaseParameters parameters() {
    return parameters;
  }

  public CameraPivotSmoothing cameraPivotSmoothing() {
    return parameters.cameraPivotSmoothing();
  }

  @Override
  public boolean isCameraControlEnabled() {
    return session.isControllingCamera();
  }

  @Override
  public boolean isControllingLocalPlayer() {
    return session.isControllingCamera() && PerspectiveGuard.isThirdPersonCurrentForLocalPlayer();
  }

  @Override
  public boolean hasDirectionalMovementIntent(double minimumMagnitude) {
    return session
        .movementIntent()
        .map(intent -> intent.hasDirectionalImpulse(minimumMagnitude))
        .orElse(false);
  }

  @Override
  public Optional<LookRotation> resolveInterestPointRotation() {
    return MinecraftPlayerRotationTargeting.interestPointRotation(this);
  }

  @Override
  public Optional<LookRotation> resolvePredictedCameraTargetRotation() {
    return MinecraftPlayerRotationTargeting.predictedCameraTargetRotation(this);
  }

  public Optional<CameraPose> updateCamera(CameraFrameInput frame, CameraCollisionPort collision) {
    Objects.requireNonNull(frame, "frame");
    Objects.requireNonNull(collision, "collision");
    return cameraController.update(
        frame, parameters.camera(), parameters.cameraSmoothing(), collision);
  }

  public boolean initialize() {
    if (initialized) {
      return false;
    }
    initialized = true;
    return true;
  }

  public void onPerspectiveActivated() {
    if (!session.isPerspectiveActive()) {
      session.activatePerspective();
    }
  }

  public void onPerspectiveDeactivated() {
    MinecraftCameraPivotIntegration.reset();
    session.reset();
  }

  public void resetCameraTracking() {
    MinecraftCameraPivotIntegration.reset();
    session.resetCameraTracking();
  }

  public void onClientIdentityChanged(boolean perspectiveCurrent) {
    MinecraftCameraPivotIntegration.reset();
    session.reset();
    if (perspectiveCurrent) {
      onPerspectiveActivated();
    }
  }
}
