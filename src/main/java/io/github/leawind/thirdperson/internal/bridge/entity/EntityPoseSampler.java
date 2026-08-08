package io.github.leawind.thirdperson.internal.bridge.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/// Samples positions from an entity's current rendered pose.
public interface EntityPoseSampler {
  Vec3 sampleEyePosition(Entity entity, float partialTick);
}
