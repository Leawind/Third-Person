package io.github.leawind.thirdperson.internal.integration.minecraft;

import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.bridge.events.ClientTickEvent;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import io.github.leawind.thirdperson.internal.integration.config.MinecraftConfigIntegration;
import io.github.leawind.thirdperson.internal.integration.perspective.PerspectiveGuard;
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
        PerspectiveGuard.isThirdPersonCurrent()
            && runtime.isCameraControlEnabled()
            && minecraft.player != null
            && minecraft.level != null
            /*? if >=26.2 {*/
            && minecraft.gui.screen() == null
            /*? } else {*/
            /*&& minecraft.screen == null
            *//*? }*/
            && minecraft.isWindowActive();

    var adjustment = runtime.session().cameraAdjustmentController();
    if (acceptsInput && ThirdPersonKeyMappings.ADJUST_CAMERA.isDown()) {
      adjustment.begin(runtime.config().camera().normal());
    } else if (adjustment.isAdjusting()) {
      adjustment.finish();
      MinecraftConfigIntegration.flushScheduledSave();
    }

    while (ThirdPersonKeyMappings.SWITCH_SHOULDER.consumeClick()) {
      if (!acceptsInput) {
        continue;
      }
      ThirdPersonConfig.CameraProfile profile = runtime.config().camera().normal();
      var mirrored =
          new ThirdPersonConfig.CameraProfile(
              profile.distance(), -profile.offsetX(), profile.offsetY(), profile.fovMultiplier());
      ThirdPersonConfig updated = runtime.updateNormalCameraProfile(mirrored);
      MinecraftConfigIntegration.scheduleSave(updated);
    }
  }
}
