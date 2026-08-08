package io.github.leawind.thirdperson.internal.logic.base.pivot;

import io.github.leawind.thirdperson.internal.bridge.entity.MinecraftEntityPose;
import io.github.leawind.thirdperson.internal.logic.base.BaseRuntime;
import io.github.leawind.thirdperson.internal.logic.base.PerspectiveGuard;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3d;

/// Adapts the active camera entity's eye position to the independent pivot tracker.
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
    runtime
        .session()
        .cameraPivotTracker()
        .updateTick(
            eyePosition(entity, 1.0f),
            CLIENT_TICK_SECONDS,
            runtime.cameraPivotSmoothing());
  }

  public static Optional<Vector3d> sample(Entity entity, float partialTick) {
    BaseRuntime runtime = BaseRuntime.getInstance();
    return runtime
        .session()
        .cameraPivotTracker()
        .sample(
            eyePosition(entity, partialTick),
            partialTick,
            runtime.cameraPivotSmoothing());
  }

  private static Vector3d eyePosition(Entity entity, float partialTick) {
    var eyePosition = MinecraftEntityPose.eyePosition(entity, partialTick);
    return new Vector3d(eyePosition.x, eyePosition.y, eyePosition.z);
  }
}
