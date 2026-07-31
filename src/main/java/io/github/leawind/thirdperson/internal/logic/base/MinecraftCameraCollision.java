package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.logic.base.CameraCollisionResolver;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Adapts Minecraft block clipping to the vanilla-equivalent camera collision resolver.
public final class MinecraftCameraCollision {
  private MinecraftCameraCollision() {}

  public static Optional<Vector3d> resolve(
      Entity entity, Vector3dc pivot, Vector3dc desiredCamera) {
    Objects.requireNonNull(entity, "entity");
    return CameraCollisionResolver.resolve(
        pivot,
        desiredCamera,
        (from, to) -> {
          HitResult hit =
              entity
                  .level()
                  .clip(
                      new ClipContext(
                          toVec3(from),
                          toVec3(to),
                          ClipContext.Block.VISUAL,
                          ClipContext.Fluid.NONE,
                          entity));
          return hit.getType() == HitResult.Type.MISS
              ? Optional.empty()
              : Optional.of(toVector3d(hit.getLocation()));
        });
  }

  private static Vec3 toVec3(Vector3dc value) {
    return new Vec3(value.x(), value.y(), value.z());
  }

  private static Vector3d toVector3d(Vec3 value) {
    return new Vector3d(value.x, value.y, value.z);
  }
}
