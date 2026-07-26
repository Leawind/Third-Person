package io.github.leawind.thirdperson.internal.integration.perspective;

import io.github.leawind.perspectiveapi.api.PerspectiveAPI;
import io.github.leawind.thirdperson.ThirdPerson;

/// Central guard for hooks that may only affect the Third Person perspective.
public final class PerspectiveGuard {
  private PerspectiveGuard() {}

  public static boolean isThirdPersonCurrent() {
    return PerspectiveAPI.isEnabled()
        && PerspectiveAPI.isCurrent(ThirdPerson.PERSPECTIVE_ID);
  }
}
