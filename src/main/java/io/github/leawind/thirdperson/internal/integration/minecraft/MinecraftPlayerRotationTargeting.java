package io.github.leawind.thirdperson.internal.integration.minecraft;

import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

/// Resolves the camera-space points used by the legacy player-rotation targets.
final class MinecraftPlayerRotationTargeting {
  private static final double CAMERA_RAY_TRACE_LENGTH = 512.0;
  private static final double TARGET_PREDICTION_DEGREES_LIMIT = 30.0;

  private MinecraftPlayerRotationTargeting() {}

  static Optional<CameraView> cameraView(ThirdPersonRuntime runtime) {
    return runtime
        .session()
        .finalCameraPose()
        .flatMap(
            pose -> {
              Vector3d position = pose.copyPosition(new Vector3d());
              Vector3f forward =
                  pose.copyRotation(new Quaternionf())
                      .transform(new Vector3f(0.0f, 0.0f, 1.0f));
              if (!Float.isFinite(forward.x)
                  || !Float.isFinite(forward.y)
                  || !Float.isFinite(forward.z)
                  || forward.lengthSquared() <= 1.0e-12f) {
                return Optional.empty();
              }
              forward.normalize();
              return Optional.of(new CameraView(position, new Vector3d(forward)));
            });
  }

  static Optional<CameraHit> cameraHit(
      Minecraft minecraft, ThirdPersonRuntime runtime, boolean aiming) {
    LocalPlayer player = minecraft.player;
    if (player == null || minecraft.level == null) {
      return Optional.empty();
    }
    return cameraView(runtime)
        .map(
            view -> {
              double rayLength =
                  CAMERA_RAY_TRACE_LENGTH
                      + view.position().distance(toVector(player.getEyePosition(1.0f)))
                      + player.getBbWidth() * 0.8660254037844386;
              Vec3 from = toVec3(view.position());
              Vec3 to = toVec3(new Vector3d(view.position()).fma(rayLength, view.forward()));
              HitResult blockHit =
                  minecraft.level.clip(
                      new ClipContext(
                          from,
                          to,
                          aiming ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE,
                          ClipContext.Fluid.NONE,
                          player));
              double blockDistanceSquared = from.distanceToSqr(blockHit.getLocation());
              Vec3 entityRayEnd =
                  blockHit.getType() == HitResult.Type.MISS
                      ? to
                      : from.add(
                          view.forward().x * (Math.sqrt(blockDistanceSquared) + 1.0),
                          view.forward().y * (Math.sqrt(blockDistanceSquared) + 1.0),
                          view.forward().z * (Math.sqrt(blockDistanceSquared) + 1.0));
              EntityHitResult entityHit =
                  ProjectileUtil.getEntityHitResult(
                      player,
                      from,
                      entityRayEnd,
                      new AABB(from, entityRayEnd),
                      entity -> !entity.isSpectator() && entity.isPickable(),
                      from.distanceToSqr(entityRayEnd));
              if (entityHit != null
                  && from.distanceToSqr(entityHit.getLocation()) < blockDistanceSquared) {
                return new CameraHit(toVector(entityHit.getLocation()), false, false);
              }
              return new CameraHit(
                  toVector(blockHit.getLocation()),
                  blockHit.getType() == HitResult.Type.BLOCK,
                  blockHit.getType() == HitResult.Type.MISS);
            });
  }

  static Optional<Vector3d> predictedTargetPoint(
      Minecraft minecraft, ThirdPersonRuntime runtime, CameraHit cameraHit) {
    LocalPlayer player = minecraft.player;
    if (player == null || minecraft.level == null || cameraHit.blocked()) {
      return Optional.empty();
    }
    return cameraView(runtime)
        .flatMap(
            view -> {
              Entity best = null;
              double bestCost = Double.POSITIVE_INFINITY;
              double cameraPitch =
                  Math.toDegrees(
                      Math.atan2(
                          -view.forward().y,
                          Math.hypot(view.forward().x, view.forward().z)));
              AABB searchArea = player.getBoundingBox().inflate(CAMERA_RAY_TRACE_LENGTH);
              for (Entity candidate :
                  minecraft.level.getEntities(
                      player, searchArea, entity -> entity instanceof LivingEntity)) {
                double playerDistance = candidate.distanceTo(player);
                if (playerDistance < 2.0 || playerDistance > CAMERA_RAY_TRACE_LENGTH) {
                  continue;
                }
                Vec3 candidatePosition = candidate.getPosition(1.0f);
                Vector3d eyeToBottom =
                    new Vector3d(
                            candidatePosition.x,
                            candidate.getBoundingBox().minY,
                            candidatePosition.z)
                        .sub(toVector(player.getEyePosition(1.0f)));
                double bottomPitch =
                    Math.toDegrees(
                        Math.atan2(-eyeToBottom.y, Math.hypot(eyeToBottom.x, eyeToBottom.z)));
                if (!Double.isFinite(bottomPitch) || bottomPitch < cameraPitch) {
                  continue;
                }

                Vector3d cameraToTarget =
                    toVector(candidatePosition).sub(view.position(), new Vector3d());
                double distance = cameraToTarget.length();
                if (!Double.isFinite(distance) || distance <= 1.0e-5) {
                  continue;
                }
                cameraToTarget.div(distance);
                double dot = Math.max(-1.0, Math.min(1.0, view.forward().dot(cameraToTarget)));
                double angleDegrees = Math.toDegrees(Math.acos(dot));
                if (!Double.isFinite(angleDegrees)
                    || angleDegrees >= TARGET_PREDICTION_DEGREES_LIMIT) {
                  continue;
                }
                double cost = distance * distance * Math.pow(angleDegrees, 2.5);
                if (Double.isFinite(cost) && cost < bestCost) {
                  best = candidate;
                  bestCost = cost;
                }
              }
              if (best == null) {
                return Optional.empty();
              }
              double targetDistance =
                  view.position().distance(toVector(best.getPosition(1.0f)));
              return Optional.of(
                  new Vector3d(view.position()).fma(targetDistance, view.forward()));
            });
  }

  private static Vec3 toVec3(Vector3d vector) {
    return new Vec3(vector.x, vector.y, vector.z);
  }

  private static Vector3d toVector(Vec3 vector) {
    return new Vector3d(vector.x, vector.y, vector.z);
  }

  record CameraView(Vector3d position, Vector3d forward) {}

  record CameraHit(Vector3d location, boolean blocked, boolean missed) {}
}
