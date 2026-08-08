package io.github.leawind.thirdperson.internal.logic.scheduler.rotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.logic.base.rotation.PlayerRotationSmoothing;
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
  void legacyInterestPointModeFacesMovementWhileDirectionalInputIsPresent() {
    PlayerRotationDecision moving = resolveNormal(NormalPlayerRotationMode.INTEREST_POINT, true);
    PlayerRotationDecision stationary =
        resolveNormal(NormalPlayerRotationMode.INTEREST_POINT, false);

    assertEquals(PlayerRotationTarget.HORIZONTAL_IMPULSE_DIRECTION, moving.target());
    assertEquals(0.03, moving.halfLifeSeconds());
    assertEquals(PlayerRotationTarget.INTEREST_POINT, stationary.target());
    assertEquals(0.03, stationary.halfLifeSeconds());
  }

  @Test
  void normalRotationModeCanSelectEachLegacyTarget() {
    assertEquals(
        PlayerRotationTarget.HORIZONTAL_IMPULSE_DIRECTION,
        resolveNormal(NormalPlayerRotationMode.MOVING_DIRECTION, false).target());
    assertEquals(
        0.06, resolveNormal(NormalPlayerRotationMode.MOVING_DIRECTION, false).halfLifeSeconds());
    assertEquals(
        PlayerRotationTarget.CAMERA_HIT_RESULT,
        resolveNormal(NormalPlayerRotationMode.CAMERA_CROSSHAIR, false).target());
    assertEquals(
        PlayerRotationTarget.CAMERA_ROTATION,
        resolveNormal(NormalPlayerRotationMode.PARALLEL_WITH_CAMERA, false).target());
    assertFalse(resolveNormal(NormalPlayerRotationMode.PARALLEL_WITH_CAMERA, false).immediate());
    assertEquals(
        PlayerRotationTarget.CURRENT_ROTATION,
        resolveNormal(NormalPlayerRotationMode.NONE, false).target());
    assertFalse(resolveNormal(NormalPlayerRotationMode.NONE, false).immediate());
  }

  @Test
  void interactionOverridesExplicitNormalTargetsAndAiming() {
    PlayerRotationState fallFlying =
        new PlayerRotationState(
            NormalPlayerRotationMode.NONE, false, true, true, true, true, true, true, true);
    PlayerRotationState aiming =
        new PlayerRotationState(
            NormalPlayerRotationMode.NONE, true, true, true, true, true, true, true, true);

    assertEquals(
        PlayerRotationTarget.CAMERA_HIT_RESULT,
        PlayerRotationStrategy.resolve(fallFlying).target());
    assertEquals(
        PlayerRotationTarget.CAMERA_HIT_RESULT, PlayerRotationStrategy.resolve(aiming).target());
  }

  @Test
  void passengerBehaviorDependsOnTheVehicleType() {
    PlayerRotationDecision nonLiving = resolve(false, false, false, false, false, true, false);
    PlayerRotationDecision living = resolve(false, false, false, false, false, true, true);

    assertEquals(PlayerRotationTarget.INTEREST_POINT, nonLiving.target());
    assertEquals(0.15, nonLiving.halfLifeSeconds());
    assertEquals(PlayerRotationTarget.HORIZONTAL_IMPULSE_DIRECTION, living.target());
    assertEquals(0.1, living.halfLifeSeconds());
    assertEquals(PlayerRotationSmoothing.FRAME_EXPONENTIAL, living.smoothing());
  }

  @Test
  void interactionOverridesOtherAutomaticRotationStates() {
    assertEquals(
        PlayerRotationTarget.HORIZONTAL_IMPULSE_DIRECTION,
        resolve(false, false, true, false, false, true, false).target());
    assertEquals(
        PlayerRotationTarget.IMPULSE_DIRECTION,
        resolve(false, true, true, false, false, true, false).target());
    assertEquals(
        PlayerRotationTarget.CAMERA_HIT_RESULT,
        resolve(false, true, true, false, true, true, false).target());
    assertTrue(resolve(false, true, true, false, true, true, false).immediate());
    assertEquals(
        PlayerRotationTarget.CAMERA_HIT_RESULT,
        resolve(false, true, true, true, true, true, false).target());

    PlayerRotationDecision aiming = resolve(true, true, true, true, true, true, true);
    assertEquals(PlayerRotationTarget.CAMERA_HIT_RESULT, aiming.target());
    assertTrue(aiming.immediate());
  }

  @Test
  void interactionOverridesEveryNormalRotationMode() {
    for (NormalPlayerRotationMode mode : NormalPlayerRotationMode.values()) {
      PlayerRotationDecision decision =
          PlayerRotationStrategy.resolve(
              new PlayerRotationState(mode, false, false, false, false, true, false, false, false));
      assertEquals(PlayerRotationTarget.CAMERA_HIT_RESULT, decision.target());
      assertTrue(decision.immediate());
    }
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
            NormalPlayerRotationMode.INTEREST_POINT,
            aiming,
            swimming,
            sprinting,
            fallFlying,
            interacting,
            passenger,
            vehicleLivingEntity,
            false));
  }

  private static PlayerRotationDecision resolveNormal(
      NormalPlayerRotationMode mode, boolean moving) {
    return PlayerRotationStrategy.resolve(
        new PlayerRotationState(mode, false, false, false, false, false, false, false, moving));
  }
}
