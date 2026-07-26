package io.github.leawind.thirdperson.internal.application;

import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.core.camera.CameraMode;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import java.util.Objects;

/// Process-wide owner of services and the current Minecraft-independent session state.
public final class ThirdPersonRuntime {
  private static final ThirdPersonRuntime INSTANCE = new ThirdPersonRuntime();

  private final ThirdPersonSession session = new ThirdPersonSession();
  private volatile ThirdPersonConfig config = ThirdPersonConfig.defaults();
  private boolean initialized;

  private ThirdPersonRuntime() {}

  public static ThirdPersonRuntime getInstance() {
    return INSTANCE;
  }

  public ThirdPersonSession session() {
    return session;
  }

  public ThirdPersonConfig config() {
    return config;
  }

  public boolean isCameraControlEnabled() {
    return config.enabled() && session.isControllingCamera();
  }

  public void initialize() {
    if (initialized) {
      return;
    }
    initialized = true;
    ThirdPerson.LOGGER.info("{} initialized", ThirdPerson.MOD_NAME);
  }

  public void onPerspectiveActivated() {
    session.activatePerspective();
    if (!config.enabled()) {
      session.setMode(CameraMode.BYPASS);
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

  public void updateConfig(ThirdPersonConfig config) {
    this.config = Objects.requireNonNull(config, "config");
    if (!session.isPerspectiveActive()) {
      return;
    }
    session.setMode(config.enabled() ? CameraMode.NORMAL : CameraMode.BYPASS);
  }
}
