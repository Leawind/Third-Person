package io.github.leawind.thirdperson.internal.base.integration.minecraft;

import io.github.leawind.thirdperson.internal.base.api.LookRotation;
import io.github.leawind.thirdperson.internal.base.api.RaycastOrigin;
import io.github.leawind.thirdperson.internal.base.application.BaseRuntime;
import io.github.leawind.thirdperson.internal.bridge.events.BeforeInteractionEvent;
import io.github.leawind.thirdperson.internal.base.core.rotation.LookGeometry;
import io.github.leawind.thirdperson.internal.base.core.math.FiniteMath;
import io.github.leawind.thirdperson.internal.base.integration.perspective.PerspectiveGuard;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

/// Aligns the authoritative player ray with the rendered camera intent before vanilla repicks.
public final class MinecraftInteractionIntegration {
  private static boolean registered;

  private MinecraftInteractionIntegration() {}

  public static void register() {
    if (registered) {
      return;
    }
    registered = true;
    BeforeInteractionEvent.register(MinecraftInteractionIntegration::alignPlayerToCameraIntent);
  }

  public static boolean alignPlayerToCameraIntent() {
    BaseRuntime runtime = BaseRuntime.getInstance();
    Minecraft minecraft = Minecraft.getInstance();
    var player = minecraft.player;
    if (!PerspectiveGuard.isThirdPersonCurrentForLocalPlayer()
        || !runtime.isCameraControlEnabled()
        || player == null
        || minecraft.level == null) {
      return false;
    }

    // A player-eye probe is only authoritative if vanilla picks after the player has been aligned
    // with the camera. This base-layer safety rule deliberately overrides every scheduled mode,
    // including CUSTOM.
    if (runtime.parameters().raycastOrigin() == RaycastOrigin.PLAYER_EYE) {
      var look = runtime.session().lookController();
      if (!look.isInitialized()) {
        return false;
      }
      setPlayerRotation(player, new LookRotation(look.yawDegrees(), look.pitchDegrees()));
      return true;
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
    return LookGeometry.lookAt(new Vector3d(eye.x, eye.y, eye.z), intentPoint)
        .map(
            rotation -> {
              setPlayerRotation(player, rotation);
              return true;
            })
        .orElse(false);
  }

  private static void setPlayerRotation(
      net.minecraft.client.player.LocalPlayer player, LookRotation rotation) {
    player.setYRot(rotation.yawDegrees());
    player.setXRot(rotation.pitchDegrees());
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
