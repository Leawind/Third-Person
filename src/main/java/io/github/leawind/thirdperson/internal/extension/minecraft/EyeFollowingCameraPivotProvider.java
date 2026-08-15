package io.github.leawind.thirdperson.internal.extension.minecraft;

import io.github.leawind.thirdperson.internal.bridge.camera.pivot.CameraPivotFrameContext;
import io.github.leawind.thirdperson.internal.bridge.camera.pivot.CameraPivotPositionProvider;
import io.github.leawind.thirdperson.internal.bridge.camera.pivot.CameraPivotTickContext;
import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import io.github.leawind.thirdperson.internal.core.base.pivot.TickInterpolatedPivotPosition;
import org.joml.Vector3d;

/// Default pivot strategy: smooth the entity eye position on ticks and interpolate tick endpoints.
final class EyeFollowingCameraPivotProvider implements CameraPivotPositionProvider {
  private final TickInterpolatedPivotPosition position = new TickInterpolatedPivotPosition();

  @Override
  public void onClientTick(CameraPivotTickContext context) {
    Vector3d eye = context.referencePose().copyEyePositionWorld(new Vector3d());
    position.updateTick(eye, context.deltaSeconds(), context.smoothing());
  }

  @Override
  public ExtensionResult<Vector3d> sample(CameraPivotFrameContext context) {
    Vector3d eye = context.referencePose().copyEyePositionWorld(new Vector3d());
    return position
        .sample(eye, context.partialTick(), context.smoothing())
        .map(ExtensionResult::handled)
        .orElseGet(ExtensionResult::pass);
  }

  @Override
  public void reset() {
    position.reset();
  }
}
