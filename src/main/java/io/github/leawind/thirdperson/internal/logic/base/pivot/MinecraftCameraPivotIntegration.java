package io.github.leawind.thirdperson.internal.logic.base.pivot;

import io.github.leawind.thirdperson.internal.bridge.camera.pivot.CameraPivotFrameContext;
import io.github.leawind.thirdperson.internal.bridge.camera.pivot.CameraPivotTickContext;
import io.github.leawind.thirdperson.internal.bridge.camera.pivot.MinecraftCameraPivotPosition;
import io.github.leawind.thirdperson.internal.bridge.entity.MinecraftEntityReferencePose;
import io.github.leawind.thirdperson.internal.core.base.pivot.PivotPose;
import io.github.leawind.thirdperson.internal.logic.base.BaseRuntime;
import io.github.leawind.thirdperson.internal.logic.base.PerspectiveGuard;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;

/// Drives the selected pivot-position strategy and combines it with external reference rotation.
public final class MinecraftCameraPivotIntegration {
  private static final double CLIENT_TICK_SECONDS = 0.05;

  private MinecraftCameraPivotIntegration() {}

  public static void onClientTick() {
    Minecraft minecraft = Minecraft.getInstance();
    BaseRuntime runtime = BaseRuntime.getInstance();
    if (!PerspectiveGuard.isThirdPersonCurrent() || !runtime.isCameraControlEnabled()) {
      return;
    }

    Entity entity = minecraft.getCameraEntity();
    if (entity == null) {
      return;
    }
    var reference = MinecraftEntityReferencePose.resolve(entity, 1.0f);
    runtime
        .session()
        .recordPivotRotation(reference.copyWorldFromReference(new Quaternionf()));
    MinecraftCameraPivotPosition.onClientTick(
        new CameraPivotTickContext(
            entity, reference, runtime.cameraPivotSmoothing(), CLIENT_TICK_SECONDS));
  }

  public static Optional<PivotPose> sample(
      Entity entity, float partialTick, double frameDeltaSeconds) {
    BaseRuntime runtime = BaseRuntime.getInstance();
    var reference = MinecraftEntityReferencePose.resolve(entity, partialTick);
    var worldFromPivot = reference.copyWorldFromReference(new Quaternionf());
    if (!runtime.session().recordPivotRotation(worldFromPivot)) {
      return Optional.empty();
    }
    return MinecraftCameraPivotPosition.sample(
            new CameraPivotFrameContext(
                entity,
                reference,
                runtime.cameraPivotSmoothing(),
                partialTick,
                frameDeltaSeconds))
        .flatMap(position -> PivotPose.tryCreate(position, worldFromPivot));
  }

  public static void reset() {
    MinecraftCameraPivotPosition.reset();
  }
}
