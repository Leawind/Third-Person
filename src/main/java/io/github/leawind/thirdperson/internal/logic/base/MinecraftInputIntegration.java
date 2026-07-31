package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.logic.base.rotation.MovementDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/// Connects neutral input events to the Minecraft-independent session state.
public final class MinecraftInputIntegration {
  private MinecraftInputIntegration() {}

  public static boolean onTurn(LocalPlayer player, double rawYaw, double rawPitch) {
    var runtime = BaseRuntime.getInstance();
    if (!canControl(player) || !runtime.isCameraControlEnabled()) {
      return false;
    }
    return runtime.session().lookController().turn(rawYaw, rawPitch);
  }

  public static float modifyMovementYaw(LocalPlayer player, float vanillaYaw) {
    var runtime = BaseRuntime.getInstance();
    if (!canControl(player) || !runtime.isCameraControlEnabled()) {
      return vanillaYaw;
    }
    var lookController = runtime.session().lookController();
    return lookController.isInitialized() ? lookController.yawDegrees() : vanillaYaw;
  }

  public static boolean modifySprintImpulseCondition(
      LocalPlayer player,
      boolean vanillaResult,
      double leftImpulse,
      double forwardImpulse,
      double minimumMagnitude) {
    var runtime = BaseRuntime.getInstance();
    if (!canControl(player) || !runtime.isCameraControlEnabled()) {
      return vanillaResult;
    }
    return MovementDirection.hasDirectionalImpulse(
        leftImpulse, forwardImpulse, minimumMagnitude);
  }

  private static boolean canControl(LocalPlayer player) {
    return player == Minecraft.getInstance().player
        && !player.isPassenger()
        && PerspectiveGuard.isThirdPersonCurrent();
  }
}
