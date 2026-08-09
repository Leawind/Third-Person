package io.github.leawind.thirdperson.internal.bridge.compat.sable;

/*? if >=1.21 && <1.21.11 {*/
/*import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
*//*? }*/
import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import io.github.leawind.thirdperson.internal.bridge.events.LocalPlayerMovementInputEvent.MovementInput;
import io.github.leawind.thirdperson.internal.bridge.input.MinecraftMovementInputMapping;
import io.github.leawind.thirdperson.internal.bridge.input.MovementInputMapper;
import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import io.github.leawind.thirdperson.internal.core.base.rotation.MovementIntent;
import java.util.Optional;
import net.minecraft.client.player.LocalPlayer;
import org.joml.Quaternionf;

/// Supplies a movement basis that follows the current Sable render pose.
public final class SableMovementInputMapper {
  private SableMovementInputMapper() {}

  public static Optional<MovementInputMapper> createIfAvailable() {
    /*? if >=1.21 && <1.21.11 {*/
    /*return SableAvailability.isAvailable() ? Optional.of(Active.INSTANCE) : Optional.empty();
    *//*? } else {*/
    return Optional.empty();
    /*? }*/
  }

  /*? if >=1.21 && <1.21.11 {*/
  /*private enum Active implements MovementInputMapper {
    INSTANCE;

    @Override
    public ExtensionResult<MovementInput> map(LocalPlayer player, MovementIntent intent) {
      ClientSubLevelAccess subLevel = SableCompanion.INSTANCE.getContainingClient(player);
      if (subLevel == null) {
        return ExtensionResult.pass();
      }
      var pivotFromHeading =
          PerspectiveMath.eulerDegToQuat(0.0f, player.getYRot(), 0.0f, new Quaternionf());
      var worldFromInput =
          new Quaternionf(subLevel.renderPose(1.0f).orientation())
              .normalize()
              .mul(pivotFromHeading);
      return MinecraftMovementInputMapping.mapToBasis(intent, worldFromInput);
    }
  }
  *//*? }*/
}
