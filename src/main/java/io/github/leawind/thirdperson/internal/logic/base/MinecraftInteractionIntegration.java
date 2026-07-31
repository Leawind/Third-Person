package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.bridge.Bridge;
import io.github.leawind.thirdperson.internal.logic.base.math.FiniteMath;
import io.github.leawind.thirdperson.internal.logic.base.rotation.LookGeometry;
import io.github.leawind.thirdperson.internal.logic.base.rotation.LookRotation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

/// Aligns the authoritative player ray with the rendered camera intent before vanilla repicks.
public final class MinecraftInteractionIntegration {
  private MinecraftInteractionIntegration() {}

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
    double reach = Bridge.interactionRange(minecraft);
    if (!FiniteMath.isFinite(cameraForward) || !Double.isFinite(reach) || reach <= 0.0) {
      return false;
    }

    Vector3d cameraRayEnd =
        new Vector3d(cameraPosition)
            .fma(reach, new Vector3d(cameraForward.x, cameraForward.y, cameraForward.z));
    Vec3 from = new Vec3(cameraPosition.x, cameraPosition.y, cameraPosition.z);
    Vec3 to = new Vec3(cameraRayEnd.x, cameraRayEnd.y, cameraRayEnd.z);
    Bridge.BlockHit blockIntent = Bridge.clipBlocks(player, from, to, false);
    Vector3d intentPoint =
        blockIntent.missed() ? cameraRayEnd : toVector3d(blockIntent.location());

    Vec3 eye = player.getEyePosition(1.0f);
    return LookGeometry.lookAt(new Vector3d(eye.x, eye.y, eye.z), intentPoint)
        .map(
            rotation -> {
              setPlayerRotation(player, rotation);
              return true;
            })
        .orElse(false);
  }

  private static void setPlayerRotation(LocalPlayer player, LookRotation rotation) {
    player.setYRot(rotation.yawDegrees());
    player.setXRot(rotation.pitchDegrees());
  }

  private static Vector3d toVector3d(Vec3 value) {
    return new Vector3d(value.x, value.y, value.z);
  }
}
