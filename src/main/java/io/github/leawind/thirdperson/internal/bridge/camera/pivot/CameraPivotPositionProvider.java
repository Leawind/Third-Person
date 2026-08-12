package io.github.leawind.thirdperson.internal.bridge.camera.pivot;

import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import org.joml.Vector3d;

/// Stateful internal strategy that supplies the final world-space camera-pivot position.
public interface CameraPivotPositionProvider {
  /// Updates any tick-driven state. Every registered provider receives this callback.
  default void onClientTick(CameraPivotTickContext context) {}

  /// Returns the position to use for this frame, or passes to the next lower-priority provider.
  ///
  /// Implementations may also advance frame-driven state here.
  ExtensionResult<Vector3d> sample(CameraPivotFrameContext context);

  /// Clears all state tied to the previous perspective, dimension, camera entity, or provider use.
  default void reset() {}
}
