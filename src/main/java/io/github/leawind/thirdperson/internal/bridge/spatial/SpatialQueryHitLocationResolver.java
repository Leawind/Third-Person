package io.github.leawind.thirdperson.internal.bridge.spatial;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/// Resolves the world-space location represented by a raw Minecraft spatial-query result.
public interface SpatialQueryHitLocationResolver {
  /// Returns the location used for distance and geometry calculations.
  ///
  /// The raw hit remains unchanged for subsequent Minecraft interaction handling.
  Vec3 resolveWorldLocation(Level level, HitResult hit);
}
