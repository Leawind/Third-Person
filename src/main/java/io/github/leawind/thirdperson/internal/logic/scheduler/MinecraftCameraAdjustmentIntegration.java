package io.github.leawind.thirdperson.internal.logic.scheduler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/// Owns the configuration-oriented camera adjustment gesture in the scheduling layer.
public final class MinecraftCameraAdjustmentIntegration {
  private MinecraftCameraAdjustmentIntegration() {}

  public static boolean onTurn(LocalPlayer player, double rawYaw, double rawPitch) {
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

  public static boolean onScroll(double xOffset, double yOffset) {
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
    // Vanilla sends mouse turning through the local player even when a spectator is attached to a
    // different camera entity. Camera adjustment belongs to the active camera, not player control.
    return player == Minecraft.getInstance().player && runtime.base().isCameraControlEnabled();
  }

  private static void syncCamera(SchedulerRuntime runtime, LocalPlayer player) {
    runtime.applyParameters(
        player.isSwimming() || player.isFallFlying(),
        MinecraftSchedulingIntegration.isCameraRaycastOriginAllowed(Minecraft.getInstance()),
        runtime.appliedParameters().playerRotation());
  }
}
