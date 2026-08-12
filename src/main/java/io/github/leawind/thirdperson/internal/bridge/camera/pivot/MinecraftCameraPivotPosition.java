package io.github.leawind.thirdperson.internal.bridge.camera.pivot;

import io.github.leawind.thirdperson.internal.core.api.PriorityProviderRegistry;
import java.util.Objects;
import java.util.Optional;
import org.joml.Vector3d;

/// Purpose-specific internal facade for camera-pivot position strategies.
public final class MinecraftCameraPivotPosition {
  private static final Object REGISTRY_LOCK = new Object();
  private static final PriorityProviderRegistry.Builder<
          CameraPivotTickContext, CameraPivotFrameContext, Vector3d>
      BUILDER = PriorityProviderRegistry.builder();

  private static volatile PriorityProviderRegistry<
          CameraPivotTickContext, CameraPivotFrameContext, Vector3d>
      providers;

  private MinecraftCameraPivotPosition() {}

  public static void registerProvider(
      String id, int priority, CameraPivotPositionProvider provider) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(provider, "provider");
    synchronized (REGISTRY_LOCK) {
      if (providers != null) {
        throw new IllegalStateException("Camera-pivot provider registration is already frozen");
      }
      BUILDER.register(
          id,
          priority,
          new PriorityProviderRegistry.Provider<>() {
            @Override
            public void update(CameraPivotTickContext context) {
              provider.onClientTick(context);
            }

            @Override
            public io.github.leawind.thirdperson.internal.core.api.ExtensionResult<Vector3d>
                resolve(CameraPivotFrameContext context) {
              return provider.sample(context);
            }

            @Override
            public void reset() {
              provider.reset();
            }
          });
    }
  }

  public static void onClientTick(CameraPivotTickContext context) {
    Objects.requireNonNull(context, "context");
    freezeRegistry();
    providers.update(context);
  }

  public static Optional<Vector3d> sample(CameraPivotFrameContext context) {
    Objects.requireNonNull(context, "context");
    freezeRegistry();
    return providers.resolve(context).map(Vector3d::new);
  }

  public static void reset() {
    PriorityProviderRegistry<CameraPivotTickContext, CameraPivotFrameContext, Vector3d> snapshot =
        providers;
    if (snapshot != null) {
      snapshot.reset();
    }
  }

  private static void freezeRegistry() {
    if (providers != null) {
      return;
    }
    synchronized (REGISTRY_LOCK) {
      if (providers == null) {
        providers = BUILDER.freeze();
      }
    }
  }
}
