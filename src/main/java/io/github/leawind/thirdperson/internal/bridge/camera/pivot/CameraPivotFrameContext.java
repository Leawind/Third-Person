package io.github.leawind.thirdperson.internal.bridge.camera.pivot;

import io.github.leawind.thirdperson.internal.bridge.entity.EntityReferencePose;
import io.github.leawind.thirdperson.internal.core.base.pivot.CameraPivotSmoothing;
import java.util.Objects;
import net.minecraft.world.entity.Entity;

/// Render-frame input resolved by camera-pivot position providers in priority order.
///
/// A provider may directly call other internal bridge facades for environment or collision data.
/// The reference pose is resolved before dispatch so every provider sees one coherent entity sample
/// for this frame.
public record CameraPivotFrameContext(
    Entity cameraEntity,
    EntityReferencePose referencePose,
    CameraPivotSmoothing smoothing,
    float partialTick,
    double deltaSeconds) {
  public CameraPivotFrameContext {
    Objects.requireNonNull(cameraEntity, "cameraEntity");
    Objects.requireNonNull(referencePose, "referencePose");
    Objects.requireNonNull(smoothing, "smoothing");
    if (!Float.isFinite(partialTick)
        || !Double.isFinite(deltaSeconds)
        || deltaSeconds < 0.0) {
      throw new IllegalArgumentException("Pivot frame timing must be finite and non-negative");
    }
  }
}
