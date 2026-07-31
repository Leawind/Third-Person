package io.github.leawind.thirdperson.internal.logic.base.rotation;

/// The four player-facing behaviors implemented by the base layer.
public enum PlayerRotationMode {
  /// Use an orientation supplied by the scheduling layer.
  CUSTOM,
  /// Keep the player parallel with the camera view.
  PARALLEL_WITH_CAMERA,
  /// Turn the player toward the point hit by the camera-directed ray.
  LOOK_AT_CAMERA_RAY_HIT,
  /// Turn the player toward the current camera-relative movement direction.
  MOVEMENT_DIRECTION,
}
