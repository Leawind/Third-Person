package io.github.leawind.thirdperson.internal.integration.minecraft;

import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.bridge.events.ClientTickEvent;
import io.github.leawind.thirdperson.internal.core.movement.MovementDirection;
import io.github.leawind.thirdperson.internal.core.config.PlayerRotationMode;
import io.github.leawind.thirdperson.internal.core.camera.CameraMode;
import io.github.leawind.thirdperson.internal.integration.perspective.PerspectiveGuard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;

/// Owns Minecraft object identity and translates client ticks into pure session updates.
public final class MinecraftClientIntegration {
  private static boolean registered;
  private static ClientLevel previousLevel;
  private static LocalPlayer previousPlayer;

  private MinecraftClientIntegration() {}

  public static void register() {
    if (registered) {
      return;
    }
    registered = true;
    ClientTickEvent.register(MinecraftClientIntegration::onClientTick);
  }

  private static void onClientTick() {
    Minecraft minecraft = Minecraft.getInstance();
    LocalPlayer player = minecraft.player;
    ClientLevel level = minecraft.level;
    var runtime = ThirdPersonRuntime.getInstance();
    boolean currentPerspective = PerspectiveGuard.isThirdPersonCurrent();

    if (player != previousPlayer || level != previousLevel) {
      previousPlayer = player;
      previousLevel = level;
      runtime.onClientIdentityChanged(currentPerspective && player != null && level != null);
    } else if (currentPerspective && player != null && !runtime.session().isPerspectiveActive()) {
      // Defensive recovery if lifecycle callbacks ran before the local player became available.
      runtime.onPerspectiveActivated();
    }

    if (!PerspectiveGuard.isThirdPersonCurrentForLocalPlayer()
        || player == null
        || !runtime.isCameraControlEnabled()
        || runtime.config().player().rotationMode() == PlayerRotationMode.VANILLA
        || player.isPassenger()
        || player.isSwimming()) {
      return;
    }

    var lookController = runtime.session().lookController();
    if (!lookController.isInitialized()) {
      return;
    }
    if (player.isFallFlying() || runtime.session().mode() == CameraMode.AIMING) {
      player.setYRot(lookController.yawDegrees());
      player.setXRot(lookController.pitchDegrees());
      return;
    }
    MovementDirection.facingYawDegrees(
            player.xxa, player.zza, lookController.yawDegrees())
        .ifPresent(yaw -> player.setYRot((float) yaw));
  }
}
