package io.github.leawind.thirdperson.internal.extension.spatial;

import io.github.leawind.thirdperson.internal.core.api.ExtensionPoint;
import io.github.leawind.thirdperson.internal.core.api.PriorityResolverRegistry;
import java.util.Objects;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/// Interprets locations returned by Minecraft spatial queries.
public final class SpatialQueryHitLocation {
  private static final ExtensionPoint<
          PriorityResolverRegistry.Resolver<Context, Vec3>,
          PriorityResolverRegistry<Context, Vec3>>
      EXTENSION_POINT = ExtensionPoint.resolver();

  private SpatialQueryHitLocation() {}

  public static void registerResolver(
      String id, int priority, SpatialQueryHitLocationResolver resolver) {
    Objects.requireNonNull(resolver, "resolver");
    EXTENSION_POINT.register(
        id, priority, context -> resolver.resolveWorldLocation(context.level(), context.hit()));
  }

  public static Vec3 resolve(Level level, HitResult hit) {
    return EXTENSION_POINT
        .get()
        .resolve(
            new Context(
                Objects.requireNonNull(level, "level"), Objects.requireNonNull(hit, "hit")))
        .orElseThrow(() -> new IllegalStateException("No spatial-query hit resolver handled hit"));
  }

  private record Context(Level level, HitResult hit) {}
}
