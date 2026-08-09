package io.github.leawind.thirdperson.internal.bridge.entity;

import java.util.Objects;
import net.minecraft.world.entity.Entity;

/// Minecraft context supplied to pivot-pose sources and modifiers.
public record EntityPoseContext(Entity entity, float partialTick) {
  public EntityPoseContext {
    Objects.requireNonNull(entity, "entity");
    if (!Float.isFinite(partialTick)) {
      throw new IllegalArgumentException("Partial tick must be finite");
    }
  }
}
