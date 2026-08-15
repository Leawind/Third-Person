package io.github.leawind.thirdperson.internal.extension.minecraft;

import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import io.github.leawind.thirdperson.internal.extension.camera.CameraSubjectBoundsResolver;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

/// Supplies the standard Minecraft bounds used to measure a camera subject.
final class MinecraftCameraSubjectBoundsResolver implements CameraSubjectBoundsResolver {
  static final MinecraftCameraSubjectBoundsResolver INSTANCE =
      new MinecraftCameraSubjectBoundsResolver();

  private MinecraftCameraSubjectBoundsResolver() {}

  @Override
  public ExtensionResult<AABB> resolveBounds(Entity entity) {
    return ExtensionResult.handled(entity.getBoundingBox());
  }
}
