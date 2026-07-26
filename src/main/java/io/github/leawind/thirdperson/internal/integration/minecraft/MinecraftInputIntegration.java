package io.github.leawind.thirdperson.internal.integration.minecraft;

import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerTurnEvent;
import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerMovementYawEvent;
import io.github.leawind.thirdperson.internal.integration.perspective.PerspectiveGuard;

/// Connects neutral input events to the Minecraft-independent session state.
public final class MinecraftInputIntegration {
  private static boolean registered;

  private MinecraftInputIntegration() {}

  public static void register() {
    if (registered) {
      return;
    }
    registered = true;
    LocalPlayerTurnEvent.register(MinecraftInputIntegration::onTurn);
    LocalPlayerMovementYawEvent.register(MinecraftInputIntegration::modifyMovementYaw);
  }

  private static boolean onTurn(double rawYaw, double rawPitch) {
    if (!PerspectiveGuard.isThirdPersonCurrent()) {
      return false;
    }
    return ThirdPersonRuntime.getInstance()
        .session()
        .lookController()
        .turn(rawYaw, rawPitch);
  }

  private static float modifyMovementYaw(float vanillaYaw) {
    if (!PerspectiveGuard.isThirdPersonCurrent()) {
      return vanillaYaw;
    }
    var lookController = ThirdPersonRuntime.getInstance().session().lookController();
    return lookController.isInitialized() ? lookController.yawDegrees() : vanillaYaw;
  }
}
