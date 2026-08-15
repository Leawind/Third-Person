package io.github.leawind.thirdperson.internal.extension.minecraft;

import io.github.leawind.thirdperson.internal.bridge.entity.EntityReferencePose;
import io.github.leawind.thirdperson.internal.bridge.entity.EntityReferencePoseContext;
import io.github.leawind.thirdperson.internal.bridge.entity.EntityReferencePoseResolver;
import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

/// Supplies the standard Minecraft entity reference pose.
final class MinecraftEntityReferencePoseResolver implements EntityReferencePoseResolver {
  static final MinecraftEntityReferencePoseResolver INSTANCE =
      new MinecraftEntityReferencePoseResolver();

  private MinecraftEntityReferencePoseResolver() {}

  @Override
  public ExtensionResult<EntityReferencePose> resolve(EntityReferencePoseContext context) {
    Vec3 eye = context.entity().getEyePosition(context.partialTick());
    return ExtensionResult.handled(
        EntityReferencePose.identity(new Vector3d(eye.x, eye.y, eye.z)));
  }
}
