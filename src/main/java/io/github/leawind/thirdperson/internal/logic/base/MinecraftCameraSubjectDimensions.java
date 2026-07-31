package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.bridge.Bridge;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.world.entity.Entity;

/// Adapts the camera entity and its complete vehicle hierarchy to neutral size inputs.
final class MinecraftCameraSubjectDimensions {
  private MinecraftCameraSubjectDimensions() {}

  static Optional<CameraSubjectDimensions> resolve(Entity cameraEntity) {
    Objects.requireNonNull(cameraEntity, "cameraEntity");
    var measurements = Bridge.measureCameraSubject(cameraEntity);
    try {
      return Optional.of(
          new CameraSubjectDimensions(
              measurements.bodyRadius(), measurements.vehicleTotalSize()));
    } catch (IllegalArgumentException ignored) {
      return Optional.empty();
    }
  }
}
