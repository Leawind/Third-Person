package io.github.leawind.thirdperson.internal.integration.minecraft;

import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerTurnEvent;
import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerMovementYawEvent;
import io.github.leawind.thirdperson.internal.bridge.events.MouseScrollEvent;
import io.github.leawind.thirdperson.internal.integration.config.MinecraftConfigIntegration;
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
    MouseScrollEvent.register(MinecraftInputIntegration::onScroll);
  }

  private static boolean onTurn(double rawYaw, double rawPitch) {
    var runtime = ThirdPersonRuntime.getInstance();
    if (!PerspectiveGuard.isThirdPersonCurrent() || !runtime.isCameraControlEnabled()) {
      return false;
    }
    var adjustment = runtime.session().cameraAdjustmentController();
    if (adjustment.isAdjusting()) {
      adjustment
          .turn(rawYaw, rawPitch)
          .flatMap(
              profile ->
                  runtime
                      .session()
                      .cameraAdjustmentSlot()
                      .map(slot -> runtime.updateCameraProfile(slot, profile)))
          .ifPresent(MinecraftConfigIntegration::scheduleSave);
      return Double.isFinite(rawYaw) && Double.isFinite(rawPitch);
    }
    return runtime.session().lookController().turn(rawYaw, rawPitch);
  }

  private static float modifyMovementYaw(float vanillaYaw) {
    var runtime = ThirdPersonRuntime.getInstance();
    if (!PerspectiveGuard.isThirdPersonCurrent() || !runtime.isCameraControlEnabled()) {
      return vanillaYaw;
    }
    var lookController = runtime.session().lookController();
    return lookController.isInitialized() ? lookController.yawDegrees() : vanillaYaw;
  }

  private static boolean onScroll(double xOffset, double yOffset) {
    var runtime = ThirdPersonRuntime.getInstance();
    if (!PerspectiveGuard.isThirdPersonCurrent() || !runtime.isCameraControlEnabled()) {
      return false;
    }
    var adjustment = runtime.session().cameraAdjustmentController();
    if (!adjustment.isAdjusting()) {
      return false;
    }
    adjustment
        .scroll(yOffset)
        .flatMap(
            profile ->
                runtime
                    .session()
                    .cameraAdjustmentSlot()
                    .map(slot -> runtime.updateCameraProfile(slot, profile)))
        .ifPresent(MinecraftConfigIntegration::scheduleSave);
    return Double.isFinite(xOffset) && Double.isFinite(yOffset) && yOffset != 0.0;
  }
}
