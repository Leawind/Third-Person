package io.github.leawind.thirdperson.internal.logic.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.logic.base.BaseParameters;
import io.github.leawind.thirdperson.internal.logic.base.RaycastOrigin;
import io.github.leawind.thirdperson.internal.logic.base.ThirdPersonBase;
import io.github.leawind.thirdperson.internal.logic.base.rotation.LookRotation;
import io.github.leawind.thirdperson.internal.logic.base.rotation.PlayerRotationMode;
import io.github.leawind.thirdperson.internal.logic.base.rotation.PlayerRotationParameters;
import io.github.leawind.thirdperson.internal.logic.base.rotation.PlayerRotationSmoothing;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SchedulerRuntimeTest {
  @Test
  void projectsModesAndDynamicFlightStateIntoModeIndependentBaseParameters() {
    var base = new FakeBase();
    var scheduler = new SchedulerRuntime();
    assertTrue(scheduler.initialize(base));
    assertFalse(scheduler.initialize(base));
    scheduler.playerSettings().setRaycastOrigin(RaycastOrigin.PLAYER_EYE);
    scheduler.soundSettings().setCenterCameraEntitySounds(true);
    PlayerRotationParameters rotation =
        PlayerRotationParameters.of(
            PlayerRotationMode.PARALLEL_WITH_CAMERA, 0.0, PlayerRotationSmoothing.IMMEDIATE);

    scheduler.setAiming(false);
    scheduler.applyParameters(false, true, rotation);
    assertEquals(scheduler.cameraSettings().normalProfile(), base.parameters.camera());
    assertEquals(
        scheduler.cameraSettings().smoothing().normal().horizontalPivotHalfLife(),
        base.parameters.cameraPivotSmoothing().horizontalHalfLife());
    assertEquals(RaycastOrigin.PLAYER_EYE, base.parameters.raycastOrigin());
    assertTrue(base.parameters.centerCameraEntitySounds());
    assertEquals(rotation, base.parameters.playerRotation());

    scheduler.setAiming(true);
    scheduler.applyParameters(true, true, rotation);
    assertEquals(
        scheduler.cameraSettings().aimingProfile().withCentered(true), base.parameters.camera());
    assertEquals(
        scheduler.cameraSettings().smoothing().flyingPivotHalfLife(),
        base.parameters.cameraPivotSmoothing().horizontalHalfLife());
    assertEquals(
        scheduler.cameraSettings().smoothing().flyingPivotHalfLife(),
        base.parameters.cameraPivotSmoothing().verticalHalfLife());
  }

  @Test
  void forcesPlayerEyeRaycastOriginWhenCameraOriginIsNotAllowed() {
    var base = new FakeBase();
    var scheduler = new SchedulerRuntime();
    assertTrue(scheduler.initialize(base));
    scheduler.playerSettings().setRaycastOrigin(RaycastOrigin.CAMERA);

    scheduler.applyParameters(false, false, BaseParameters.defaults().playerRotation());

    assertEquals(RaycastOrigin.PLAYER_EYE, base.parameters.raycastOrigin());
    assertEquals(RaycastOrigin.CAMERA, scheduler.playerSettings().raycastOrigin());

    scheduler.applyParameters(false, true, BaseParameters.defaults().playerRotation());

    assertEquals(RaycastOrigin.CAMERA, base.parameters.raycastOrigin());
  }

  private static final class FakeBase implements ThirdPersonBase {
    private BaseParameters parameters = BaseParameters.defaults();

    @Override
    public void applyParameters(BaseParameters parameters) {
      this.parameters = parameters;
    }

    @Override
    public void commitInteractionRotation(LookRotation rotation) {}

    @Override
    public boolean isCameraControlEnabled() {
      return true;
    }

    @Override
    public boolean isControllingLocalPlayer() {
      return true;
    }

    @Override
    public boolean hasDirectionalMovementIntent(double minimumMagnitude) {
      return false;
    }

    @Override
    public Optional<LookRotation> resolveInterestPointRotation() {
      return Optional.empty();
    }

    @Override
    public Optional<LookRotation> resolvePredictedCameraTargetRotation() {
      return Optional.empty();
    }
  }
}
