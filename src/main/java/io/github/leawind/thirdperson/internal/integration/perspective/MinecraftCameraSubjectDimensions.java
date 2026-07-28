package io.github.leawind.thirdperson.internal.integration.perspective;

import io.github.leawind.thirdperson.internal.core.camera.CameraSubjectDimensions;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

/// Adapts the camera entity and its complete vehicle hierarchy to neutral size inputs.
final class MinecraftCameraSubjectDimensions {
  private static final double BODY_RADIUS_PER_WIDTH = 0.5 * Math.sqrt(3.0);

  private MinecraftCameraSubjectDimensions() {}

  static Optional<CameraSubjectDimensions> resolve(Entity cameraEntity) {
    Objects.requireNonNull(cameraEntity, "cameraEntity");
    Entity rootVehicle = cameraEntity.getRootVehicle();
    AABB vehicleBounds =
        rootVehicle
            .getPassengersAndSelf()
            .map(Entity::getBoundingBox)
            .reduce(AABB::minmax)
            .orElseGet(rootVehicle::getBoundingBox);
    double vehicleTotalSize =
        Math.hypot(
            Math.hypot(vehicleBounds.getXsize(), vehicleBounds.getYsize()),
            vehicleBounds.getZsize());
    double bodyRadius = cameraEntity.getBbWidth() * BODY_RADIUS_PER_WIDTH;
    try {
      return Optional.of(new CameraSubjectDimensions(bodyRadius, vehicleTotalSize));
    } catch (IllegalArgumentException ignored) {
      return Optional.empty();
    }
  }
}
