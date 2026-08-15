package io.github.leawind.thirdperson.internal.extension.minecraft;

import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import io.github.leawind.thirdperson.internal.extension.spatial.SpatialQueryHitLocationResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/// Interprets standard Minecraft spatial-query hits directly in world space.
final class MinecraftSpatialQueryHitLocationResolver implements SpatialQueryHitLocationResolver {
  static final MinecraftSpatialQueryHitLocationResolver INSTANCE =
      new MinecraftSpatialQueryHitLocationResolver();

  private MinecraftSpatialQueryHitLocationResolver() {}

  @Override
  public ExtensionResult<Vec3> resolveWorldLocation(Level level, HitResult hit) {
    return ExtensionResult.handled(hit.getLocation());
  }
}
