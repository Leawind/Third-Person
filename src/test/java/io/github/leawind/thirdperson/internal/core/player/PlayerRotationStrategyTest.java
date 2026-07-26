package io.github.leawind.thirdperson.internal.core.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlayerRotationStrategyTest {
  @Test
  void normalRotationUsesTheLegacyInterestPointDefault() {
    PlayerRotationDecision decision = resolve(false, false, false, false, false, false, false);

    assertEquals(PlayerRotationTarget.INTEREST_POINT, decision.target());
    assertEquals(0.03, decision.halfLifeSeconds());
    assertFalse(decision.immediate());
  }

  @Test
  void passengerBehaviorDependsOnTheVehicleType() {
    PlayerRotationDecision nonLiving =
        resolve(false, false, false, false, false, true, false);
    PlayerRotationDecision living =
        resolve(false, false, false, false, false, true, true);

    assertEquals(PlayerRotationTarget.INTEREST_POINT, nonLiving.target());
    assertEquals(0.15, nonLiving.halfLifeSeconds());
    assertEquals(PlayerRotationTarget.HORIZONTAL_IMPULSE_DIRECTION, living.target());
    assertEquals(0.1, living.halfLifeSeconds());
  }

  @Test
  void exceptionalStatesUseTheLegacyPriority() {
    assertEquals(
        PlayerRotationTarget.HORIZONTAL_IMPULSE_DIRECTION,
        resolve(false, false, true, false, false, true, false).target());
    assertEquals(
        PlayerRotationTarget.IMPULSE_DIRECTION,
        resolve(false, true, true, false, false, true, false).target());
    assertEquals(
        PlayerRotationTarget.CAMERA_HIT_RESULT,
        resolve(false, true, true, false, true, true, false).target());
    assertEquals(
        PlayerRotationTarget.CAMERA_ROTATION,
        resolve(false, true, true, true, true, true, false).target());

    PlayerRotationDecision aiming = resolve(true, true, true, true, true, true, true);
    assertEquals(PlayerRotationTarget.PREDICTED_TARGET_ENTITY, aiming.target());
    assertTrue(aiming.immediate());
  }

  private static PlayerRotationDecision resolve(
      boolean aiming,
      boolean swimming,
      boolean sprinting,
      boolean fallFlying,
      boolean interacting,
      boolean passenger,
      boolean vehicleLivingEntity) {
    return PlayerRotationStrategy.resolve(
        new PlayerRotationState(
            aiming,
            swimming,
            sprinting,
            fallFlying,
            interacting,
            passenger,
            vehicleLivingEntity));
  }
}
