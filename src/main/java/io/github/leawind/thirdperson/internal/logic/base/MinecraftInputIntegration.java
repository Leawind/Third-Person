package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerMovementInputEvent.MovementInput;
import io.github.leawind.thirdperson.internal.bridge.entity.MinecraftEntityReferencePose;
import io.github.leawind.thirdperson.internal.core.base.pivot.PivotPose;
import io.github.leawind.thirdperson.internal.bridge.input.MinecraftMovementInputMapping;
import io.github.leawind.thirdperson.internal.core.base.rotation.MovementDirection;
import io.github.leawind.thirdperson.internal.core.base.rotation.MovementIntent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.joml.Quaternionf;
import org.joml.Vector3d;

/// Connects neutral input events to the Minecraft-independent session state.
public final class MinecraftInputIntegration {
  private MinecraftInputIntegration() {}

  public static boolean onTurn(LocalPlayer player, double rawYaw, double rawPitch) {
    var runtime = BaseRuntime.getInstance();
    if (!canControlCamera(player) || !runtime.isCameraControlEnabled()) {
      return false;
    }
    return runtime.session().lookController().turn(rawYaw, rawPitch);
  }

  public static MovementInput modifyMovementInput(LocalPlayer player, MovementInput vanillaInput) {
    var runtime = BaseRuntime.getInstance();
    if (!canControlCamera(player) || !runtime.isCameraControlEnabled()) {
      runtime.session().clearMovementIntent();
      return vanillaInput;
    }
    var lookController = runtime.session().lookController();
    var pivotPose =
        runtime
            .session()
            .pivotPose()
            .orElseGet(() -> referencePivotPose(player));
    if (!lookController.isInitialized()) {
      var worldFromCamera =
          PerspectiveMath.eulerDegToQuat(
              player.getXRot(), player.getYRot(), 0.0f, new Quaternionf());
      lookController.initializeFromWorldRotation(
          worldFromCamera, pivotPose.copyWorldFromPivot(new Quaternionf()));
    }
    var pivotFromCamera = new Quaternionf();
    if (!lookController.copyRotation(pivotFromCamera)) {
      runtime.session().clearMovementIntent();
      return vanillaInput;
    }
    var intent =
        MovementIntent.tryCreate(
                vanillaInput.leftImpulse(),
                vanillaInput.forwardImpulse(),
                lookController.yawDegrees(),
                pivotFromCamera,
                pivotPose)
            .orElse(null);
    if (intent == null) {
      runtime.session().clearMovementIntent();
      return vanillaInput;
    }
    var mapping = MinecraftMovementInputMapping.map(player, intent, vanillaInput);
    if (!mapping.handled()) {
      runtime.session().clearMovementIntent();
      return mapping.input();
    }
    runtime.session().recordMovementIntent(intent);
    return mapping.input();
  }

  public static boolean modifySprintImpulseCondition(
      LocalPlayer player,
      boolean vanillaResult,
      double leftImpulse,
      double forwardImpulse,
      double minimumMagnitude) {
    var runtime = BaseRuntime.getInstance();
    if (!canModifyPlayerMovement(player) || !runtime.isCameraControlEnabled()) {
      return vanillaResult;
    }
    return MovementDirection.hasDirectionalImpulse(leftImpulse, forwardImpulse, minimumMagnitude);
  }

  private static boolean canControlCamera(LocalPlayer player) {
    return player == Minecraft.getInstance().player && PerspectiveGuard.isThirdPersonCurrent();
  }

  private static PivotPose referencePivotPose(LocalPlayer player) {
    var reference = MinecraftEntityReferencePose.resolve(player, 1.0f);
    return PivotPose.tryCreate(
            reference.copyEyePositionWorld(new Vector3d()),
            reference.copyWorldFromReference(new Quaternionf()))
        .orElseThrow();
  }

  private static boolean canModifyPlayerMovement(LocalPlayer player) {
    return canControlCamera(player) && !player.isPassenger();
  }
}
