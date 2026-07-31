package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.bridge.Bridge;
import io.github.leawind.thirdperson.internal.bridge.events.BeforeInteractionEvent;
import io.github.leawind.thirdperson.internal.logic.base.math.FiniteMath;
import io.github.leawind.thirdperson.internal.logic.base.rotation.LookGeometry;
import io.github.leawind.thirdperson.internal.logic.base.rotation.LookRotation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

/// Prepares the authoritative interaction ray before vanilla consumes the current hit result.
public final class MinecraftInteractionIntegration {
  private MinecraftInteractionIntegration() {}

  public static BeforeInteractionEvent.Result prepareInteractionRaycast() {
    BaseRuntime runtime = BaseRuntime.getInstance();
    Minecraft minecraft = Minecraft.getInstance();
    var player = minecraft.player;
    if (!PerspectiveGuard.isThirdPersonCurrentForLocalPlayer()
        || !runtime.isCameraControlEnabled()
        || player == null
        || minecraft.level == null) {
      return BeforeInteractionEvent.Result.PASS;
    }

    var cameraPose = runtime.session().finalCameraPose().orElse(null);
    if (cameraPose == null) {
      return BeforeInteractionEvent.Result.PASS;
    }
    Vector3d cameraPosition = cameraPose.copyPosition(new Vector3d());
    Quaternionf cameraRotation = cameraPose.copyRotation(new Quaternionf());
    Vector3f cameraForward = cameraRotation.transform(new Vector3f(0.0f, 0.0f, 1.0f));
    Vec3 eye = player.getEyePosition(1.0f);
    Vec3 from = new Vec3(cameraPosition.x, cameraPosition.y, cameraPosition.z);
    double blockRange = Bridge.blockInteractionRange(minecraft);
    double entityRange = Bridge.entityInteractionRange(minecraft);
    double candidateRange =
        InteractionRaycastGeometry.candidateRange(
            blockRange, entityRange, from.distanceTo(eye));
    if (!FiniteMath.isFinite(cameraForward)
        || !Double.isFinite(candidateRange)
        || candidateRange <= 0.0) {
      return BeforeInteractionEvent.Result.PASS;
    }

    Vec3 direction = new Vec3(cameraForward.x, cameraForward.y, cameraForward.z).normalize();
    HitResult cameraHit = Bridge.pickFrom(player, from, direction, candidateRange);
    if (runtime.parameters().raycastOrigin() == RaycastOrigin.CAMERA) {
      HitResult filtered = filterByPlayerReach(cameraHit, eye, blockRange, entityRange);
      minecraft.hitResult = filtered;
      minecraft.crosshairPickEntity =
          filtered instanceof EntityHitResult entityHit ? entityHit.getEntity() : null;
      return BeforeInteractionEvent.Result.APPLIED;
    }

    Vector3d intentPoint = toVector3d(cameraHit.getLocation());
    return LookGeometry.lookAt(new Vector3d(eye.x, eye.y, eye.z), intentPoint)
        .map(
            rotation -> {
              setPlayerRotation(player, rotation);
              return BeforeInteractionEvent.Result.REPICK;
            })
        .orElse(BeforeInteractionEvent.Result.PASS);
  }

  private static HitResult filterByPlayerReach(
      HitResult hit, Vec3 playerEye, double blockRange, double entityRange) {
    double allowedRange = hit instanceof EntityHitResult ? entityRange : blockRange;
    return InteractionRaycastGeometry.isWithinRange(
            hit.getLocation().distanceToSqr(playerEye), allowedRange)
        ? hit
        : Bridge.missAt(hit.getLocation(), playerEye);
  }

  private static void setPlayerRotation(LocalPlayer player, LookRotation rotation) {
    player.setYRot(rotation.yawDegrees());
    player.setXRot(rotation.pitchDegrees());
  }

  private static Vector3d toVector3d(Vec3 value) {
    return new Vector3d(value.x, value.y, value.z);
  }
}
