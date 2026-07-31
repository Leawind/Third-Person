package io.github.leawind.thirdperson.internal.logic.scheduler;

/// Rotation target used when no higher-priority aiming behavior is active.
public enum NormalPlayerRotationMode {
  INTEREST_POINT,
  CAMERA_CROSSHAIR,
  MOVING_DIRECTION,
  PARALLEL_WITH_CAMERA,
  NONE,
}
