package io.github.leawind.thirdperson.internal.integration.minecraft;

import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Adapts Minecraft block clipping to a validated camera segment.
public final class MinecraftCameraCollision {
  private static final double PROBE_RADIUS = 0.1;
  private static final double COLLISION_MARGIN = 0.03;

  private MinecraftCameraCollision() {}

  public static Optional<Vector3d> resolve(
      Entity entity, Vector3dc pivot, Vector3dc desiredCamera) {
    var direction = new Vector3d(desiredCamera).sub(pivot);
    double desiredDistance = direction.length();
    if (!Double.isFinite(desiredDistance)) {
      return Optional.empty();
    }
    if (desiredDistance <= 1.0e-9) {
      return Optional.of(new Vector3d(pivot));
    }
    direction.div(desiredDistance);

    double allowedDistance = desiredDistance;
    for (int i = 0; i < 8; i++) {
      double offsetX = ((i & 1) * 2 - 1) * PROBE_RADIUS;
      double offsetY = (((i >> 1) & 1) * 2 - 1) * PROBE_RADIUS;
      double offsetZ = (((i >> 2) & 1) * 2 - 1) * PROBE_RADIUS;
      Vec3 from = new Vec3(pivot.x() + offsetX, pivot.y() + offsetY, pivot.z() + offsetZ);
      Vec3 to =
          new Vec3(
              desiredCamera.x() + offsetX,
              desiredCamera.y() + offsetY,
              desiredCamera.z() + offsetZ);
      HitResult hit =
          entity
              .level()
              .clip(
                  new ClipContext(
                      from, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));
      if (hit.getType() == HitResult.Type.MISS) {
        continue;
      }

      double hitDistance = hit.getLocation().distanceTo(from);
      if (Double.isFinite(hitDistance)) {
        allowedDistance =
            Math.min(allowedDistance, Math.max(0.0, hitDistance - COLLISION_MARGIN));
      }
    }

    return Optional.of(new Vector3d(pivot).fma(allowedDistance, direction));
  }
}
