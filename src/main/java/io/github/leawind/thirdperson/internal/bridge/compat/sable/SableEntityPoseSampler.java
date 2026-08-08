package io.github.leawind.thirdperson.internal.bridge.compat.sable;

/*? if >=1.21 && <1.21.11 {*/
/*import dev.ryanhcode.sable.companion.SableCompanion;
*//*? }*/
import io.github.leawind.thirdperson.internal.bridge.entity.EntityPoseSampler;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

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
    public Vec3 sampleEyePosition(Entity entity, float partialTick) {
      return SableCompanion.INSTANCE.getEyePositionInterpolated(entity, partialTick);
    }
  }
  *//*? }*/
}
