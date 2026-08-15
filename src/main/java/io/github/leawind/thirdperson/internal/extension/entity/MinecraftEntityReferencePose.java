package io.github.leawind.thirdperson.internal.extension.entity;

import io.github.leawind.thirdperson.internal.core.api.OrderedModifierRegistry;
import io.github.leawind.thirdperson.internal.core.api.PriorityResolverRegistry;
import java.util.Objects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

/// Provides purpose-neutral entity reference poses sampled from Minecraft render state.
public final class MinecraftEntityReferencePose {
  private static final Object REGISTRY_LOCK = new Object();
  private static final PriorityResolverRegistry.Builder<
          EntityReferencePoseContext, EntityReferencePose>
      SOURCE_BUILDER = PriorityResolverRegistry.builder();
  private static final OrderedModifierRegistry.Builder<
          EntityReferencePoseContext, EntityReferencePose>
      MODIFIER_BUILDER = OrderedModifierRegistry.builder();

  private static volatile PriorityResolverRegistry<
          EntityReferencePoseContext, EntityReferencePose>
      sources;
  private static volatile OrderedModifierRegistry<
          EntityReferencePoseContext, EntityReferencePose>
      modifiers;

  private MinecraftEntityReferencePose() {}

  public static void registerSource(
      String id, int priority, EntityReferencePoseResolver resolver) {
    Objects.requireNonNull(resolver, "resolver");
    synchronized (REGISTRY_LOCK) {
      requireRegistrationOpen();
      SOURCE_BUILDER.register(id, priority, resolver::resolve);
    }
  }

  /// Registers a coordinate-space correction for the real entity reference pose.
  ///
  /// Camera-pivot positioning behavior belongs to the independent pivot-position extension point.
  public static void registerModifier(
      String id,
      int priority,
      OrderedModifierRegistry.Modifier<EntityReferencePoseContext, EntityReferencePose> modifier) {
    synchronized (REGISTRY_LOCK) {
      requireRegistrationOpen();
      MODIFIER_BUILDER.register(id, priority, modifier);
    }
  }

  public static EntityReferencePose resolve(Entity entity, float partialTick) {
    freezeRegistries();
    var context =
        new EntityReferencePoseContext(Objects.requireNonNull(entity, "entity"), partialTick);
    EntityReferencePose base =
        sources
            .resolve(context)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "No entity reference pose resolver handled the entity"));
    return modifiers.apply(context, base);
  }

  public static Vec3 eyePosition(Entity entity, float partialTick) {
    Vector3d position = resolve(entity, partialTick).copyEyePositionWorld(new Vector3d());
    return new Vec3(position.x, position.y, position.z);
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
      throw new IllegalStateException("Entity reference pose registration is already frozen");
    }
  }

}
