package io.github.leawind.thirdperson.internal.bridge.camera.pivot;

import io.github.leawind.thirdperson.internal.bridge.entity.EntityReferencePose;
import io.github.leawind.thirdperson.internal.core.base.pivot.CameraPivotSmoothing;
import java.util.Objects;
import net.minecraft.world.entity.Entity;

/// Client-tick input supplied to every internal camera-pivot position provider.
public record CameraPivotTickContext(
    Entity cameraEntity,
    EntityReferencePose referencePose,
    CameraPivotSmoothing smoothing,
    double deltaSeconds) {
  public CameraPivotTickContext {
    Objects.requireNonNull(cameraEntity, "cameraEntity");
    Objects.requireNonNull(referencePose, "referencePose");
    Objects.requireNonNull(smoothing, "smoothing");
    if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0.0) {
      throw new IllegalArgumentException("Pivot tick delta must be finite and non-negative");
    }
  }
}
