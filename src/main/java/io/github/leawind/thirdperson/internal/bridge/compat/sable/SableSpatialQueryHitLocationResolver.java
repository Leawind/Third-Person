package io.github.leawind.thirdperson.internal.bridge.compat.sable;

/*? if >=1.21 && <1.21.11 {*/
/*import dev.ryanhcode.sable.companion.SableCompanion;
*//*? }*/
import io.github.leawind.thirdperson.internal.bridge.spatial.SpatialQueryHitLocationResolver;
import java.util.Optional;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/// Supplies Sable-aware interpretation of raw spatial-query hit locations.
public final class SableSpatialQueryHitLocationResolver {
  private SableSpatialQueryHitLocationResolver() {}

  public static Optional<SpatialQueryHitLocationResolver> createIfAvailable() {
    /*? if >=1.21 && <1.21.11 {*/
    /*return SableAvailability.isAvailable() ? Optional.of(Active.INSTANCE) : Optional.empty();
    *//*? } else {*/
    return Optional.empty();
    /*? }*/
  }

  /*? if >=1.21 && <1.21.11 {*/
  /*private enum Active implements SpatialQueryHitLocationResolver {
    INSTANCE;

    @Override
    public Vec3 resolveWorldLocation(Level level, HitResult hit) {
      if (hit.getType() == HitResult.Type.MISS) {
        return hit.getLocation();
      }
      return SableCompanion.INSTANCE.projectOutOfSubLevel(
          level, (net.minecraft.core.Position) hit.getLocation());
    }
  }
  *//*? }*/
}
