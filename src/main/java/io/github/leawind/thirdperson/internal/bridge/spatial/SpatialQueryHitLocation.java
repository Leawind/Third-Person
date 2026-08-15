package io.github.leawind.thirdperson.internal.bridge.spatial;

import io.github.leawind.thirdperson.internal.core.api.PriorityResolverRegistry;
import java.util.Objects;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/// Interprets locations returned by Minecraft spatial queries.
public final class SpatialQueryHitLocation {
  private static final Object REGISTRY_LOCK = new Object();
  private static final PriorityResolverRegistry.Builder<Context, Vec3> BUILDER =
      PriorityResolverRegistry.builder();
  private static volatile PriorityResolverRegistry<Context, Vec3> resolvers;

  private SpatialQueryHitLocation() {}

  public static void registerResolver(
      String id, int priority, SpatialQueryHitLocationResolver resolver) {
    Objects.requireNonNull(resolver, "resolver");
    synchronized (REGISTRY_LOCK) {
      if (resolvers != null) {
        throw new IllegalStateException(
            "Spatial-query hit extension registration is already frozen");
      }
      BUILDER.register(
          id,
          priority,
          context -> resolver.resolveWorldLocation(context.level(), context.hit()));
    }
  }

  public static Vec3 resolve(Level level, HitResult hit) {
    freezeRegistry();
    return resolvers
        .resolve(
            new Context(
                Objects.requireNonNull(level, "level"), Objects.requireNonNull(hit, "hit")))
        .orElseThrow(() -> new IllegalStateException("No spatial-query hit resolver handled hit"));
  }

  private static void freezeRegistry() {
    if (resolvers != null) {
      return;
    }
    synchronized (REGISTRY_LOCK) {
      if (resolvers == null) {
        resolvers = BUILDER.freeze();
      }
    }
  }

  private record Context(Level level, HitResult hit) {}
}
