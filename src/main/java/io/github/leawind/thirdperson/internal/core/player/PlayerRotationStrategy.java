package io.github.leawind.thirdperson.internal.core.player;

import java.util.Objects;

/// Selects the same rotation behavior and priority as the legacy automatic strategy.
public final class PlayerRotationStrategy {
  private PlayerRotationStrategy() {}

  public static PlayerRotationDecision resolve(PlayerRotationState state) {
    Objects.requireNonNull(state, "state");
    if (state.aiming()) {
      return immediate(PlayerRotationTarget.PREDICTED_TARGET_ENTITY);
    }
    if (state.fallFlying()) {
      return immediate(PlayerRotationTarget.CAMERA_ROTATION);
    }
    if (state.interacting()) {
      return immediate(PlayerRotationTarget.CAMERA_HIT_RESULT);
    }
    if (state.swimming()) {
      return smooth(PlayerRotationTarget.IMPULSE_DIRECTION, 0.01);
    }
    if (state.sprinting()) {
      return smooth(PlayerRotationTarget.HORIZONTAL_IMPULSE_DIRECTION, 0.025);
    }
    if (state.passenger()) {
      return state.vehicleLivingEntity()
          ? smooth(PlayerRotationTarget.HORIZONTAL_IMPULSE_DIRECTION, 0.1)
          : smooth(PlayerRotationTarget.INTEREST_POINT, 0.15);
    }
    return smooth(PlayerRotationTarget.INTEREST_POINT, 0.03);
  }

  private static PlayerRotationDecision immediate(PlayerRotationTarget target) {
    return new PlayerRotationDecision(target, 0.0, true);
  }

  private static PlayerRotationDecision smooth(PlayerRotationTarget target, double halfLife) {
    return new PlayerRotationDecision(target, halfLife, false);
  }
}
