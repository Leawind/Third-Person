package io.github.leawind.thirdperson.internal.extension.camera;

import io.github.leawind.thirdperson.internal.core.api.ExtensionPoint;
import io.github.leawind.thirdperson.internal.core.api.PriorityResolverRegistry;
import java.util.Objects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

/// Measures the active camera entity and its complete vehicle hierarchy.
public final class MinecraftCameraSubjectMeasurements {
  private static final ExtensionPoint<
          PriorityResolverRegistry.Resolver<Entity, AABB>, PriorityResolverRegistry<Entity, AABB>>
      EXTENSION_POINT = ExtensionPoint.resolver();

  private MinecraftCameraSubjectMeasurements() {}

  public static void registerResolver(
      String id, int priority, CameraSubjectBoundsResolver resolver) {
    Objects.requireNonNull(resolver, "resolver");
    EXTENSION_POINT.register(id, priority, resolver::resolveBounds);
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
    return EXTENSION_POINT
        .get()
        .resolve(entity)
        .orElseThrow(
            () -> new IllegalStateException("No camera-subject bounds resolver handled entity"));
  }

  public record Measurements(double bodyRadius, double vehicleTotalSize) {}
}
