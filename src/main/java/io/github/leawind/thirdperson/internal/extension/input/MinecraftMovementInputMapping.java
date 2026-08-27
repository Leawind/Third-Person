package io.github.leawind.thirdperson.internal.extension.input;

import io.github.leawind.thirdperson.internal.core.api.ExtensionPoint;
import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import io.github.leawind.thirdperson.internal.core.api.PriorityResolverRegistry;
import io.github.leawind.thirdperson.internal.core.base.rotation.MovementInputProjector;
import io.github.leawind.thirdperson.internal.core.base.rotation.MovementIntent;
import java.util.Objects;
import net.minecraft.client.player.LocalPlayer;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/// Purpose-specific facade for movement-input compatibility.
public final class MinecraftMovementInputMapping {
  private static final ExtensionPoint<
          PriorityResolverRegistry.Resolver<Context, MovementInput>,
          PriorityResolverRegistry<Context, MovementInput>>
      EXTENSION_POINT = ExtensionPoint.resolver();

  private MinecraftMovementInputMapping() {}

  public static void registerMapper(String id, int priority, MovementInputMapper mapper) {
    Objects.requireNonNull(mapper, "mapper");
    EXTENSION_POINT.register(id, priority, context -> mapper.map(context.player(), context.intent()));
  }

  public static MappingResult map(
      LocalPlayer player, MovementIntent intent, MovementInput unmodifiedInput) {
    return EXTENSION_POINT
        .get()
        .resolve(new Context(Objects.requireNonNull(player, "player"), intent))
        .map(input -> new MappingResult(input, true))
        .orElseGet(
            () ->
                new MappingResult(
                    Objects.requireNonNull(unmodifiedInput, "unmodifiedInput"), false));
  }

  public static ExtensionResult<MovementInput> mapToBasis(
      MovementIntent intent, Quaternionf worldFromInput) {
    var desired = intent.copyPivotPlaneDirectionWorld(new Vector3f());
    return MovementInputProjector.project(desired, worldFromInput)
        .map(input -> new MovementInput(input.leftImpulse(), input.forwardImpulse()))
        .map(ExtensionResult::handled)
        .orElseGet(ExtensionResult::pass);
  }

  private record Context(LocalPlayer player, MovementIntent intent) {
    private Context {
      Objects.requireNonNull(intent, "intent");
    }
  }

  public record MappingResult(MovementInput input, boolean handled) {
    public MappingResult {
      Objects.requireNonNull(input, "input");
    }
  }
}
