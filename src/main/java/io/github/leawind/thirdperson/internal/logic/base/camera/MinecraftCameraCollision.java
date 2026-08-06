package io.github.leawind.thirdperson.internal.logic.base.camera;

import io.github.leawind.thirdperson.internal.bridge.MinecraftRaycasting;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Adapts Minecraft block clipping to the vanilla-equivalent camera collision resolver.
public final class MinecraftCameraCollision {
  private MinecraftCameraCollision() {}

  public static Optional<Vector3d> resolve(
      Entity entity, Vector3dc pivot, Vector3dc desiredCamera) {
    Objects.requireNonNull(entity, "entity");
    return CameraCollisionResolver.resolve(
        pivot, desiredCamera, (from, to) -> MinecraftRaycasting.clipVisualBlocks(entity, from, to));
  }
}
