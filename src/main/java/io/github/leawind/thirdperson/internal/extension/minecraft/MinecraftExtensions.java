package io.github.leawind.thirdperson.internal.extension.minecraft;

import io.github.leawind.perspectiveapi.api.PerspectiveMath;
import io.github.leawind.thirdperson.internal.core.api.ExtensionResult;
import io.github.leawind.thirdperson.internal.extension.camera.MinecraftCameraSubjectMeasurements;
import io.github.leawind.thirdperson.internal.extension.camera.pivot.MinecraftCameraPivotPosition;
import io.github.leawind.thirdperson.internal.extension.entity.EntityReferencePose;
import io.github.leawind.thirdperson.internal.extension.entity.MinecraftEntityReferencePose;
import io.github.leawind.thirdperson.internal.extension.input.MinecraftMovementInputMapping;
import io.github.leawind.thirdperson.internal.extension.spatial.SpatialQueryHitLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;

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
        "minecraft:entity_reference_pose",
        0,
        context -> {
          Vec3 eye = context.entity().getEyePosition(context.partialTick());
          return ExtensionResult.handled(
              EntityReferencePose.identity(new Vector3d(eye.x, eye.y, eye.z)));
        });
    MinecraftCameraSubjectMeasurements.registerResolver(
        "minecraft:camera_subject_bounds", 0, entity -> ExtensionResult.handled(entity.getBoundingBox()));
    MinecraftMovementInputMapping.registerMapper(
        "minecraft:movement_input",
        0,
        (player, intent) -> {
          var worldFromInput =
              PerspectiveMath.eulerDegToQuat(0.0f, player.getYRot(), 0.0f, new Quaternionf());
          return MinecraftMovementInputMapping.mapToBasis(intent, worldFromInput);
        });
    SpatialQueryHitLocation.registerResolver(
        "minecraft:spatial_query_hit", 0, (level, hit) -> ExtensionResult.handled(hit.getLocation()));
    registered = true;
  }
}
