package io.github.leawind.thirdperson.internal.integration.minecraft;

import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.core.aiming.AimGeometry;
import io.github.leawind.thirdperson.internal.core.math.FiniteMath;
import io.github.leawind.thirdperson.internal.integration.perspective.PerspectiveGuard;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

/// Aligns the authoritative player ray with the rendered camera intent before vanilla repicks.
public final class MinecraftInteractionIntegration {
  private MinecraftInteractionIntegration() {}

  public static boolean alignPlayerToCameraIntent() {
    ThirdPersonRuntime runtime = ThirdPersonRuntime.getInstance();
    Minecraft minecraft = Minecraft.getInstance();
    var player = minecraft.player;
    if (!PerspectiveGuard.isThirdPersonCurrentForLocalPlayer()
        || !runtime.isCameraControlEnabled()
        || player == null
        || minecraft.level == null
        || player.isPassenger()) {
      return false;
    }

    var cameraPose = runtime.session().finalCameraPose().orElse(null);
    if (cameraPose == null) {
      return false;
    }
    Vector3d cameraPosition = cameraPose.copyPosition(new Vector3d());
    Quaternionf cameraRotation = cameraPose.copyRotation(new Quaternionf());
    Vector3f cameraForward = cameraRotation.transform(new Vector3f(0.0f, 0.0f, 1.0f));
    double reach = interactionRange(minecraft);
    if (!FiniteMath.isFinite(cameraForward) || !Double.isFinite(reach) || reach <= 0.0) {
      return false;
    }

    Vector3d cameraRayEnd =
        new Vector3d(cameraPosition)
            .fma(reach, new Vector3d(cameraForward.x, cameraForward.y, cameraForward.z));
    Vec3 from = new Vec3(cameraPosition.x, cameraPosition.y, cameraPosition.z);
    Vec3 to = new Vec3(cameraRayEnd.x, cameraRayEnd.y, cameraRayEnd.z);
    HitResult blockIntent =
        minecraft.level.clip(
            new ClipContext(from, to, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    Vector3d intentPoint = cameraRayEnd;
    if (blockIntent.getType() != HitResult.Type.MISS) {
      Vec3 location = blockIntent.getLocation();
      intentPoint = new Vector3d(location.x, location.y, location.z);
    }

    Vec3 eye = player.getEyePosition(1.0f);
    return AimGeometry.lookAt(new Vector3d(eye.x, eye.y, eye.z), intentPoint)
        .map(
            rotation -> {
              player.setYRot(rotation.yawDegrees());
              player.setXRot(rotation.pitchDegrees());
              return true;
            })
        .orElse(false);
  }

  private static double interactionRange(Minecraft minecraft) {
    /*? if >=1.20.5 {*/
    return Math.max(
        minecraft.player.blockInteractionRange(), minecraft.player.entityInteractionRange());
    /*? } else {*/
    /*return minecraft.gameMode.getPickRange();
    *//*? }*/
  }
}
