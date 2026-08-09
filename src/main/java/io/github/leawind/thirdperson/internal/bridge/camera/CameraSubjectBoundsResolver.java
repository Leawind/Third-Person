package io.github.leawind.thirdperson.internal.bridge.camera;

import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

/// Resolves the entity bounds used specifically for camera-subject measurement.
public interface CameraSubjectBoundsResolver {
  ExtensionResult<AABB> resolveBounds(Entity entity);
}
