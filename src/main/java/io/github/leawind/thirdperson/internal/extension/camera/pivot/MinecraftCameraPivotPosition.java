package io.github.leawind.thirdperson.internal.extension.camera.pivot;

import io.github.leawind.thirdperson.internal.core.api.ExtensionPoint;
import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import io.github.leawind.thirdperson.internal.core.api.PriorityProviderRegistry;
import java.util.Objects;
import java.util.Optional;
import org.joml.Vector3d;

/// Purpose-specific internal facade for camera-pivot position strategies.
public final class MinecraftCameraPivotPosition {
  private static final ExtensionPoint<
          PriorityProviderRegistry.Provider<CameraPivotTickContext, CameraPivotFrameContext, Vector3d>,
          PriorityProviderRegistry<CameraPivotTickContext, CameraPivotFrameContext, Vector3d>>
      EXTENSION_POINT = ExtensionPoint.provider();

  private MinecraftCameraPivotPosition() {}

  public static void registerProvider(
      String id, int priority, CameraPivotPositionProvider provider) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(provider, "provider");
    EXTENSION_POINT.register(
        id,
        priority,
        new PriorityProviderRegistry.Provider<>() {
          @Override
          public void update(CameraPivotTickContext context) {
            provider.onClientTick(context);
          }

          @Override
          public ExtensionResult<Vector3d> resolve(CameraPivotFrameContext context) {
            return provider.sample(context);
          }

          @Override
          public void reset() {
            provider.reset();
          }
        });
  }

  public static void onClientTick(CameraPivotTickContext context) {
    Objects.requireNonNull(context, "context");
    EXTENSION_POINT.get().update(context);
  }

  public static Optional<Vector3d> sample(CameraPivotFrameContext context) {
    Objects.requireNonNull(context, "context");
    return EXTENSION_POINT.get().resolve(context).map(Vector3d::new);
  }

  public static void reset() {
    PriorityProviderRegistry<CameraPivotTickContext, CameraPivotFrameContext, Vector3d> snapshot =
        EXTENSION_POINT.getIfFrozen();
    if (snapshot != null) {
      snapshot.reset();
    }
  }
}
