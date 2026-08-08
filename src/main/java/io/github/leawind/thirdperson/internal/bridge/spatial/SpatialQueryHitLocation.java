package io.github.leawind.thirdperson.internal.bridge.spatial;

import io.github.leawind.thirdperson.internal.bridge.compat.sable.SableSpatialQueryHitLocationResolver;
import java.util.Objects;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/// Interprets locations returned by Minecraft spatial queries.
public final class SpatialQueryHitLocation {
  private SpatialQueryHitLocation() {}

  public static Vec3 resolve(Level level, HitResult hit) {
    return Holder.RESOLVER.resolveWorldLocation(
        Objects.requireNonNull(level, "level"), Objects.requireNonNull(hit, "hit"));
  }

  private static final class Holder {
    private static final SpatialQueryHitLocationResolver RESOLVER =
        SableSpatialQueryHitLocationResolver.createIfAvailable()
            .orElse(VanillaResolver.INSTANCE);
  }

  private enum VanillaResolver implements SpatialQueryHitLocationResolver {
    INSTANCE;

    @Override
    public Vec3 resolveWorldLocation(Level level, HitResult hit) {
      return hit.getLocation();
    }
  }
}
