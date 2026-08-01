package io.github.leawind.thirdperson.internal.logic.base;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

/// Applies the active perspective's sound-source policy to Minecraft positions.
public final class MinecraftSoundIntegration {
  private MinecraftSoundIntegration() {}

  public static Vec3 adjustCameraEntitySoundPosition(Entity source, Vec3 vanillaPosition) {
    BaseRuntime runtime = BaseRuntime.getInstance();
    Minecraft minecraft = Minecraft.getInstance();
    if (source != minecraft.getCameraEntity()
        || !runtime.isCameraControlEnabled()
        || !runtime.parameters().centerCameraEntitySounds()) {
      return vanillaPosition;
    }

    return runtime
        .session()
        .finalCameraPose()
        .flatMap(
            pose ->
                SoundSourceGeometry.projectToViewCenter(
                    new Vector3d(vanillaPosition.x, vanillaPosition.y, vanillaPosition.z), pose))
        .map(position -> new Vec3(position.x, position.y, position.z))
        .orElse(vanillaPosition);
  }
}
