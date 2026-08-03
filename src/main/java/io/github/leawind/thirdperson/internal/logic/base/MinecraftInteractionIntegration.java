package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.bridge.Bridge;
import io.github.leawind.thirdperson.internal.bridge.events.BeforeInteractionEvent;
import io.github.leawind.thirdperson.internal.logic.base.math.FiniteMath;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

/// Maintains the authoritative camera-directed hit result used by rendering and interaction.
public final class MinecraftInteractionIntegration {
  private MinecraftInteractionIntegration() {}

  public static BeforeInteractionEvent.Result prepareInteractionRaycast() {
    return refreshRaycast(1.0f)
        ? BeforeInteractionEvent.Result.APPLIED
        : BeforeInteractionEvent.Result.PASS;
  }

  /// Replaces vanilla's current hit result with the configured camera-directed raycast.
  public static boolean refreshRaycast(float partialTick) {
    BaseRuntime runtime = BaseRuntime.getInstance();
    Minecraft minecraft = Minecraft.getInstance();
    var player = minecraft.player;
    if (!PerspectiveGuard.isThirdPersonCurrentForLocalPlayer()
        || !runtime.isCameraControlEnabled()
        || player == null
        || minecraft.level == null
        || !Float.isFinite(partialTick)) {
      return false;
    }

    var cameraPose = runtime.session().finalCameraPose().orElse(null);
    if (cameraPose == null) {
      return false;
    }
    Vector3d cameraPosition = cameraPose.copyPosition(new Vector3d());
    Quaternionf cameraRotation = cameraPose.copyRotation(new Quaternionf());
    Vector3f cameraForward = cameraRotation.transform(new Vector3f(0.0f, 0.0f, 1.0f));
    Vec3 eye = player.getEyePosition(partialTick);
    Vec3 cameraOrigin = new Vec3(cameraPosition.x, cameraPosition.y, cameraPosition.z);
    Vec3 from = runtime.parameters().raycastOrigin() == RaycastOrigin.CAMERA ? cameraOrigin : eye;
    double blockRange = Bridge.blockInteractionRange(minecraft);
    double entityRange = Bridge.entityInteractionRange(minecraft);
    double originExtension =
        runtime.parameters().raycastOrigin() == RaycastOrigin.CAMERA
            ? cameraOrigin.distanceTo(eye)
            : 0.0;
    double candidateRange =
        InteractionRaycastGeometry.candidateRange(blockRange, entityRange, originExtension);
    if (!FiniteMath.isFinite(cameraForward)
        || !Double.isFinite(candidateRange)
        || candidateRange <= 0.0) {
      return false;
    }

    Vec3 direction = new Vec3(cameraForward.x, cameraForward.y, cameraForward.z).normalize();
    Bridge.SpatialHit cameraHit = Bridge.pickFrom(player, from, direction, candidateRange);
    HitResult filtered = filterByPlayerReach(cameraHit, eye, blockRange, entityRange);
    minecraft.hitResult = filtered;
    minecraft.crosshairPickEntity =
        filtered instanceof EntityHitResult entityHit ? entityHit.getEntity() : null;
    return true;
  }

  private static HitResult filterByPlayerReach(
      Bridge.SpatialHit spatialHit, Vec3 playerEye, double blockRange, double entityRange) {
    HitResult hit = spatialHit.rawHit();
    double allowedRange = hit instanceof EntityHitResult ? entityRange : blockRange;
    return InteractionRaycastGeometry.isWithinRange(
            spatialHit.worldLocation().distanceToSqr(playerEye), allowedRange)
        ? hit
        : Bridge.missAt(spatialHit.worldLocation(), playerEye);
  }
}
