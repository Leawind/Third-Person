package io.github.leawind.thirdperson.internal.bridge.entity;

import io.github.leawind.thirdperson.internal.bridge.compat.sable.SableEntityPoseSampler;
import java.util.Objects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/// Provides positions sampled from Minecraft entity render poses.
public final class MinecraftEntityPose {
  private MinecraftEntityPose() {}

  public static Vec3 eyePosition(Entity entity, float partialTick) {
    return Holder.SAMPLER.sampleEyePosition(
        Objects.requireNonNull(entity, "entity"), partialTick);
  }

  private static final class Holder {
    private static final EntityPoseSampler SAMPLER =
        SableEntityPoseSampler.createIfAvailable().orElse(VanillaSampler.INSTANCE);
  }

  private enum VanillaSampler implements EntityPoseSampler {
    INSTANCE;

    @Override
    public Vec3 sampleEyePosition(Entity entity, float partialTick) {
      return entity.getEyePosition(partialTick);
    }
  }
}
