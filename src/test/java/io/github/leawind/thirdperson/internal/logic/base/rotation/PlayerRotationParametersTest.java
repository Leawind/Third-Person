package io.github.leawind.thirdperson.internal.logic.base.rotation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class PlayerRotationParametersTest {
  @Test
  void baseExposesExactlyTheFourModeIndependentRotationBehaviors() {
    assertArrayEquals(
        new PlayerRotationMode[] {
          PlayerRotationMode.CUSTOM,
          PlayerRotationMode.PARALLEL_WITH_CAMERA,
          PlayerRotationMode.LOOK_AT_CAMERA_RAY_HIT,
          PlayerRotationMode.MOVEMENT_DIRECTION,
        },
        PlayerRotationMode.values());
  }

  @Test
  void customModeCarriesTheSchedulingLayerOrientation() {
    LookRotation target = new LookRotation(45.0f, -10.0f);

    PlayerRotationParameters parameters =
        PlayerRotationParameters.custom(
            Optional.of(target), 0.03, PlayerRotationSmoothing.TICK_INTERPOLATED);

    assertEquals(PlayerRotationMode.CUSTOM, parameters.mode());
    assertEquals(target, parameters.customRotation().orElseThrow());
    assertThrows(
        IllegalArgumentException.class,
        () ->
            PlayerRotationParameters.of(
                PlayerRotationMode.CUSTOM, 0.0, PlayerRotationSmoothing.IMMEDIATE));
  }
}
