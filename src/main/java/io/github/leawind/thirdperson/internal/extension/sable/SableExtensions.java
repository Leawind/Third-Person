package io.github.leawind.thirdperson.internal.extension.sable;

import io.github.leawind.thirdperson.internal.extension.camera.MinecraftCameraSubjectMeasurements;
import io.github.leawind.thirdperson.internal.extension.entity.MinecraftEntityReferencePose;
import io.github.leawind.thirdperson.internal.extension.input.MinecraftMovementInputMapping;
import io.github.leawind.thirdperson.internal.extension.spatial.SpatialQueryHitLocation;

/// Installs the extensions that override Minecraft behavior inside Sable sub-levels.
public final class SableExtensions {
  private static final int PRIORITY = 100;
  private static boolean registered;

  private SableExtensions() {}

  public static synchronized void register() {
    if (registered) {
      return;
    }
    SableEntityReferencePoseResolver.createIfAvailable()
        .ifPresent(
            resolver ->
                MinecraftEntityReferencePose.registerSource(
                    "sable:entity_reference_pose", PRIORITY, resolver));
    SableCameraSubjectBoundsResolver.createIfAvailable()
        .ifPresent(
            resolver ->
                MinecraftCameraSubjectMeasurements.registerResolver(
                    "sable:camera_subject_bounds", PRIORITY, resolver));
    SableMovementInputMapper.createIfAvailable()
        .ifPresent(
            mapper ->
                MinecraftMovementInputMapping.registerMapper(
                    "sable:movement_input", PRIORITY, mapper));
    SableSpatialQueryHitLocationResolver.createIfAvailable()
        .ifPresent(
            resolver ->
                SpatialQueryHitLocation.registerResolver(
                    "sable:spatial_query_hit", PRIORITY, resolver));
    registered = true;
  }
}
