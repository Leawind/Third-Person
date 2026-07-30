package io.github.leawind.thirdperson.internal.scheduler.integration.minecraft;

import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerTurnEvent;
import io.github.leawind.thirdperson.internal.bridge.events.MouseScrollEvent;
import io.github.leawind.thirdperson.internal.scheduler.SchedulerRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/// Owns the configuration-oriented camera adjustment gesture in the scheduling layer.
public final class MinecraftCameraAdjustmentIntegration {
  private static boolean registered;

  private MinecraftCameraAdjustmentIntegration() {}

  public static void register() {
    if (registered) {
      return;
    }
    registered = true;
    LocalPlayerTurnEvent.register(MinecraftCameraAdjustmentIntegration::onTurn);
    MouseScrollEvent.register(MinecraftCameraAdjustmentIntegration::onScroll);
  }

  private static boolean onTurn(LocalPlayer player, double rawYaw, double rawPitch) {
    SchedulerRuntime runtime = SchedulerRuntime.getInstance();
    if (!canControl(player, runtime)) {
      return false;
    }
    var adjustment = runtime.session().cameraAdjustmentController();
    if (!adjustment.isAdjusting()) {
      return false;
    }
    adjustment
        .turn(rawYaw, rawPitch)
        .ifPresent(
            profile ->
                runtime
                    .session()
                    .cameraAdjustmentSlot()
                    .ifPresent(slot -> runtime.updateCameraProfile(slot, profile)));
    syncCamera(runtime, player);
    return Double.isFinite(rawYaw) && Double.isFinite(rawPitch);
  }

  private static boolean onScroll(double xOffset, double yOffset) {
    SchedulerRuntime runtime = SchedulerRuntime.getInstance();
    LocalPlayer player = Minecraft.getInstance().player;
    if (player == null || !canControl(player, runtime)) {
      return false;
    }
    var adjustment = runtime.session().cameraAdjustmentController();
    if (!adjustment.isAdjusting()) {
      return false;
    }
    adjustment
        .scroll(yOffset)
        .ifPresent(
            profile ->
                runtime
                    .session()
                    .cameraAdjustmentSlot()
                    .ifPresent(slot -> runtime.updateCameraProfile(slot, profile)));
    syncCamera(runtime, player);
    return Double.isFinite(xOffset) && Double.isFinite(yOffset) && yOffset != 0.0;
  }

  private static boolean canControl(LocalPlayer player, SchedulerRuntime runtime) {
    return player == Minecraft.getInstance().player
        && !player.isPassenger()
        && runtime.base().isControllingLocalPlayer();
  }

  private static void syncCamera(SchedulerRuntime runtime, LocalPlayer player) {
    runtime.applyParameters(
        player.isSwimming() || player.isFallFlying(),
        runtime.base().parameters().playerRotation());
  }
}
