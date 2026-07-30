package io.github.leawind.thirdperson.internal.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.base.api.BaseParameters;
import io.github.leawind.thirdperson.internal.base.api.LookRotation;
import io.github.leawind.thirdperson.internal.base.api.PlayerRotationMode;
import io.github.leawind.thirdperson.internal.base.api.PlayerRotationParameters;
import io.github.leawind.thirdperson.internal.base.api.PlayerRotationSmoothing;
import io.github.leawind.thirdperson.internal.base.api.RaycastOrigin;
import io.github.leawind.thirdperson.internal.base.api.ThirdPersonBase;
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
    PlayerRotationParameters rotation =
        PlayerRotationParameters.of(
            PlayerRotationMode.PARALLEL_WITH_CAMERA,
            0.0,
            PlayerRotationSmoothing.IMMEDIATE);

    scheduler.setAiming(false);
    scheduler.applyParameters(false, rotation);
    assertEquals(scheduler.cameraSettings().normalProfile(), base.parameters.camera());
    assertEquals(
        scheduler.cameraSettings().smoothing().normal().horizontalPivotHalfLife(),
        base.parameters.cameraSmoothing().horizontalPivotHalfLife());
    assertEquals(RaycastOrigin.PLAYER_EYE, base.parameters.raycastOrigin());
    assertEquals(rotation, base.parameters.playerRotation());

    scheduler.setAiming(true);
    scheduler.applyParameters(true, rotation);
    assertEquals(
        scheduler.cameraSettings().aimingProfile().withCentered(true),
        base.parameters.camera());
    assertEquals(
        scheduler.cameraSettings().smoothing().flyingPivotHalfLife(),
        base.parameters.cameraSmoothing().horizontalPivotHalfLife());
    assertEquals(
        scheduler.cameraSettings().smoothing().flyingPivotHalfLife(),
        base.parameters.cameraSmoothing().verticalPivotHalfLife());
  }

  private static final class FakeBase implements ThirdPersonBase {
    private BaseParameters parameters = BaseParameters.defaults();

    @Override
    public void applyParameters(BaseParameters parameters) {
      this.parameters = parameters;
    }

    @Override
    public BaseParameters parameters() {
      return parameters;
    }

    @Override
    public boolean isCameraControlEnabled() {
      return true;
    }

    @Override
    public boolean isControllingLocalPlayer() {
      return true;
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
