package io.github.leawind.thirdperson.internal.bridge.compat.sable;

/*? if >=1.21 && <1.21.11 {*/
/*import dev.ryanhcode.sable.companion.SableCompanion;
*//*? }*/
import io.github.leawind.thirdperson.platform.api.Services;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
/*? if >=1.21 && <1.21.11 {*/
/*import org.joml.Vector3d;
*//*? }*/

/// Optional coordinate conversion supplied by Sable Companion.
public final class SableCompatibility {
  private static final boolean COMPANION_LOADED =
      Services.PLATFORM_HELPER.isModLoaded("sablecompanion");

  private SableCompatibility() {}

  /// Converts a position returned from a Sable sublevel into the containing world's coordinates.
  ///
  /// When Sable Companion is unavailable, the original position is returned unchanged.
  public static Vec3 projectToWorld(Level level, Vec3 position) {
    if (!COMPANION_LOADED) {
      return position;
    }
    /*? if >=1.21 && <1.21.11 {*/
    /*return Active.projectToWorld(level, position);
    *//*? } else {*/
    return position;
    /*? }*/
  }

  // Keep optional API references out of the outer class's constant pool until the loader confirms
  // that Sable Companion is present.
  /*? if >=1.21 && <1.21.11 {*/
  /*private static final class Active {
    private Active() {}

    private static Vec3 projectToWorld(Level level, Vec3 position) {
      Vector3d projected =
          SableCompanion.INSTANCE.projectOutOfSubLevel(
              level, new Vector3d(position.x, position.y, position.z), new Vector3d());
      return new Vec3(projected.x, projected.y, projected.z);
    }
  }
  *//*? }*/
}
