package io.github.leawind.thirdperson.internal.logic.scheduler;

import io.github.leawind.thirdperson.internal.core.schedule.SchedulerRuntime;
import io.github.leawind.thirdperson.internal.bridge.Bridge;
import io.github.leawind.thirdperson.internal.core.base.rotation.LookRotation;
import io.github.leawind.thirdperson.internal.core.base.rotation.PlayerRotationMode;
import io.github.leawind.thirdperson.internal.core.base.rotation.PlayerRotationParameters;
import io.github.leawind.thirdperson.internal.core.base.rotation.PlayerRotationSmoothing;
import io.github.leawind.thirdperson.internal.core.schedule.rotation.PlayerRotationDecision;
import io.github.leawind.thirdperson.internal.core.schedule.rotation.PlayerRotationState;
import io.github.leawind.thirdperson.internal.core.schedule.rotation.PlayerRotationStrategy;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;

/// Detects dynamic game state and schedules one complete base-layer parameter snapshot per tick.
public final class MinecraftSchedulingIntegration {
  private static boolean refreshPredictedTargetEachFrame;

  private MinecraftSchedulingIntegration() {}

  public static void onClientTick() {
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
    runtime.applyParameters(flyingOrSwimming, isCameraRaycastOriginAllowed(minecraft), rotation);
  }

  static boolean isCameraRaycastOriginAllowed(Minecraft minecraft) {
    GameType gameType = minecraft.gameMode.getPlayerMode();
    return gameType != GameType.SURVIVAL && gameType != GameType.ADVENTURE;
  }

  private static PlayerRotationParameters schedulePlayerRotation(
      Minecraft minecraft, SchedulerRuntime runtime, LocalPlayer player) {
    refreshPredictedTargetEachFrame = false;
    var settings = runtime.playerSettings();
    if (!runtime.base().isControllingLocalPlayer()) {
      return customCurrent(player, 0.0, PlayerRotationSmoothing.IMMEDIATE);
    }

    boolean interacting =
        settings.autoRotateInteracting()
            && (minecraft.options.keyUse.isDown()
                || minecraft.options.keyAttack.isDown()
                || minecraft.options.keyPickItem.isDown())
            && !(settings.doNotRotateWhenEating() && Bridge.isEating(player));
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
                runtime.base().hasDirectionalMovementIntent(1.0e-5)));

    return switch (decision.target()) {
      case CURRENT_ROTATION ->
          customCurrent(player, decision.halfLifeSeconds(), decision.smoothing());
      case INTEREST_POINT ->
          custom(
              runtime.base().resolveInterestPointRotation(),
              decision.halfLifeSeconds(),
              decision.smoothing());
      case CAMERA_ROTATION -> mode(PlayerRotationMode.PARALLEL_WITH_CAMERA, decision);
      case CAMERA_HIT_RESULT -> mode(PlayerRotationMode.LOOK_AT_CAMERA_RAY_HIT, decision);
      case PREDICTED_TARGET_ENTITY -> {
        refreshPredictedTargetEachFrame = true;
        yield custom(
            runtime.base().resolvePredictedCameraTargetRotation(),
            decision.halfLifeSeconds(),
            decision.smoothing());
      }
      case HORIZONTAL_IMPULSE_DIRECTION -> mode(PlayerRotationMode.MOVEMENT_DIRECTION, decision);
      case IMPULSE_DIRECTION ->
          mode(PlayerRotationMode.MOVEMENT_DIRECTION, decision).withThreeDimensionalMovement(true);
    };
  }

  public static void beforeRenderFrame() {
    if (!refreshPredictedTargetEachFrame) {
      return;
    }
    SchedulerRuntime runtime = SchedulerRuntime.getInstance();
    var base = runtime.base();
    var current = runtime.appliedParameters();
    var rotation = current.playerRotation();
    runtime.applyPlayerRotation(
        custom(
            base.resolvePredictedCameraTargetRotation(),
            rotation.halfLifeSeconds(),
            rotation.smoothing()));
  }

  public static void beforeInteraction(Optional<LookRotation> rotation) {
    Objects.requireNonNull(rotation, "rotation");
    if (rotation.isEmpty()) {
      return;
    }
    Minecraft minecraft = Minecraft.getInstance();
    LocalPlayer player = minecraft.player;
    SchedulerRuntime runtime = SchedulerRuntime.getInstance();
    var settings = runtime.playerSettings();
    if (player == null
        || !runtime.base().isControllingLocalPlayer()
        || !settings.autoRotateInteracting()
        || (settings.doNotRotateWhenEating() && Bridge.isEating(player))) {
      return;
    }

    LookRotation target = rotation.orElseThrow();
    runtime.applyPlayerRotation(
        custom(Optional.of(target), 0.0, PlayerRotationSmoothing.IMMEDIATE));
    runtime.base().commitInteractionRotation(target);
  }

  private static PlayerRotationParameters mode(
      PlayerRotationMode mode, PlayerRotationDecision decision) {
    return PlayerRotationParameters.of(mode, decision.halfLifeSeconds(), decision.smoothing());
  }

  private static PlayerRotationParameters customCurrent(
      LocalPlayer player, double halfLifeSeconds, PlayerRotationSmoothing smoothing) {
    return custom(
        Optional.of(new LookRotation(player.getYRot(), player.getXRot())),
        halfLifeSeconds,
        smoothing);
  }

  private static PlayerRotationParameters custom(
      Optional<LookRotation> rotation, double halfLifeSeconds, PlayerRotationSmoothing smoothing) {
    return PlayerRotationParameters.custom(rotation, halfLifeSeconds, smoothing);
  }
}
