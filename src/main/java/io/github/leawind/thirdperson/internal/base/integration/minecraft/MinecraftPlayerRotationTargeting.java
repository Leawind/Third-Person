package io.github.leawind.thirdperson.internal.base.integration.minecraft;

import io.github.leawind.thirdperson.internal.base.api.LookRotation;
import io.github.leawind.thirdperson.internal.base.api.RaycastOrigin;
import io.github.leawind.thirdperson.internal.base.application.BaseRuntime;
import io.github.leawind.thirdperson.internal.base.core.player.PlayerRotationGeometry;
import io.github.leawind.thirdperson.internal.base.core.rotation.LookGeometry;
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
public final class MinecraftPlayerRotationTargeting {
  private static final double CAMERA_RAY_TRACE_LENGTH = 512.0;
  private static final double TARGET_PREDICTION_DEGREES_LIMIT = 30.0;
  private static final float VANILLA_HEAD_ROTATION_LIMIT_DEGREES = 50.0f;

  private MinecraftPlayerRotationTargeting() {}

  static Optional<CameraView> cameraView(BaseRuntime runtime) {
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
      Minecraft minecraft, BaseRuntime runtime, boolean useColliderBlocks) {
    LocalPlayer player = minecraft.player;
    if (player == null || minecraft.level == null) {
      return Optional.empty();
    }
    return cameraView(runtime)
        .map(
            view -> {
              Vector3d rayStart =
                  runtime.parameters().raycastOrigin() == RaycastOrigin.PLAYER_EYE
                      ? toVector(player.getEyePosition(1.0f))
                      : view.position();
              double rayLength =
                  CAMERA_RAY_TRACE_LENGTH
                      + rayStart.distance(toVector(player.getEyePosition(1.0f)))
                      + player.getBbWidth() * 0.8660254037844386;
              Vec3 from = toVec3(rayStart);
              Vec3 to = toVec3(new Vector3d(rayStart).fma(rayLength, view.forward()));
              HitResult blockHit =
                  minecraft.level.clip(
                      new ClipContext(
                          from,
                          to,
                          useColliderBlocks
                              ? ClipContext.Block.COLLIDER
                              : ClipContext.Block.OUTLINE,
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
      Minecraft minecraft, BaseRuntime runtime, CameraHit cameraHit) {
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

  public static Optional<LookRotation> cameraRayHitRotation(BaseRuntime runtime) {
    return cameraRayHitRotation(runtime, false);
  }

  public static Optional<LookRotation> predictedCameraTargetRotation(BaseRuntime runtime) {
    return cameraRayHitRotation(runtime, true);
  }

  public static Optional<LookRotation> interestPointRotation(BaseRuntime runtime) {
    Minecraft minecraft = Minecraft.getInstance();
    LocalPlayer player = minecraft.player;
    if (player == null) {
      return Optional.empty();
    }
    var cameraView = cameraView(runtime).orElse(null);
    if (cameraView == null) {
      return Optional.empty();
    }
    float cameraYaw = runtime.session().lookController().yawDegrees();
    boolean cameraBehindPlayer =
        Math.abs(PlayerRotationGeometry.shortestDifference(cameraYaw, player.yBodyRot)) < 90.0f;
    Optional<Vector3d> point =
        cameraBehindPlayer
            ? cameraHit(minecraft, runtime, false).map(CameraHit::location)
            : Optional.of(cameraView.position());
    return point
        .flatMap(target -> lookAtPlayerEye(player, target))
        .map(
            rotation ->
                new LookRotation(
                    PlayerRotationGeometry.clampYawAround(
                        rotation.yawDegrees(),
                        player.yBodyRot,
                        VANILLA_HEAD_ROTATION_LIMIT_DEGREES),
                    rotation.pitchDegrees()));
  }

  private static Optional<LookRotation> cameraRayHitRotation(
      BaseRuntime runtime, boolean predictTargetEntity) {
    Minecraft minecraft = Minecraft.getInstance();
    LocalPlayer player = minecraft.player;
    if (player == null) {
      return Optional.empty();
    }
    return cameraHit(minecraft, runtime, predictTargetEntity)
        .flatMap(
            hit -> {
              Optional<Vector3d> predicted =
                  predictTargetEntity
                      ? predictedTargetPoint(minecraft, runtime, hit)
                      : Optional.empty();
              if (predicted.isPresent()) {
                return lookAtPlayerEye(player, predicted.orElseThrow());
              }
              if (hit.missed()) {
                var lookController = runtime.session().lookController();
                return Optional.of(
                    new LookRotation(
                        lookController.yawDegrees(), lookController.pitchDegrees()));
              }
              return lookAtPlayerEye(player, hit.location());
            });
  }

  private static Optional<LookRotation> lookAtPlayerEye(
      LocalPlayer player, Vector3d point) {
    var eye = player.getEyePosition(1.0f);
    return LookGeometry.lookAt(new Vector3d(eye.x, eye.y, eye.z), point);
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
