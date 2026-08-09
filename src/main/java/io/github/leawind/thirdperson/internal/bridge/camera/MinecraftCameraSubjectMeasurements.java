package io.github.leawind.thirdperson.internal.bridge.camera;

import io.github.leawind.thirdperson.internal.bridge.compat.sable.SableCameraSubjectBoundsResolver;
import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import io.github.leawind.thirdperson.internal.core.api.PriorityResolverRegistry;
import java.util.Objects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

/// Measures the active camera entity and its complete vehicle hierarchy.
public final class MinecraftCameraSubjectMeasurements {
  private static final Object REGISTRY_LOCK = new Object();
  private static final PriorityResolverRegistry.Builder<Entity, AABB> BUILDER = createBuilder();
  private static volatile PriorityResolverRegistry<Entity, AABB> resolvers;

  private MinecraftCameraSubjectMeasurements() {}

  public static void registerResolver(
      String id, int priority, CameraSubjectBoundsResolver resolver) {
    Objects.requireNonNull(resolver, "resolver");
    synchronized (REGISTRY_LOCK) {
      if (resolvers != null) {
        throw new IllegalStateException(
            "Camera-subject bounds extension registration is already frozen");
      }
      BUILDER.register(id, priority, resolver::resolveBounds);
    }
  }

  public static Measurements measure(Entity cameraEntity) {
    Objects.requireNonNull(cameraEntity, "cameraEntity");
    Entity rootVehicle = cameraEntity.getRootVehicle();
    AABB vehicleBounds =
        rootVehicle
            .getPassengersAndSelf()
            .map(MinecraftCameraSubjectMeasurements::resolveBounds)
            .reduce(AABB::minmax)
            .orElseGet(() -> resolveBounds(rootVehicle));
    double vehicleTotalSize =
        Math.hypot(
            Math.hypot(vehicleBounds.getXsize(), vehicleBounds.getYsize()),
            vehicleBounds.getZsize());
    double bodyRadius = cameraEntity.getBbWidth() * 0.5 * Math.sqrt(3.0);
    return new Measurements(bodyRadius, vehicleTotalSize);
  }

  private static AABB resolveBounds(Entity entity) {
    freezeRegistry();
    return resolvers
        .resolve(entity)
        .orElseThrow(
            () -> new IllegalStateException("No camera-subject bounds resolver handled entity"));
  }

  private static PriorityResolverRegistry.Builder<Entity, AABB> createBuilder() {
    var builder = PriorityResolverRegistry.<Entity, AABB>builder();
    SableCameraSubjectBoundsResolver.createIfAvailable()
        .ifPresent(resolver -> builder.register("sable", 100, resolver::resolveBounds));
    builder.register("vanilla", 0, VanillaResolver.INSTANCE::resolveBounds);
    return builder;
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

  private enum VanillaResolver implements CameraSubjectBoundsResolver {
    INSTANCE;

    @Override
    public ExtensionResult<AABB> resolveBounds(Entity entity) {
      return ExtensionResult.handled(entity.getBoundingBox());
    }
  }

  public record Measurements(double bodyRadius, double vehicleTotalSize) {}
}
