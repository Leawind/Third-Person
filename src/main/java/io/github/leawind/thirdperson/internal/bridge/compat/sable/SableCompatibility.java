package io.github.leawind.thirdperson.internal.bridge.compat.sable;

/*? if >=1.21 && <1.21.11 {*/
/*import dev.ryanhcode.sable.companion.SableCompanion;
*//*? }*/
import io.github.leawind.perspectiveapi.internal.utils.Utils;
import io.github.leawind.thirdperson.platform.api.Services;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/// Optional coordinate conversion supplied by Sable Companion.
public final class SableCompatibility {
  private static boolean loaded() {
    return Services.PLATFORM_HELPER.isModLoaded("sablecompanion");
  }

  static {
    Utils.DEBUG_LOGGER.info("sablecompanion loaded: {}", loaded());
  }

  private SableCompatibility() {}

  /// Converts a position returned from a Sable sublevel into the containing world's coordinates.
  ///
  /// When Sable Companion is unavailable, the original position is returned unchanged.
  public static Vec3 projectToWorld(Level level, Vec3 position) {
    if (!loaded()) {
      return position;
    }
    return Active.projectToWorld(level, position);
  }

  public static Vec3 getEyePositionInterpolated(Entity entity, float partialTick) {
    if (!loaded()) {
      return entity.getEyePosition(partialTick);
    }
    return Active.getEyePositionInterpolated(entity, partialTick);
  }

  /// Returns the entity bounds translated to the world-space feet position used by Sable.
  ///
  /// Sable can rotate an entity with its supporting sublevel or store an entity in a sublevel's
  /// remote plot without changing the vanilla bounding box anchor. Preserve the vanilla box
  /// dimensions while moving that anchor to the entity's effective world-space feet position.
  /// Without Sable Companion, the vanilla bounds are returned.
  public static AABB getWorldBoundingBox(Entity entity) {
    if (!loaded()) {
      return entity.getBoundingBox();
    }
    return Active.getWorldBoundingBox(entity);
  }

  // Keep optional API references out of the outer class's constant pool until the loader confirms
  // that Sable Companion is present.
  private static final class Active {
    private Active() {}

    private static Vec3 projectToWorld(Level level, Vec3 position) {
      /*? if >=1.21 && <1.21.11 {*/
      /*return SableCompanion.INSTANCE.projectOutOfSubLevel(
          level, (net.minecraft.core.Position) position);
      *//*? } else {*/
      return position;
      /*? }*/
    }

    private static Vec3 getEyePositionInterpolated(Entity entity, float partialTick) {
      /*? if >=1.21 && <1.21.11 {*/
      /*return SableCompanion.INSTANCE.getEyePositionInterpolated(entity, partialTick);
      *//*? } else {*/
      return entity.getEyePosition(partialTick);
      /*? }*/
    }

    private static AABB getWorldBoundingBox(Entity entity) {
      /*? if >=1.21 && <1.21.11 {*/
      /*org.joml.Vector3d feetPosition = SableCompanion.INSTANCE.getFeetPos(entity, 0.0f);
      SableCompanion.INSTANCE.projectOutOfSubLevel(entity.level(), feetPosition);
      return entity
          .getBoundingBox()
          .move(
              feetPosition.x - entity.getX(),
              feetPosition.y - entity.getY(),
              feetPosition.z - entity.getZ());
      *//*? } else {*/
      return entity.getBoundingBox();
      /*? }*/
    }
  }
}
