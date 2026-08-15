package io.github.leawind.thirdperson.internal.extension.minecraft;

import io.github.leawind.thirdperson.internal.bridge.camera.MinecraftCameraSubjectMeasurements;
import io.github.leawind.thirdperson.internal.bridge.camera.pivot.MinecraftCameraPivotPosition;
import io.github.leawind.thirdperson.internal.bridge.entity.MinecraftEntityReferencePose;
import io.github.leawind.thirdperson.internal.bridge.input.MinecraftMovementInputMapping;
import io.github.leawind.thirdperson.internal.bridge.spatial.SpatialQueryHitLocation;

/// Installs the baseline extensions that connect the mod's features to Minecraft.
public final class MinecraftExtensions {
  private static boolean registered;

  private MinecraftExtensions() {}

  public static synchronized void register() {
    if (registered) {
      return;
    }
    MinecraftCameraPivotPosition.registerProvider(
        "minecraft:eye_following", 0, new EyeFollowingCameraPivotProvider());
    MinecraftEntityReferencePose.registerSource(
        "minecraft:entity_reference_pose", 0, MinecraftEntityReferencePoseResolver.INSTANCE);
    MinecraftCameraSubjectMeasurements.registerResolver(
        "minecraft:camera_subject_bounds", 0, MinecraftCameraSubjectBoundsResolver.INSTANCE);
    MinecraftMovementInputMapping.registerMapper(
        "minecraft:movement_input", 0, MinecraftMovementInputMapper.INSTANCE);
    SpatialQueryHitLocation.registerResolver(
        "minecraft:spatial_query_hit", 0, MinecraftSpatialQueryHitLocationResolver.INSTANCE);
    registered = true;
  }
}
