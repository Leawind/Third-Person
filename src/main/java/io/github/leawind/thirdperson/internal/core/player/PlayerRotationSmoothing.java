package io.github.leawind.thirdperson.internal.core.player;

/// Timing model used to apply a selected player-rotation target.
public enum PlayerRotationSmoothing {
  IMMEDIATE,
  TICK_INTERPOLATED,
  FRAME_EXPONENTIAL,
}
