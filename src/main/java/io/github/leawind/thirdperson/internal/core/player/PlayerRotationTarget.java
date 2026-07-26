package io.github.leawind.thirdperson.internal.core.player;

/// World-space target used to derive the local player's logical rotation.
public enum PlayerRotationTarget {
  CURRENT_ROTATION,
  INTEREST_POINT,
  CAMERA_ROTATION,
  CAMERA_HIT_RESULT,
  PREDICTED_TARGET_ENTITY,
  HORIZONTAL_IMPULSE_DIRECTION,
  IMPULSE_DIRECTION,
}
