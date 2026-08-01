package io.github.leawind.thirdperson.internal.logic.base;

import io.github.leawind.thirdperson.internal.logic.base.camera.CameraPose;
import io.github.leawind.thirdperson.internal.logic.base.math.FiniteMath;
import java.util.Objects;
import java.util.Optional;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;

/// View-space geometry for sounds emitted by the camera's subject.
public final class SoundSourceGeometry {
  private SoundSourceGeometry() {}

  /// Projects a world-space source onto the camera's vertical center plane.
  public static Optional<Vector3d> projectToViewCenter(
      Vector3dc sourcePosition, CameraPose cameraPose) {
    Objects.requireNonNull(sourcePosition, "sourcePosition");
    Objects.requireNonNull(cameraPose, "cameraPose");
    if (!FiniteMath.isFinite(sourcePosition)) {
      return Optional.empty();
    }

    Vector3d cameraPosition = cameraPose.copyPosition(new Vector3d());
    Vector3f leftAxis =
        cameraPose.copyRotation(new Quaternionf()).transform(new Vector3f(1.0f, 0.0f, 0.0f));
    if (!Float.isFinite(leftAxis.x) || !Float.isFinite(leftAxis.y) || !Float.isFinite(leftAxis.z)) {
      return Optional.empty();
    }

    Vector3d lateralAxis = new Vector3d(leftAxis);
    double lateralDisplacement = new Vector3d(sourcePosition).sub(cameraPosition).dot(lateralAxis);
    if (!Double.isFinite(lateralDisplacement)) {
      return Optional.empty();
    }
    return Optional.of(new Vector3d(sourcePosition).fma(-lateralDisplacement, lateralAxis));
  }
}
