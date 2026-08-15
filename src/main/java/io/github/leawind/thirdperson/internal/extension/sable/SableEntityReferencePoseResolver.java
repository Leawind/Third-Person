package io.github.leawind.thirdperson.internal.extension.sable;

/*? if >=1.21 && <1.21.11 {*/
/*import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
*//*? }*/
import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import io.github.leawind.thirdperson.internal.extension.entity.EntityReferencePose;
import io.github.leawind.thirdperson.internal.extension.entity.EntityReferencePoseContext;
import io.github.leawind.thirdperson.internal.extension.entity.EntityReferencePoseResolver;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;

/// Supplies Sable-aware entity reference-pose sampling.
final class SableEntityReferencePoseResolver {
  private SableEntityReferencePoseResolver() {}

  static Optional<EntityReferencePoseResolver> createIfAvailable() {
    /*? if >=1.21 && <1.21.11 {*/
    /*return SableAvailability.isAvailable() ? Optional.of(Active.INSTANCE) : Optional.empty();
    *//*? } else {*/
    return Optional.empty();
    /*? }*/
  }

  /*? if >=1.21 && <1.21.11 {*/
  /*private enum Active implements EntityReferencePoseResolver {
    INSTANCE;

    @Override
    public ExtensionResult<EntityReferencePose> resolve(EntityReferencePoseContext context) {
      Entity entity = context.entity();
      float partialTick = context.partialTick();
      ClientSubLevelAccess subLevel = SableCompanion.INSTANCE.getContainingClient(entity);
      if (subLevel == null) {
        return ExtensionResult.pass();
      }
      Vec3 eye = SableCompanion.INSTANCE.getEyePositionInterpolated(entity, partialTick);
      var rotation = new Quaternionf(subLevel.renderPose(partialTick).orientation());
      return EntityReferencePose.tryCreate(new Vector3d(eye.x, eye.y, eye.z), rotation)
          .map(ExtensionResult::handled)
          .orElseGet(ExtensionResult::pass);
    }
  }
  *//*? }*/
}
