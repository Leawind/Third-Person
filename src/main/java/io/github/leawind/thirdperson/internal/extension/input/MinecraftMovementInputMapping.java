package io.github.leawind.thirdperson.internal.extension.input;

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
  private static final Object REGISTRY_LOCK = new Object();
  private static final PriorityResolverRegistry.Builder<Context, MovementInput> BUILDER =
      PriorityResolverRegistry.builder();
  private static volatile PriorityResolverRegistry<Context, MovementInput> resolvers;

  private MinecraftMovementInputMapping() {}

  public static void registerMapper(String id, int priority, MovementInputMapper mapper) {
    Objects.requireNonNull(mapper, "mapper");
    synchronized (REGISTRY_LOCK) {
      if (resolvers != null) {
        throw new IllegalStateException("Movement input extension registration is already frozen");
      }
      BUILDER.register(id, priority, context -> mapper.map(context.player(), context.intent()));
    }
  }

  public static MappingResult map(
      LocalPlayer player, MovementIntent intent, MovementInput unmodifiedInput) {
    freezeRegistry();
    return resolvers
        .resolve(new Context(Objects.requireNonNull(player, "player"), intent))
        .map(input -> new MappingResult(input, true))
        .orElseGet(
            () ->
                new MappingResult(
                    Objects.requireNonNull(unmodifiedInput, "unmodifiedInput"), false));
  }

  private static void freezeRegistry() {
    if (resolvers != null) {
      return;
    }
    synchronized (REGISTRY_LOCK) {
      if (resolvers == null) {
        resolvers = BUILDER.freeze();
      }
    }
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
