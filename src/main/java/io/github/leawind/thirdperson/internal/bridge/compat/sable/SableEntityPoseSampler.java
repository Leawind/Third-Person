package io.github.leawind.thirdperson.internal.bridge.compat.sable;

/*? if >=1.21 && <1.21.11 {*/
/*import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
*//*? }*/
import io.github.leawind.thirdperson.internal.bridge.entity.EntityPoseSampler;
import io.github.leawind.thirdperson.internal.bridge.entity.EntityPoseContext;
import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import io.github.leawind.thirdperson.internal.core.base.pivot.PivotPose;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;

/// Supplies Sable-aware entity render-pose sampling.
public final class SableEntityPoseSampler {
  private SableEntityPoseSampler() {}

  public static Optional<EntityPoseSampler> createIfAvailable() {
    /*? if >=1.21 && <1.21.11 {*/
    /*return SableAvailability.isAvailable() ? Optional.of(Active.INSTANCE) : Optional.empty();
    *//*? } else {*/
    return Optional.empty();
    /*? }*/
  }

  /*? if >=1.21 && <1.21.11 {*/
  /*private enum Active implements EntityPoseSampler {
    INSTANCE;

    @Override
    public ExtensionResult<PivotPose> sample(EntityPoseContext context) {
      Entity entity = context.entity();
      float partialTick = context.partialTick();
      ClientSubLevelAccess subLevel = SableCompanion.INSTANCE.getContainingClient(entity);
      if (subLevel == null) {
        return ExtensionResult.pass();
      }
      Vec3 eye = SableCompanion.INSTANCE.getEyePositionInterpolated(entity, partialTick);
      var rotation = new Quaternionf(subLevel.renderPose(partialTick).orientation());
      return PivotPose.tryCreate(new Vector3d(eye.x, eye.y, eye.z), rotation)
          .map(ExtensionResult::handled)
          .orElseGet(ExtensionResult::pass);
    }
  }
  *//*? }*/
}
