package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.bridge.Bridge;
import io.github.leawind.thirdperson.internal.bridge.MinecraftAttackRangePicking;
import io.github.leawind.thirdperson.internal.bridge.MinecraftSpatialQuerying;
import io.github.leawind.thirdperson.internal.bridge.compat.sable.SableCompatibility;
import io.github.leawind.thirdperson.internal.logic.base.rotation.LookGeometry;
import io.github.leawind.thirdperson.internal.logic.base.rotation.LookRotation;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Maintains the authoritative camera-directed hit result used by rendering and interaction.
public final class MinecraftInteractionIntegration {
  private MinecraftInteractionIntegration() {}

  /// Refreshes the final interaction hit and returns the matching player-eye rotation.
  public static Optional<LookRotation> prepareInteractionRaycast() {
    return refreshRaycastInternal(1.0f).flatMap(PreparedInteractionRaycast::playerRotation);
  }

  /// Replaces vanilla's current hit result with the configured camera-directed raycast.
  public static boolean refreshRaycast(float partialTick) {
    return refreshRaycastInternal(partialTick).isPresent();
  }

  private static Optional<PreparedInteractionRaycast> refreshRaycastInternal(float partialTick) {
    BaseRuntime runtime = BaseRuntime.getInstance();
    Minecraft minecraft = Minecraft.getInstance();
    var player = minecraft.player;
    if (!PerspectiveGuard.isThirdPersonCurrentForLocalPlayer()
        || !runtime.isCameraControlEnabled()
        || player == null
        || minecraft.level == null
        || !Float.isFinite(partialTick)) {
      return Optional.empty();
    }

    WorldRay cameraRay = MinecraftCameraRaycasting.cameraRay(runtime.session()).orElse(null);
    if (cameraRay == null) {
      return Optional.empty();
    }
    Vec3 eye = SableCompatibility.getEyePositionInterpolated(player, partialTick);
    Vector3d playerEye = toVector(eye);
    double blockRange = Bridge.blockInteractionRange(minecraft);
    double entityRange = Bridge.entityInteractionRange(minecraft);
    MinecraftSpatialQuerying.SpatialHit cameraIntent =
        pickAlongRay(player, eye, cameraRay, blockRange, entityRange).orElse(null);
    if (cameraIntent == null) {
      return Optional.empty();
    }

    WorldRay interactionRay =
        InteractionRaycastGeometry.selectInteractionRay(
                runtime.parameters().raycastOrigin(),
                cameraRay,
                playerEye,
                toVector(cameraIntent.worldLocation()))
            .orElse(null);
    if (interactionRay == null) {
      return Optional.empty();
    }
    MinecraftSpatialQuerying.SpatialHit selected =
        interactionRay == cameraRay
            ? cameraIntent
            : pickAlongRay(player, eye, interactionRay, blockRange, entityRange).orElse(null);
    if (selected == null) {
      return Optional.empty();
    }

    HitResult filtered =
        validateFinalHit(player, eye, interactionRay, selected, blockRange, entityRange);
    minecraft.hitResult = filtered;
    minecraft.crosshairPickEntity =
        filtered instanceof EntityHitResult entityHit ? entityHit.getEntity() : null;
    Optional<LookRotation> playerRotation =
        LookGeometry.lookAt(playerEye, toVector(selected.worldLocation()));
    return Optional.of(new PreparedInteractionRaycast(playerRotation));
  }

  private static Optional<MinecraftSpatialQuerying.SpatialHit> pickAlongRay(
      LocalPlayer player, Vec3 playerEye, WorldRay ray, double blockRange, double entityRange) {
    Vec3 from = toVec3(ray.origin());
    Vec3 direction = toVec3(ray.direction());
    double originExtension =
        InteractionRaycastGeometry.capCameraOriginExtension(from.distanceTo(playerEye));
    double candidateRange =
        InteractionRaycastGeometry.candidateRange(blockRange, entityRange, originExtension);
    if (!Double.isFinite(candidateRange) || candidateRange <= 0.0) {
      return Optional.empty();
    }

    var attackRangeHit =
        pickWithActiveAttackRange(player, playerEye, from, direction, originExtension);
    if (attackRangeHit.isPresent()
        && attackRangeHit.orElseThrow().rawHit().getType() != HitResult.Type.MISS) {
      return attackRangeHit;
    }
    return Optional.of(MinecraftSpatialQuerying.pickFrom(player, from, direction, candidateRange));
  }

  private static Optional<MinecraftSpatialQuerying.SpatialHit> pickWithActiveAttackRange(
      LocalPlayer player, Vec3 playerEye, Vec3 from, Vec3 direction, double originExtension) {
    var parameters = MinecraftAttackRangePicking.parameters(player, direction).orElse(null);
    if (parameters == null) {
      return Optional.empty();
    }
    double candidateRange =
        InteractionRaycastGeometry.attackCandidateRange(
            parameters.maximumRange(), parameters.forwardMovement(), originExtension);
    if (!Double.isFinite(candidateRange) || candidateRange <= 0.0) {
      return Optional.empty();
    }

    var candidates =
        MinecraftSpatialQuerying.collectCandidates(
            player,
            from,
            direction,
            originExtension == 0.0 ? parameters.minimumRange() : 0.0,
            candidateRange,
            parameters.hitboxMargin());
    MinecraftSpatialQuerying.SpatialHit closestHit = null;
    double closestDistanceSquared = Double.POSITIVE_INFINITY;
    for (MinecraftSpatialQuerying.SpatialHit candidate : candidates.entityHits()) {
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
    return Optional.of(closestHit == null ? candidates.blockHit() : closestHit);
  }

  private static HitResult validateFinalHit(
      LocalPlayer player,
      Vec3 playerEye,
      WorldRay ray,
      MinecraftSpatialQuerying.SpatialHit selected,
      double blockRange,
      double entityRange) {
    HitResult hit = selected.rawHit();
    double distanceSquared = selected.worldLocation().distanceToSqr(playerEye);
    boolean valid;
    if (hit instanceof EntityHitResult) {
      Vec3 direction = toVec3(ray.direction());
      var attackRange = MinecraftAttackRangePicking.parameters(player, direction).orElse(null);
      valid =
          attackRange == null
              ? InteractionRaycastGeometry.isWithinRange(distanceSquared, entityRange)
              : InteractionRaycastGeometry.isWithinAttackRange(
                  distanceSquared,
                  attackRange.minimumRange(),
                  attackRange.maximumRange(),
                  attackRange.hitboxMargin(),
                  attackRange.forwardMovement());
    } else {
      valid = InteractionRaycastGeometry.isWithinRange(distanceSquared, blockRange);
    }
    return valid ? hit : MinecraftSpatialQuerying.missAt(selected.worldLocation(), playerEye);
  }

  private static Vec3 toVec3(Vector3dc vector) {
    return new Vec3(vector.x(), vector.y(), vector.z());
  }

  private static Vector3d toVector(Vec3 vector) {
    return new Vector3d(vector.x, vector.y, vector.z);
  }

  private record PreparedInteractionRaycast(Optional<LookRotation> playerRotation) {}
}
