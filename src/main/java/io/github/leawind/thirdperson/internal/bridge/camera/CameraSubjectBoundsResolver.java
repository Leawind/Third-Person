package io.github.leawind.thirdperson.internal.bridge.camera;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

/// Resolves the entity bounds used specifically for camera-subject measurement.
public interface CameraSubjectBoundsResolver {
  AABB resolveBounds(Entity entity);
}
