package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.logic.base.BaseParameters;
import io.github.leawind.thirdperson.internal.logic.base.LookRotation;
import io.github.leawind.thirdperson.internal.logic.base.ThirdPersonBase;
import io.github.leawind.thirdperson.internal.logic.base.CameraController;
import io.github.leawind.thirdperson.internal.logic.base.CameraFrameInput;
import io.github.leawind.thirdperson.internal.logic.base.CameraCollisionPort;
import io.github.leawind.thirdperson.internal.logic.base.CameraPose;
import io.github.leawind.thirdperson.internal.logic.base.MinecraftPlayerRotationTargeting;
import io.github.leawind.thirdperson.internal.logic.base.PerspectiveGuard;
import java.util.Objects;
import java.util.Optional;

/// Process-wide owner of the mode-independent camera and player-control implementation.
public final class BaseRuntime implements ThirdPersonBase {
  private static final BaseRuntime INSTANCE = new BaseRuntime();

  private final BaseSession session = new BaseSession();
  private final CameraController cameraController = new CameraController(session);
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
  public BaseParameters parameters() {
    return parameters;
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
  public Optional<LookRotation> resolveInterestPointRotation() {
    return MinecraftPlayerRotationTargeting.interestPointRotation(this);
  }

  @Override
  public Optional<LookRotation> resolvePredictedCameraTargetRotation() {
    return MinecraftPlayerRotationTargeting.predictedCameraTargetRotation(this);
  }

  public Optional<CameraPose> updateCamera(
      CameraFrameInput frame, CameraCollisionPort collision) {
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
    session.reset();
  }

  public void onClientIdentityChanged(boolean perspectiveCurrent) {
    session.reset();
    if (perspectiveCurrent) {
      onPerspectiveActivated();
    }
  }
}
