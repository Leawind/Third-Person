package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.logic.base.rotation.LookRotation;
import io.github.leawind.thirdperson.internal.logic.base.rotation.PlayerRotationParameters;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;

/// Owns client identity and executes the current mode-independent base parameters.
public final class MinecraftClientIntegration {
  private static final double CLIENT_TICK_SECONDS = 0.05;
  private static final float HORIZONTAL_ROTATION_PITCH = 0.1f;

  private static ClientLevel previousLevel;
  private static LocalPlayer previousPlayer;
  private static long previousRenderNanos;

  private MinecraftClientIntegration() {}

  public static void onClientTick() {
    Minecraft minecraft = Minecraft.getInstance();
    LocalPlayer player = minecraft.player;
    ClientLevel level = minecraft.level;
    BaseRuntime runtime = BaseRuntime.getInstance();
    boolean currentPerspective = PerspectiveGuard.isThirdPersonCurrent();

    if (player != previousPlayer || level != previousLevel) {
      previousPlayer = player;
      previousLevel = level;
      runtime.onClientIdentityChanged(currentPerspective && player != null && level != null);
    } else if (currentPerspective && player != null && !runtime.session().isPerspectiveActive()) {
      runtime.onPerspectiveActivated();
    }

    if (!PerspectiveGuard.isThirdPersonCurrentForLocalPlayer()
        || player == null
        || !runtime.isCameraControlEnabled()) {
      runtime.session().playerRotationController().reset();
      return;
    }

    var lookController = runtime.session().lookController();
    if (!lookController.isInitialized()) {
      return;
    }
    PlayerRotationParameters parameters = runtime.parameters().playerRotation();
    LookRotation current = new LookRotation(player.getYRot(), player.getXRot());
    LookRotation target = resolveTarget(runtime, player, parameters).orElse(current);
    runtime
        .session()
        .playerRotationController()
        .update(current, target, CLIENT_TICK_SECONDS, parameters);
  }

  public static void beforeRenderFrame(float partialTick) {
    Minecraft minecraft = Minecraft.getInstance();
    LocalPlayer player = minecraft.player;
    BaseRuntime runtime = BaseRuntime.getInstance();
    if (!PerspectiveGuard.isThirdPersonCurrentForLocalPlayer()
        || player == null
        || !runtime.isCameraControlEnabled()) {
      previousRenderNanos = 0L;
      return;
    }

    var controller = runtime.session().playerRotationController();
    PlayerRotationParameters parameters = controller.parameters().orElse(null);
    if (parameters == null) {
      return;
    }
    long renderNanos = System.nanoTime();
    double frameDeltaSeconds =
        previousRenderNanos == 0L ? 0.0 : (renderNanos - previousRenderNanos) * 1.0e-9;
    previousRenderNanos = renderNanos;
    Optional<LookRotation> rotation =
        switch (parameters.smoothing()) {
          case IMMEDIATE -> resolveTarget(runtime, player, parameters);
          case TICK_INTERPOLATED -> controller.sample(partialTick);
          case FRAME_EXPONENTIAL ->
              resolveTarget(runtime, player, parameters)
                  .map(target -> controller.updateFrame(target, frameDeltaSeconds));
        };
    rotation.ifPresent(value -> setPlayerRotation(player, value));
  }

  private static Optional<LookRotation> resolveTarget(
      BaseRuntime runtime,
      LocalPlayer player,
      PlayerRotationParameters parameters) {
    var lookController = runtime.session().lookController();
    return switch (parameters.mode()) {
      case CUSTOM -> parameters.customRotation();
      case PARALLEL_WITH_CAMERA ->
          Optional.of(
              new LookRotation(lookController.yawDegrees(), lookController.pitchDegrees()));
      case LOOK_AT_CAMERA_RAY_HIT ->
          MinecraftPlayerRotationTargeting.cameraRayHitRotation(runtime);
      case MOVEMENT_DIRECTION ->
          runtime
              .session()
              .movementIntent()
              .flatMap(
                  intent ->
                      parameters.threeDimensionalMovement()
                          ? intent.facingRotation()
                          : intent
                              .facingYawDegrees()
                              .stream()
                              .mapToObj(
                                  yaw ->
                                      new LookRotation(
                                          (float) yaw, HORIZONTAL_ROTATION_PITCH))
                              .findFirst());
    };
  }

  private static void setPlayerRotation(LocalPlayer player, LookRotation rotation) {
    player.yRotO = rotation.yawDegrees();
    player.xRotO = rotation.pitchDegrees();
    player.setYRot(rotation.yawDegrees());
    player.setXRot(rotation.pitchDegrees());
  }
}
