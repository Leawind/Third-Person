package io.github.leawind.thirdperson.internal.logic.base.camera;

import java.util.Optional;
import org.joml.Vector3d;
import org.joml.Vector3dc;

/// Resolves a desired camera position without exposing Minecraft collision types to application.
@FunctionalInterface
public interface CameraCollisionPort {
  Optional<Vector3d> resolve(Vector3dc pivot, Vector3dc desiredCameraPosition);
}
