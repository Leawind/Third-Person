package io.github.leawind.thirdperson.internal.integration.minecraft;

import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.application.camera.CameraSettings;
import io.github.leawind.thirdperson.internal.bridge.events.ClientTickEvent;
import io.github.leawind.thirdperson.internal.core.aiming.AimModeResolver;
import io.github.leawind.thirdperson.internal.core.camera.CameraMode;
import io.github.leawind.thirdperson.internal.core.camera.CameraProfile;
import io.github.leawind.thirdperson.internal.core.camera.CameraProfileSlot;
import io.github.leawind.thirdperson.internal.integration.perspective.PerspectiveGuard;
import io.github.leawind.thirdperson.internal.integration.resource.MinecraftItemPredicateIntegration;
import io.github.leawind.thirdperson.internal.persistence.MinecraftStatePersistence;
import java.util.function.Consumer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

/// Handles key edges after each client tick while platform entrypoints only register mappings.
public final class MinecraftKeyIntegration {
  private static final int CENTER_HOLD_TICKS = 4;

  private static boolean registered;
  private static boolean acceptsInputThisTick;
  private static KeyStateTracker shoulderKeyTracker;

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
    acceptsInputThisTick = acceptsInput;

    shoulderKeyTracker().tick().drain();

    runtime.setAiming(
        acceptsInput
            && AimModeResolver.shouldAim(
                ThirdPersonKeyMappings.AIM.isDown(),
                runtime.aimingSettings().smartAiming(),
                MinecraftItemPredicateIntegration.isAutomaticallyAiming()));

    var session = runtime.session();
    var adjustment = session.cameraAdjustmentController();
    if (acceptsInput && ThirdPersonKeyMappings.ADJUST_CAMERA.isDown()) {
      CameraProfileSlot slot =
          session.mode() == CameraMode.AIMING
              ? CameraProfileSlot.AIMING
              : CameraProfileSlot.NORMAL;
      CameraProfile profile = runtime.cameraSettings().profile(slot);
      session.beginCameraAdjustment(slot, profile);
    } else if (adjustment.isAdjusting()) {
      session.finishCameraAdjustment();
      MinecraftStatePersistence.flushScheduledSave();
    }
  }

  private static KeyStateTracker shoulderKeyTracker() {
    if (shoulderKeyTracker == null) {
      shoulderKeyTracker =
          KeyStateTracker.builder(ThirdPersonKeyMappings.SWITCH_SHOULDER)
              .setHoldTicks(CENTER_HOLD_TICKS)
              .onPress(() -> updateShoulder(false))
              .onHoldStart(() -> updateShoulder(true))
              .build();
    }
    return shoulderKeyTracker;
  }

  private static void updateShoulder(boolean centered) {
    if (!acceptsInputThisTick) {
      return;
    }
    ThirdPersonRuntime runtime = ThirdPersonRuntime.getInstance();
    var session = runtime.session();
    CameraProfileSlot slot =
        session.mode() == CameraMode.AIMING
            ? CameraProfileSlot.AIMING
            : CameraProfileSlot.NORMAL;
    CameraProfile profile = runtime.cameraSettings().profile(slot);
    CameraProfile updatedProfile;
    if (centered) {
      updatedProfile = profile.withCentered(true);
    } else if (profile.centered()) {
      updatedProfile = profile.withCentered(false);
    } else {
      updatedProfile = profile.withOffsetX(nextShoulderOffset(slot, profile.offsetX()));
    }
    if (updatedProfile.equals(profile)) {
      return;
    }
    runtime.updateCameraProfile(slot, updatedProfile);
  }

  private static double nextShoulderOffset(CameraProfileSlot slot, double currentOffset) {
    if (currentOffset != 0.0) {
      return -currentOffset;
    }
    return slot == CameraProfileSlot.AIMING
        ? CameraSettings.defaultAimingProfile().offsetX()
        : CameraSettings.defaultNormalProfile().offsetX();
  }
}
