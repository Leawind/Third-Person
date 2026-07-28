package io.github.leawind.thirdperson.internal.core.player;

import io.github.leawind.thirdperson.internal.core.config.NormalPlayerRotationMode;
import java.util.Objects;

/// Selects the same rotation behavior and priority as the legacy automatic strategy.
public final class PlayerRotationStrategy {
  private PlayerRotationStrategy() {}

  public static PlayerRotationDecision resolve(PlayerRotationState state) {
    Objects.requireNonNull(state, "state");
    if (state.aiming()) {
      return immediate(PlayerRotationTarget.PREDICTED_TARGET_ENTITY);
    }
    switch (state.normalMode()) {
      case CAMERA_CROSSHAIR -> {
        return immediate(PlayerRotationTarget.CAMERA_HIT_RESULT);
      }
      case PARALLEL_WITH_CAMERA -> {
        return smooth(PlayerRotationTarget.CAMERA_ROTATION, 0.0);
      }
      case NONE -> {
        return smooth(PlayerRotationTarget.CURRENT_ROTATION, 0.0);
      }
      case INTEREST_POINT, MOVING_DIRECTION -> {}
    }
    if (state.fallFlying()) {
      return immediate(PlayerRotationTarget.CAMERA_ROTATION);
    }
    if (state.interacting()) {
      return smooth(PlayerRotationTarget.CAMERA_HIT_RESULT, 0.0);
    }
    if (state.swimming()) {
      return smooth(PlayerRotationTarget.IMPULSE_DIRECTION, 0.01);
    }
    if (state.sprinting()) {
      return smooth(PlayerRotationTarget.HORIZONTAL_IMPULSE_DIRECTION, 0.025);
    }
    if (state.passenger()) {
      return state.vehicleLivingEntity()
          ? frameExponential(PlayerRotationTarget.HORIZONTAL_IMPULSE_DIRECTION, 0.1)
          : smooth(PlayerRotationTarget.INTEREST_POINT, 0.15);
    }
    if (state.normalMode() == NormalPlayerRotationMode.MOVING_DIRECTION) {
      return smooth(PlayerRotationTarget.HORIZONTAL_IMPULSE_DIRECTION, 0.06);
    }
    return smooth(
        state.moving()
            ? PlayerRotationTarget.HORIZONTAL_IMPULSE_DIRECTION
            : PlayerRotationTarget.INTEREST_POINT,
        0.03);
  }

  private static PlayerRotationDecision immediate(PlayerRotationTarget target) {
    return new PlayerRotationDecision(target, 0.0, PlayerRotationSmoothing.IMMEDIATE);
  }

  private static PlayerRotationDecision smooth(PlayerRotationTarget target, double halfLife) {
    return new PlayerRotationDecision(
        target, halfLife, PlayerRotationSmoothing.TICK_INTERPOLATED);
  }

  private static PlayerRotationDecision frameExponential(
      PlayerRotationTarget target, double halfLife) {
    return new PlayerRotationDecision(
        target, halfLife, PlayerRotationSmoothing.FRAME_EXPONENTIAL);
  }
}
