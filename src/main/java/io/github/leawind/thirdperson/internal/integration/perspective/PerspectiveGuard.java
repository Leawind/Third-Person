package io.github.leawind.thirdperson.internal.integration.perspective;

import io.github.leawind.perspectiveapi.api.PerspectiveAPI;
import io.github.leawind.thirdperson.ThirdPerson;
import net.minecraft.client.Minecraft;

/// Central guard for hooks that may only affect the Third Person perspective.
public final class PerspectiveGuard {
  private PerspectiveGuard() {}

  public static boolean isThirdPersonCurrent() {
    return PerspectiveAPI.isEnabled()
        && PerspectiveAPI.isCurrent(ThirdPerson.PERSPECTIVE_ID);
  }

  public static boolean isThirdPersonCurrentForLocalPlayer() {
    if (!isThirdPersonCurrent()) {
      return false;
    }
    Minecraft minecraft = Minecraft.getInstance();
    return minecraft.player != null && minecraft.getCameraEntity() == minecraft.player;
  }
}
