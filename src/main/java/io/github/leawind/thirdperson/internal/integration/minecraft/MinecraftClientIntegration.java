package io.github.leawind.thirdperson.internal.integration.minecraft;

import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.bridge.events.ClientTickEvent;
import io.github.leawind.thirdperson.internal.core.aiming.AimGeometry;
import io.github.leawind.thirdperson.internal.core.aiming.LookRotation;
import io.github.leawind.thirdperson.internal.core.camera.CameraMode;
import io.github.leawind.thirdperson.internal.core.config.PlayerRotationMode;
import io.github.leawind.thirdperson.internal.core.movement.MovementDirection;
import io.github.leawind.thirdperson.internal.core.player.PlayerRotationDecision;
import io.github.leawind.thirdperson.internal.core.player.PlayerRotationGeometry;
import io.github.leawind.thirdperson.internal.core.player.PlayerRotationState;
import io.github.leawind.thirdperson.internal.core.player.PlayerRotationStrategy;
import io.github.leawind.thirdperson.internal.core.player.PlayerRotationTarget;
import io.github.leawind.thirdperson.internal.integration.perspective.PerspectiveGuard;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
/*? if >=1.20.5 {*/
import net.minecraft.core.component.DataComponents;
/*? }*/
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3d;

/// Owns Minecraft object identity and translates client ticks into pure session updates.
public final class MinecraftClientIntegration {
  private static final double CLIENT_TICK_SECONDS = 0.05;
  private static final float HORIZONTAL_ROTATION_PITCH = 0.1f;
  private static final float VANILLA_HEAD_ROTATION_LIMIT_DEGREES = 50.0f;

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
        || runtime.config().player().rotationMode() == PlayerRotationMode.VANILLA) {
      runtime.session().playerRotationController().reset();
      return;
    }

    var lookController = runtime.session().lookController();
    if (!lookController.isInitialized()) {
      return;
    }
    var playerSettings = runtime.config().player();
    boolean interacting =
        playerSettings.autoRotateInteracting()
            && (minecraft.options.keyUse.isDown()
                || minecraft.options.keyAttack.isDown()
                || minecraft.options.keyPickItem.isDown())
            && !(playerSettings.doNotRotateWhenEating() && isEating(player));
    PlayerRotationDecision decision =
        PlayerRotationStrategy.resolve(
            new PlayerRotationState(
                playerSettings.normalMode(),
                runtime.session().mode() == CameraMode.AIMING,
                player.isSwimming(),
                minecraft.options.keySprint.isDown() || player.isSprinting(),
                player.isFallFlying(),
                interacting,
                player.isPassenger(),
                player.getVehicle() instanceof LivingEntity,
                MovementDirection.hasDirectionalImpulse(player.xxa, player.zza, 1.0e-5)));

    LookRotation current = new LookRotation(player.getYRot(), player.getXRot());
    LookRotation target =
        resolveTarget(minecraft, runtime, player, decision.target()).orElse(current);
    LookRotation rotation =
        runtime
            .session()
            .playerRotationController()
            .update(current, target, CLIENT_TICK_SECONDS, decision);
    setPlayerRotation(player, rotation);
  }

  private static Optional<LookRotation> resolveTarget(
      Minecraft minecraft,
      ThirdPersonRuntime runtime,
      LocalPlayer player,
      PlayerRotationTarget target) {
    var lookController = runtime.session().lookController();
    return switch (target) {
      case CURRENT_ROTATION ->
          Optional.of(new LookRotation(player.getYRot(), player.getXRot()));
      case CAMERA_ROTATION ->
          Optional.of(
              new LookRotation(lookController.yawDegrees(), lookController.pitchDegrees()));
      case HORIZONTAL_IMPULSE_DIRECTION ->
          MovementDirection.facingYawDegrees(
                  player.xxa, player.zza, lookController.yawDegrees())
              .stream()
              .mapToObj(yaw -> new LookRotation((float) yaw, HORIZONTAL_ROTATION_PITCH))
              .findFirst();
      case IMPULSE_DIRECTION ->
          MovementDirection.facingRotation(
              player.xxa,
              player.zza,
              lookController.yawDegrees(),
              lookController.pitchDegrees());
      case CAMERA_HIT_RESULT -> cameraHitRotation(minecraft, runtime, player, false);
      case PREDICTED_TARGET_ENTITY -> cameraHitRotation(minecraft, runtime, player, true);
      case INTEREST_POINT -> interestPointRotation(minecraft, runtime, player);
    };
  }

  private static Optional<LookRotation> cameraHitRotation(
      Minecraft minecraft,
      ThirdPersonRuntime runtime,
      LocalPlayer player,
      boolean predictTargetEntity) {
    return MinecraftPlayerRotationTargeting.cameraHit(minecraft, runtime, predictTargetEntity)
        .flatMap(
            hit -> {
              Optional<Vector3d> predicted =
                  predictTargetEntity
                      ? MinecraftPlayerRotationTargeting.predictedTargetPoint(
                          minecraft, runtime, hit)
                      : Optional.empty();
              if (predicted.isPresent()) {
                return lookAtPlayerEye(player, predicted.orElseThrow());
              }
              if (hit.missed()) {
                var lookController = runtime.session().lookController();
                return Optional.of(
                    new LookRotation(
                        lookController.yawDegrees(), lookController.pitchDegrees()));
              }
              return lookAtPlayerEye(player, hit.location());
            });
  }

  private static Optional<LookRotation> interestPointRotation(
      Minecraft minecraft, ThirdPersonRuntime runtime, LocalPlayer player) {
    var cameraView = MinecraftPlayerRotationTargeting.cameraView(runtime).orElse(null);
    if (cameraView == null) {
      return Optional.empty();
    }
    float cameraYaw = runtime.session().lookController().yawDegrees();
    boolean cameraBehindPlayer =
        Math.abs(PlayerRotationGeometry.shortestDifference(cameraYaw, player.yBodyRot)) < 90.0f;
    Optional<Vector3d> point =
        cameraBehindPlayer
            ? MinecraftPlayerRotationTargeting.cameraHit(minecraft, runtime, false)
                .map(MinecraftPlayerRotationTargeting.CameraHit::location)
            : Optional.of(cameraView.position());
    return point
        .flatMap(target -> lookAtPlayerEye(player, target))
        .map(
            rotation ->
                new LookRotation(
                    PlayerRotationGeometry.clampYawAround(
                        rotation.yawDegrees(),
                        player.yBodyRot,
                        VANILLA_HEAD_ROTATION_LIMIT_DEGREES),
                    rotation.pitchDegrees()));
  }

  private static Optional<LookRotation> lookAtPlayerEye(
      LocalPlayer player, Vector3d point) {
    var eye = player.getEyePosition(1.0f);
    return AimGeometry.lookAt(new Vector3d(eye.x, eye.y, eye.z), point);
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

  private static void setPlayerRotation(LocalPlayer player, LookRotation rotation) {
    player.yRotO = rotation.yawDegrees();
    player.xRotO = rotation.pitchDegrees();
    player.setYRot(rotation.yawDegrees());
    player.setXRot(rotation.pitchDegrees());
  }
}
