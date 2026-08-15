package io.github.leawind.thirdperson.internal.extension.sable;

/*? if >=1.21 && <1.21.11 {*/
/*import dev.ryanhcode.sable.companion.SableCompanion;
*//*? }*/
import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import io.github.leawind.thirdperson.internal.extension.camera.CameraSubjectBoundsResolver;
import java.util.Optional;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

/// Supplies Sable-aware bounds for camera-subject measurement.
final class SableCameraSubjectBoundsResolver {
  private SableCameraSubjectBoundsResolver() {}

  static Optional<CameraSubjectBoundsResolver> createIfAvailable() {
    /*? if >=1.21 && <1.21.11 {*/
    /*return SableAvailability.isAvailable() ? Optional.of(Active.INSTANCE) : Optional.empty();
    *//*? } else {*/
    return Optional.empty();
    /*? }*/
  }

  /*? if >=1.21 && <1.21.11 {*/
  /*private enum Active implements CameraSubjectBoundsResolver {
    INSTANCE;

    @Override
    public ExtensionResult<AABB> resolveBounds(Entity entity) {
      org.joml.Vector3d feetPosition = SableCompanion.INSTANCE.getFeetPos(entity, 0.0f);
      SableCompanion.INSTANCE.projectOutOfSubLevel(entity.level(), feetPosition);
      return ExtensionResult.handled(
          entity
              .getBoundingBox()
              .move(
                  feetPosition.x - entity.getX(),
                  feetPosition.y - entity.getY(),
                  feetPosition.z - entity.getZ()));
    }
  }
  *//*? }*/
}
