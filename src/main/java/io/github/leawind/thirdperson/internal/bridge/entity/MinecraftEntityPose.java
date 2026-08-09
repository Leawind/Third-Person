package io.github.leawind.thirdperson.internal.bridge.entity;

import io.github.leawind.thirdperson.internal.bridge.compat.sable.SableEntityPoseSampler;
import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import io.github.leawind.thirdperson.internal.core.api.OrderedModifierRegistry;
import io.github.leawind.thirdperson.internal.core.api.PriorityResolverRegistry;
import io.github.leawind.thirdperson.internal.core.base.pivot.PivotPose;
import java.util.Objects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

/// Provides positions sampled from Minecraft entity render poses.
public final class MinecraftEntityPose {
  private static final Object REGISTRY_LOCK = new Object();
  private static final PriorityResolverRegistry.Builder<EntityPoseContext, PivotPose>
      SOURCE_BUILDER = createSourceBuilder();
  private static final OrderedModifierRegistry.Builder<EntityPoseContext, PivotPose>
      MODIFIER_BUILDER = OrderedModifierRegistry.builder();

  private static volatile PriorityResolverRegistry<EntityPoseContext, PivotPose> sources;
  private static volatile OrderedModifierRegistry<EntityPoseContext, PivotPose> modifiers;

  private MinecraftEntityPose() {}

  public static void registerSource(String id, int priority, EntityPoseSampler sampler) {
    Objects.requireNonNull(sampler, "sampler");
    synchronized (REGISTRY_LOCK) {
      requireRegistrationOpen();
      SOURCE_BUILDER.register(id, priority, sampler::sample);
    }
  }

  public static void registerModifier(
      String id,
      int priority,
      OrderedModifierRegistry.Modifier<EntityPoseContext, PivotPose> modifier) {
    synchronized (REGISTRY_LOCK) {
      requireRegistrationOpen();
      MODIFIER_BUILDER.register(id, priority, modifier);
    }
  }

  public static PivotPose pivotPose(Entity entity, float partialTick) {
    freezeRegistries();
    var context = new EntityPoseContext(Objects.requireNonNull(entity, "entity"), partialTick);
    PivotPose base =
        sources
            .resolve(context)
            .orElseThrow(
                () -> new IllegalStateException("No entity pose resolver handled the entity"));
    return modifiers.apply(context, base);
  }

  public static Vec3 eyePosition(Entity entity, float partialTick) {
    Vector3d position = pivotPose(entity, partialTick).copyPositionWorld(new Vector3d());
    return new Vec3(position.x, position.y, position.z);
  }

  private static PriorityResolverRegistry.Builder<EntityPoseContext, PivotPose>
      createSourceBuilder() {
    var builder = PriorityResolverRegistry.<EntityPoseContext, PivotPose>builder();
    SableEntityPoseSampler.createIfAvailable()
        .ifPresent(sampler -> builder.register("sable", 100, sampler::sample));
    builder.register("vanilla", 0, VanillaSampler.INSTANCE::sample);
    return builder;
  }

  private static void freezeRegistries() {
    if (sources != null) {
      return;
    }
    synchronized (REGISTRY_LOCK) {
      if (sources == null) {
        sources = SOURCE_BUILDER.freeze();
        modifiers = MODIFIER_BUILDER.freeze();
      }
    }
  }

  private static void requireRegistrationOpen() {
    if (sources != null) {
      throw new IllegalStateException("Entity pose extension registration is already frozen");
    }
  }

  private enum VanillaSampler implements EntityPoseSampler {
    INSTANCE;

    @Override
    public ExtensionResult<PivotPose> sample(EntityPoseContext context) {
      Vec3 eye = context.entity().getEyePosition(context.partialTick());
      return ExtensionResult.handled(PivotPose.identity(new Vector3d(eye.x, eye.y, eye.z)));
    }
  }
}
