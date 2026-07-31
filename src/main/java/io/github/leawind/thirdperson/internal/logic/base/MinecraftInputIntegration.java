package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerMovementInputEvent.MovementInput;
import io.github.leawind.thirdperson.internal.logic.base.rotation.MovementDirection;
import io.github.leawind.thirdperson.internal.logic.base.rotation.MovementIntent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/// Connects neutral input events to the Minecraft-independent session state.
public final class MinecraftInputIntegration {
  private MinecraftInputIntegration() {}

  public static boolean onTurn(LocalPlayer player, double rawYaw, double rawPitch) {
    var runtime = BaseRuntime.getInstance();
    if (!canControlCamera(player) || !runtime.isCameraControlEnabled()) {
      return false;
    }
    return runtime.session().lookController().turn(rawYaw, rawPitch);
  }

  public static MovementInput modifyMovementInput(
      LocalPlayer player, MovementInput vanillaInput) {
    var runtime = BaseRuntime.getInstance();
    if (!canControlCamera(player) || !runtime.isCameraControlEnabled()) {
      runtime.session().clearMovementIntent();
      return vanillaInput;
    }
    var lookController = runtime.session().lookController();
    float cameraYaw =
        lookController.isInitialized() ? lookController.yawDegrees() : player.getYRot();
    float cameraPitch =
        lookController.isInitialized() ? lookController.pitchDegrees() : player.getXRot();
    var intent =
        MovementIntent.tryCreate(
                vanillaInput.leftImpulse(),
                vanillaInput.forwardImpulse(),
                cameraYaw,
                cameraPitch)
            .orElse(null);
    if (intent == null) {
      runtime.session().clearMovementIntent();
      return vanillaInput;
    }
    runtime.session().recordMovementIntent(intent);
    return intent
        .relativeToPlayerYaw(player.getYRot())
        .map(input -> new MovementInput(input.leftImpulse(), input.forwardImpulse()))
        .orElse(vanillaInput);
  }

  public static boolean modifySprintImpulseCondition(
      LocalPlayer player,
      boolean vanillaResult,
      double leftImpulse,
      double forwardImpulse,
      double minimumMagnitude) {
    var runtime = BaseRuntime.getInstance();
    if (!canModifyPlayerMovement(player) || !runtime.isCameraControlEnabled()) {
      return vanillaResult;
    }
    return MovementDirection.hasDirectionalImpulse(
        leftImpulse, forwardImpulse, minimumMagnitude);
  }

  private static boolean canControlCamera(LocalPlayer player) {
    return player == Minecraft.getInstance().player
        && PerspectiveGuard.isThirdPersonCurrent();
  }

  private static boolean canModifyPlayerMovement(LocalPlayer player) {
    return canControlCamera(player) && !player.isPassenger();
  }
}
