package io.github.leawind.thirdperson.internal.extension.entity;

import io.github.leawind.thirdperson.internal.core.api.ExtensionPoint;
import io.github.leawind.thirdperson.internal.core.api.OrderedModifierRegistry;
import io.github.leawind.thirdperson.internal.core.api.PriorityResolverRegistry;
import java.util.Objects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

/// Provides purpose-neutral entity reference poses sampled from Minecraft render state.
public final class MinecraftEntityReferencePose {
  private static final ExtensionPoint<
          PriorityResolverRegistry.Resolver<EntityReferencePoseContext, EntityReferencePose>,
          PriorityResolverRegistry<EntityReferencePoseContext, EntityReferencePose>>
      SOURCES = ExtensionPoint.resolver();
  private static final ExtensionPoint<
          OrderedModifierRegistry.Modifier<EntityReferencePoseContext, EntityReferencePose>,
          OrderedModifierRegistry<EntityReferencePoseContext, EntityReferencePose>>
      MODIFIERS = ExtensionPoint.modifier();

  private MinecraftEntityReferencePose() {}

  public static void registerSource(
      String id, int priority, EntityReferencePoseResolver resolver) {
    Objects.requireNonNull(resolver, "resolver");
    SOURCES.register(id, priority, resolver::resolve);
  }

  /// Registers a coordinate-space correction for the real entity reference pose.
  ///
  /// Camera-pivot positioning behavior belongs to the independent pivot-position extension point.
  public static void registerModifier(
      String id,
      int priority,
      OrderedModifierRegistry.Modifier<EntityReferencePoseContext, EntityReferencePose> modifier) {
    MODIFIERS.register(id, priority, modifier);
  }

  public static EntityReferencePose resolve(Entity entity, float partialTick) {
    var context =
        new EntityReferencePoseContext(Objects.requireNonNull(entity, "entity"), partialTick);
    EntityReferencePose base =
        SOURCES
            .get()
            .resolve(context)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "No entity reference pose resolver handled the entity"));
    return MODIFIERS.get().apply(context, base);
  }

  public static Vec3 eyePosition(Entity entity, float partialTick) {
    Vector3d position = resolve(entity, partialTick).copyEyePositionWorld(new Vector3d());
    return new Vec3(position.x, position.y, position.z);
  }
}
