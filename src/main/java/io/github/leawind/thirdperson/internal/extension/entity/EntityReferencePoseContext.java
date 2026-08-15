package io.github.leawind.thirdperson.internal.extension.entity;

import java.util.Objects;
import net.minecraft.world.entity.Entity;

/// Minecraft context supplied to entity-reference-pose resolvers and modifiers.
public record EntityReferencePoseContext(Entity entity, float partialTick) {
  public EntityReferencePoseContext {
    Objects.requireNonNull(entity, "entity");
    if (!Float.isFinite(partialTick)) {
      throw new IllegalArgumentException("Partial tick must be finite");
    }
  }
}
