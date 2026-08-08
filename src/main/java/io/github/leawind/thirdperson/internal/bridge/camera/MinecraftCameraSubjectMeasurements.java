package io.github.leawind.thirdperson.internal.bridge.camera;

import io.github.leawind.thirdperson.internal.bridge.compat.sable.SableCameraSubjectBoundsResolver;
import java.util.Objects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

/// Measures the active camera entity and its complete vehicle hierarchy.
public final class MinecraftCameraSubjectMeasurements {
  private MinecraftCameraSubjectMeasurements() {}

  public static Measurements measure(Entity cameraEntity) {
    Objects.requireNonNull(cameraEntity, "cameraEntity");
    Entity rootVehicle = cameraEntity.getRootVehicle();
    AABB vehicleBounds =
        rootVehicle
            .getPassengersAndSelf()
            .map(Holder.BOUNDS_RESOLVER::resolveBounds)
            .reduce(AABB::minmax)
            .orElseGet(() -> Holder.BOUNDS_RESOLVER.resolveBounds(rootVehicle));
    double vehicleTotalSize =
        Math.hypot(
            Math.hypot(vehicleBounds.getXsize(), vehicleBounds.getYsize()),
            vehicleBounds.getZsize());
    double bodyRadius = cameraEntity.getBbWidth() * 0.5 * Math.sqrt(3.0);
    return new Measurements(bodyRadius, vehicleTotalSize);
  }

  private static final class Holder {
    private static final CameraSubjectBoundsResolver BOUNDS_RESOLVER =
        SableCameraSubjectBoundsResolver.createIfAvailable().orElse(VanillaResolver.INSTANCE);
  }

  private enum VanillaResolver implements CameraSubjectBoundsResolver {
    INSTANCE;

    @Override
    public AABB resolveBounds(Entity entity) {
      return entity.getBoundingBox();
    }
  }

  public record Measurements(double bodyRadius, double vehicleTotalSize) {}
}
