package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.bridge.MinecraftRaycasting;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;

/// Creates and probes the ray that represents the final camera pose's intent.
///
/// This path deliberately has no dependency on the configured interaction raycast origin.
public final class MinecraftCameraRaycasting {
  static final double TRACE_LENGTH = 512.0;

  private MinecraftCameraRaycasting() {}

  /// Ray starts from camera, towards the forward direction of camera.
  static Optional<WorldRay> cameraRay(BaseSession session) {
    return session
        .finalCameraPose()
        .flatMap(
            pose -> {
              Vector3d position = pose.copyPosition(new Vector3d());
              Vector3f forward =
                  pose.copyRotation(new Quaternionf()).transform(new Vector3f(0.0f, 0.0f, 1.0f));
              return WorldRay.tryCreate(position, new Vector3d(forward));
            });
  }

  static Optional<CameraHit> cameraHit(
      Minecraft minecraft, BaseRuntime runtime, boolean useColliderBlocks) {
    LocalPlayer player = minecraft.player;
    if (player == null || minecraft.level == null) {
      return Optional.empty();
    }
    return cameraRay(runtime.session())
        .flatMap(
            ray -> {
              Vector3dc origin = ray.origin();
              Vector3dc direction = ray.direction();
              Vec3 eye = player.getEyePosition(1.0f);
              Vec3 from = toVec3(origin);
              double cameraToPlayerEyeDistance =
                  InteractionRaycastGeometry.capCameraOriginExtension(
                      origin.distance(new Vector3d(eye.x, eye.y, eye.z)));
              double rayLength =
                  TRACE_LENGTH
                      + cameraToPlayerEyeDistance
                      + player.getBbWidth() * 0.8660254037844386;
              Vector3d endpoint = ray.pointAt(rayLength).orElse(null);
              if (endpoint == null) {
                return Optional.empty();
              }

              Vec3 to = toVec3(endpoint);
              MinecraftRaycasting.BlockHit blockHit =
                  MinecraftRaycasting.clipBlocks(player, from, to, useColliderBlocks);
              double blockDistanceSquared = from.distanceToSqr(blockHit.worldLocation());
              Vec3 entityRayEnd =
                  blockHit.missed()
                      ? to
                      : from.add(
                          direction.x() * (Math.sqrt(blockDistanceSquared) + 1.0),
                          direction.y() * (Math.sqrt(blockDistanceSquared) + 1.0),
                          direction.z() * (Math.sqrt(blockDistanceSquared) + 1.0));
              Optional<Vec3> entityHit =
                  MinecraftRaycasting.pickEntity(
                      player, from, entityRayEnd, from.distanceToSqr(entityRayEnd));
              if (entityHit.isPresent()
                  && from.distanceToSqr(entityHit.orElseThrow()) < blockDistanceSquared) {
                return Optional.of(new CameraHit(toVector(entityHit.orElseThrow()), false, false));
              }
              return Optional.of(
                  new CameraHit(
                      toVector(blockHit.worldLocation()), blockHit.blocked(), blockHit.missed()));
            });
  }

  private static Vec3 toVec3(Vector3dc vector) {
    return new Vec3(vector.x(), vector.y(), vector.z());
  }

  private static Vector3d toVector(Vec3 vector) {
    return new Vector3d(vector.x, vector.y, vector.z);
  }

  record CameraHit(Vector3d location, boolean blocked, boolean missed) {
    CameraHit {
      location = new Vector3d(location);
    }

    @Override
    public Vector3d location() {
      return new Vector3d(location);
    }
  }
}
