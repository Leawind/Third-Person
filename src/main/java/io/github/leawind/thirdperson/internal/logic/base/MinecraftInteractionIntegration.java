package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.bridge.Bridge;
import io.github.leawind.thirdperson.internal.bridge.MinecraftAttackRangePicking;
import io.github.leawind.thirdperson.internal.bridge.events.BeforeInteractionEvent;
import io.github.leawind.thirdperson.internal.logic.base.math.FiniteMath;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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
    var attackRangeHit =
        pickWithActiveAttackRange(player, eye, from, direction, originExtension, blockRange);
    HitResult filtered =
        attackRangeHit
            .filter(hit -> hit.getType() != HitResult.Type.MISS)
            .orElseGet(
                () ->
                    filterByPlayerReach(
                        Bridge.pickFrom(player, from, direction, candidateRange),
                        eye,
                        blockRange,
                        entityRange));
    minecraft.hitResult = filtered;
    minecraft.crosshairPickEntity =
        filtered instanceof EntityHitResult entityHit ? entityHit.getEntity() : null;
    return true;
  }

  private static Optional<HitResult> pickWithActiveAttackRange(
      LocalPlayer player,
      Vec3 playerEye,
      Vec3 from,
      Vec3 direction,
      double originExtension,
      double blockRange) {
    var parameters = MinecraftAttackRangePicking.parameters(player, direction).orElse(null);
    if (parameters == null) {
      return Optional.empty();
    }
    double candidateRange =
        InteractionRaycastGeometry.attackCandidateRange(
            parameters.maximumRange(), parameters.forwardMovement(), originExtension);
    if (!Double.isFinite(candidateRange) || candidateRange <= 0.0) {
      return Optional.of(Bridge.missAt(from, from));
    }

    var candidates =
        MinecraftAttackRangePicking.collectCandidates(
            player,
            from,
            direction,
            originExtension == 0.0 ? parameters.minimumRange() : 0.0,
            candidateRange,
            parameters.hitboxMargin());
    Bridge.SpatialHit closestHit = null;
    double closestDistanceSquared = Double.POSITIVE_INFINITY;
    for (Bridge.SpatialHit candidate : candidates.entityHits()) {
      if (!InteractionRaycastGeometry.isWithinAttackRange(
          candidate.worldLocation().distanceToSqr(playerEye),
          parameters.minimumRange(),
          parameters.maximumRange(),
          parameters.hitboxMargin(),
          parameters.forwardMovement())) {
        continue;
      }
      double distanceSquared = candidate.worldLocation().distanceToSqr(from);
      if (distanceSquared < closestDistanceSquared) {
        closestHit = candidate;
        closestDistanceSquared = distanceSquared;
      }
    }
    return Optional.of(
        closestHit == null
            ? filterAttackRangeHit(candidates.blockHit(), playerEye, blockRange)
            : closestHit.rawHit());
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

  private static HitResult filterAttackRangeHit(
      Bridge.SpatialHit spatialHit, Vec3 playerEye, double blockRange) {
    HitResult hit = spatialHit.rawHit();
    if (hit instanceof EntityHitResult) {
      return hit;
    }
    return InteractionRaycastGeometry.isWithinRange(
            spatialHit.worldLocation().distanceToSqr(playerEye), blockRange)
        ? hit
        : Bridge.missAt(spatialHit.worldLocation(), playerEye);
  }
}
