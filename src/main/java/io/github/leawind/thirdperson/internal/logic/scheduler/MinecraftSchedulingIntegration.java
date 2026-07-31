package io.github.leawind.thirdperson.internal.logic.scheduler;

import io.github.leawind.thirdperson.internal.logic.base.LookRotation;
import io.github.leawind.thirdperson.internal.logic.base.PlayerRotationMode;
import io.github.leawind.thirdperson.internal.logic.base.PlayerRotationParameters;
import io.github.leawind.thirdperson.internal.logic.base.PlayerRotationSmoothing;
import io.github.leawind.thirdperson.internal.bridge.events.ClientTickEvent;
import io.github.leawind.thirdperson.internal.bridge.events.RenderFrameEvent;
import io.github.leawind.thirdperson.internal.logic.scheduler.SchedulerRuntime;
import io.github.leawind.thirdperson.internal.logic.scheduler.PlayerRotationDecision;
import io.github.leawind.thirdperson.internal.logic.scheduler.PlayerRotationState;
import io.github.leawind.thirdperson.internal.logic.scheduler.PlayerRotationStrategy;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
/*? if >=1.20.5 {*/
import net.minecraft.core.component.DataComponents;
/*? }*/
import net.minecraft.world.entity.LivingEntity;

/// Detects dynamic game state and schedules one complete base-layer parameter snapshot per tick.
public final class MinecraftSchedulingIntegration {
  private static boolean registered;
  private static boolean refreshPredictedTargetEachFrame;

  private MinecraftSchedulingIntegration() {}

  public static void register() {
    if (registered) {
      return;
    }
    registered = true;
    ClientTickEvent.register(MinecraftSchedulingIntegration::onClientTick);
    RenderFrameEvent.register(MinecraftSchedulingIntegration::beforeRenderFrame);
  }

  private static void onClientTick() {
    Minecraft minecraft = Minecraft.getInstance();
    LocalPlayer player = minecraft.player;
    SchedulerRuntime runtime = SchedulerRuntime.getInstance();
    if (player == null || minecraft.level == null) {
      refreshPredictedTargetEachFrame = false;
      runtime.session().reset();
      return;
    }

    boolean flyingOrSwimming = player.isFallFlying() || player.isSwimming();
    PlayerRotationParameters rotation = schedulePlayerRotation(minecraft, runtime, player);
    runtime.applyParameters(flyingOrSwimming, rotation);
  }

  private static PlayerRotationParameters schedulePlayerRotation(
      Minecraft minecraft, SchedulerRuntime runtime, LocalPlayer player) {
    refreshPredictedTargetEachFrame = false;
    var settings = runtime.playerSettings();
    if (!runtime.base().isControllingLocalPlayer()
        || settings.rotationMode()
            == io.github.leawind.thirdperson.internal.logic.scheduler.ConfiguredPlayerRotationMode
                .VANILLA) {
      return customCurrent(player, 0.0, PlayerRotationSmoothing.IMMEDIATE);
    }

    boolean interacting =
        settings.autoRotateInteracting()
            && (minecraft.options.keyUse.isDown()
                || minecraft.options.keyAttack.isDown()
                || minecraft.options.keyPickItem.isDown())
            && !(settings.doNotRotateWhenEating() && isEating(player));
    PlayerRotationDecision decision =
        PlayerRotationStrategy.resolve(
            new PlayerRotationState(
                settings.normalMode(),
                runtime.isAiming(),
                player.isSwimming(),
                minecraft.options.keySprint.isDown() || player.isSprinting(),
                player.isFallFlying(),
                interacting,
                player.isPassenger(),
                player.getVehicle() instanceof LivingEntity,
                hasDirectionalImpulse(player.xxa, player.zza, 1.0e-5)));

    return switch (decision.target()) {
      case CURRENT_ROTATION ->
          customCurrent(player, decision.halfLifeSeconds(), decision.smoothing());
      case INTEREST_POINT ->
          custom(
              runtime.base().resolveInterestPointRotation(),
              decision.halfLifeSeconds(),
              decision.smoothing());
      case CAMERA_ROTATION ->
          mode(PlayerRotationMode.PARALLEL_WITH_CAMERA, decision);
      case CAMERA_HIT_RESULT ->
          mode(PlayerRotationMode.LOOK_AT_CAMERA_RAY_HIT, decision);
      case PREDICTED_TARGET_ENTITY -> {
        refreshPredictedTargetEachFrame = true;
        yield custom(
            runtime.base().resolvePredictedCameraTargetRotation(),
            decision.halfLifeSeconds(),
            decision.smoothing());
      }
      case HORIZONTAL_IMPULSE_DIRECTION ->
          mode(PlayerRotationMode.MOVEMENT_DIRECTION, decision);
      case IMPULSE_DIRECTION ->
          mode(PlayerRotationMode.MOVEMENT_DIRECTION, decision)
              .withThreeDimensionalMovement(true);
    };
  }

  private static void beforeRenderFrame(float partialTick) {
    if (!refreshPredictedTargetEachFrame) {
      return;
    }
    SchedulerRuntime runtime = SchedulerRuntime.getInstance();
    var base = runtime.base();
    var current = base.parameters();
    var rotation = current.playerRotation();
    base.applyParameters(
        current.withPlayerRotation(
            custom(
                base.resolvePredictedCameraTargetRotation(),
                rotation.halfLifeSeconds(),
                rotation.smoothing())));
  }

  private static PlayerRotationParameters mode(
      PlayerRotationMode mode, PlayerRotationDecision decision) {
    return PlayerRotationParameters.of(
        mode, decision.halfLifeSeconds(), decision.smoothing());
  }

  private static PlayerRotationParameters customCurrent(
      LocalPlayer player,
      double halfLifeSeconds,
      PlayerRotationSmoothing smoothing) {
    return custom(
        Optional.of(new LookRotation(player.getYRot(), player.getXRot())),
        halfLifeSeconds,
        smoothing);
  }

  private static PlayerRotationParameters custom(
      Optional<LookRotation> rotation,
      double halfLifeSeconds,
      PlayerRotationSmoothing smoothing) {
    return PlayerRotationParameters.custom(rotation, halfLifeSeconds, smoothing);
  }

  private static boolean isEating(LocalPlayer player) {
    if (!player.isUsingItem()) {
      return false;
    }
    /*? if >=1.20.5 {*/
    return player.getUseItem().get(DataComponents.FOOD) != null;
    /*? } else {*/
    /*return player.getUseItem().isEdible();
    *//*? }*/
  }

  private static boolean hasDirectionalImpulse(
      double leftImpulse, double forwardImpulse, double minimumMagnitude) {
    double lengthSquared = leftImpulse * leftImpulse + forwardImpulse * forwardImpulse;
    return Double.isFinite(lengthSquared)
        && lengthSquared >= minimumMagnitude * minimumMagnitude;
  }
}
