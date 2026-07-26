package io.github.leawind.thirdperson.internal.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ThirdPersonConfigTest {
  @Test
  void defaultsMatchTheCompactPublicSchema() {
    ThirdPersonConfig config = ThirdPersonConfig.defaults();

    assertEquals(ThirdPersonConfig.CURRENT_SCHEMA_VERSION, config.schemaVersion());
    assertEquals(4.0, config.camera().normal().distance());
    assertEquals(-0.18, config.camera().normal().offsetX());
    assertEquals(2.4, config.camera().aiming().distance());
    assertEquals(SmoothingPreset.BALANCED, config.camera().smoothing());
    assertEquals(PlayerRotationMode.AUTO, config.player().rotationMode());
    assertEquals(ReticleMode.AUTO, config.hud().reticle());
    assertEquals(0.0, config.camera().normal().centered().offsetX());
    assertEquals(
        config.camera().normal().distance(), config.camera().normal().centered().distance());
  }

  @Test
  void profileRejectsInvalidDirectConstruction() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new ThirdPersonConfig.CameraProfile(Double.NaN, 0.0, 0.0, 1.0));
    assertThrows(
        IllegalArgumentException.class,
        () -> new ThirdPersonConfig.CameraProfile(4.0, 2.0, 0.0, 1.0));
  }
}
