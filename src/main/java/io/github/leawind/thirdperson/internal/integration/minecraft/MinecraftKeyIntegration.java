package io.github.leawind.thirdperson.internal.integration.minecraft;

import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.bridge.events.ClientTickEvent;
import io.github.leawind.thirdperson.internal.core.aiming.AimModeResolver;
import io.github.leawind.thirdperson.internal.core.aiming.AimUseAnimation;
import io.github.leawind.thirdperson.internal.core.camera.CameraMode;
import io.github.leawind.thirdperson.internal.core.config.CameraProfileSlot;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import io.github.leawind.thirdperson.internal.integration.config.MinecraftConfigIntegration;
import io.github.leawind.thirdperson.internal.integration.perspective.PerspectiveGuard;
import io.github.leawind.thirdperson.internal.integration.resource.MinecraftAimingRuleIntegration;
import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

/// Handles key edges after each client tick while platform entrypoints only register mappings.
public final class MinecraftKeyIntegration {
  private static boolean registered;

  private MinecraftKeyIntegration() {}

  public static void register() {
    if (registered) {
      return;
    }
    registered = true;
    ClientTickEvent.register(MinecraftKeyIntegration::onClientTick);
  }

  public static void registerKeyMappings(Consumer<KeyMapping> registrar) {
    ThirdPersonKeyMappings.all().forEach(registrar);
  }

  private static void onClientTick() {
    Minecraft minecraft = Minecraft.getInstance();
    ThirdPersonRuntime runtime = ThirdPersonRuntime.getInstance();
    boolean acceptsInput =
        PerspectiveGuard.isThirdPersonCurrentForLocalPlayer()
            && runtime.isCameraControlEnabled()
            && minecraft.player != null
            && minecraft.level != null
            /*? if >=26.2 {*/
            && minecraft.gui.screen() == null
            /*? } else {*/
            /*&& minecraft.screen == null
            *//*? }*/
            && minecraft.isWindowActive();

    boolean usingItem = minecraft.player != null && minecraft.player.isUsingItem();
    AimUseAnimation useAnimation =
        usingItem
            ? mapUseAnimation(minecraft.player.getUseItem().getUseAnimation().name())
            : AimUseAnimation.NONE;
    runtime.setAiming(
        acceptsInput
            && AimModeResolver.shouldAim(
                ThirdPersonKeyMappings.AIM.isDown(),
                runtime.config().aiming().smartAiming(),
                usingItem,
                useAnimation,
                MinecraftAimingRuleIntegration.currentAction()));

    var session = runtime.session();
    var adjustment = session.cameraAdjustmentController();
    if (acceptsInput && ThirdPersonKeyMappings.ADJUST_CAMERA.isDown()) {
      CameraProfileSlot slot =
          session.mode() == CameraMode.AIMING
              ? CameraProfileSlot.AIMING
              : CameraProfileSlot.NORMAL;
      ThirdPersonConfig.CameraProfile profile =
          slot == CameraProfileSlot.AIMING
              ? runtime.config().camera().aiming()
              : runtime.config().camera().normal();
      session.beginCameraAdjustment(slot, profile);
    } else if (adjustment.isAdjusting()) {
      session.finishCameraAdjustment();
      MinecraftConfigIntegration.flushScheduledSave();
    }

    while (ThirdPersonKeyMappings.SWITCH_SHOULDER.consumeClick()) {
      if (!acceptsInput) {
        continue;
      }
      CameraProfileSlot slot =
          session.mode() == CameraMode.AIMING
              ? CameraProfileSlot.AIMING
              : CameraProfileSlot.NORMAL;
      ThirdPersonConfig.CameraProfile profile =
          slot == CameraProfileSlot.AIMING
              ? runtime.config().camera().aiming()
              : runtime.config().camera().normal();
      var mirrored =
          new ThirdPersonConfig.CameraProfile(
              profile.distance(), -profile.offsetX(), profile.offsetY(), profile.fovMultiplier());
      ThirdPersonConfig updated = runtime.updateCameraProfile(slot, mirrored);
      MinecraftConfigIntegration.scheduleSave(updated);
    }
  }

  private static AimUseAnimation mapUseAnimation(String name) {
    return switch (name) {
      case "BOW" -> AimUseAnimation.BOW;
      case "CROSSBOW" -> AimUseAnimation.CROSSBOW;
      case "SPEAR", "TRIDENT" -> AimUseAnimation.SPEAR;
      case "NONE" -> AimUseAnimation.NONE;
      default -> AimUseAnimation.OTHER;
    };
  }
}
