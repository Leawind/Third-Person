package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.bridge.MinecraftSpatialQuerying;
import io.github.leawind.thirdperson.internal.bridge.compat.sable.SableCompatibility;
import io.github.leawind.thirdperson.internal.logic.base.rotation.LookGeometry;
import io.github.leawind.thirdperson.internal.logic.base.rotation.LookRotation;
import io.github.leawind.thirdperson.internal.logic.base.rotation.PlayerRotationGeometry;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Resolves the camera-space points used by the legacy player-rotation targets.
public final class MinecraftPlayerRotationTargeting {
  private static final double TARGET_PREDICTION_DEGREES_LIMIT = 30.0;
  private static final float VANILLA_HEAD_ROTATION_LIMIT_DEGREES = 50.0f;

  private MinecraftPlayerRotationTargeting() {}

  static Optional<Vector3d> predictedTargetPoint(
      Minecraft minecraft, BaseRuntime runtime, MinecraftCameraRaycasting.CameraHit cameraHit) {
    LocalPlayer player = minecraft.player;
    if (player == null || minecraft.level == null || cameraHit.blocked()) {
      return Optional.empty();
    }
    return MinecraftCameraRaycasting.cameraRay(runtime.session())
        .flatMap(
            cameraRay -> {
              Vector3dc cameraPosition = cameraRay.origin();
              Vector3dc cameraForward = cameraRay.direction();
              Entity best = null;
              double bestCost = Double.POSITIVE_INFINITY;
              double cameraPitch =
                  Math.toDegrees(
                      Math.atan2(
                          -cameraForward.y(), Math.hypot(cameraForward.x(), cameraForward.z())));
              for (Entity candidate :
                  MinecraftSpatialQuerying.entitiesInInflatedBounds(
                      player,
                      MinecraftCameraRaycasting.TRACE_LENGTH,
                      entity -> entity instanceof LivingEntity)) {
                double playerDistance = candidate.distanceTo(player);
                if (playerDistance < 2.0
                    || playerDistance > MinecraftCameraRaycasting.TRACE_LENGTH) {
                  continue;
                }
                Vec3 candidatePosition = candidate.getPosition(1.0f);
                Vector3d eyeToBottom =
                    new Vector3d(
                            candidatePosition.x,
                            candidate.getBoundingBox().minY,
                            candidatePosition.z)
                        .sub(toVector(SableCompatibility.getEyePositionInterpolated(player, 1.0f)));
                double bottomPitch =
                    Math.toDegrees(
                        Math.atan2(-eyeToBottom.y, Math.hypot(eyeToBottom.x, eyeToBottom.z)));
                if (!Double.isFinite(bottomPitch) || bottomPitch < cameraPitch) {
                  continue;
                }

                Vector3d cameraToTarget =
                    toVector(candidatePosition).sub(cameraPosition, new Vector3d());
                double distance = cameraToTarget.length();
                if (!Double.isFinite(distance) || distance <= 1.0e-5) {
                  continue;
                }
                cameraToTarget.div(distance);
                double dot = Math.max(-1.0, Math.min(1.0, cameraForward.dot(cameraToTarget)));
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
              double targetDistance = cameraPosition.distance(toVector(best.getPosition(1.0f)));
              return cameraRay.pointAt(targetDistance);
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
    var cameraRay = MinecraftCameraRaycasting.cameraRay(runtime.session()).orElse(null);
    if (cameraRay == null) {
      return Optional.empty();
    }
    float cameraYaw = runtime.session().lookController().yawDegrees();
    boolean cameraBehindPlayer =
        Math.abs(PlayerRotationGeometry.shortestDifference(cameraYaw, player.yBodyRot)) < 90.0f;
    Optional<Vector3dc> point =
        cameraBehindPlayer
            ? MinecraftCameraRaycasting.cameraHit(minecraft, runtime, false)
                .map(MinecraftCameraRaycasting.CameraHit::location)
            : Optional.of(cameraRay.origin());
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
    return MinecraftCameraRaycasting.cameraHit(minecraft, runtime, predictTargetEntity)
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
                    new LookRotation(lookController.yawDegrees(), lookController.pitchDegrees()));
              }
              return lookAtPlayerEye(player, hit.location());
            });
  }

  private static Optional<LookRotation> lookAtPlayerEye(LocalPlayer player, Vector3dc point) {
    var eye = player.getEyePosition(1.0f);
    return LookGeometry.lookAt(new Vector3d(eye.x, eye.y, eye.z), point);
  }

  private static Vector3d toVector(Vec3 vector) {
    return new Vector3d(vector.x, vector.y, vector.z);
  }
}
