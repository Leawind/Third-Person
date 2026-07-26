package io.github.leawind.thirdperson.internal.application;

import io.github.leawind.thirdperson.ThirdPerson;

/// Process-wide owner of services and the current Minecraft-independent session state.
public final class ThirdPersonRuntime {
  private static final ThirdPersonRuntime INSTANCE = new ThirdPersonRuntime();

  private final ThirdPersonSession session = new ThirdPersonSession();
  private boolean initialized;

  private ThirdPersonRuntime() {}

  public static ThirdPersonRuntime getInstance() {
    return INSTANCE;
  }

  public ThirdPersonSession session() {
    return session;
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
  }

  public void onPerspectiveDeactivated() {
    session.reset();
  }

  public void onClientIdentityChanged(boolean perspectiveCurrent) {
    session.reset();
    if (perspectiveCurrent) {
      session.activatePerspective();
    }
  }
}
