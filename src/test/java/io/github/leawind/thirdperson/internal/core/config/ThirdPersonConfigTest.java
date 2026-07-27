package io.github.leawind.thirdperson.internal.core.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class ThirdPersonConfigTest {
  @Test
  void defaultsMatchTheCompactPublicSchema() {
    ThirdPersonConfig config = ThirdPersonConfig.defaults();

    assertEquals(ThirdPersonConfig.CURRENT_SCHEMA_VERSION, config.schemaVersion());
    assertEquals(4.0, config.camera().normal().distance());
    assertEquals(-0.18, config.camera().normal().offsetX());
    assertEquals(2.4, config.camera().aiming().distance());
    assertEquals(0.0, config.camera().smoothing().rotationHalfLife());
    assertEquals(0.064, config.camera().smoothing().normal().horizontalPivotHalfLife());
    assertEquals(0.08, config.camera().smoothing().normal().verticalPivotHalfLife());
    assertEquals(0.08, config.camera().smoothing().normal().distanceHalfLife());
    assertEquals(0.08, config.camera().smoothing().aiming().distanceHalfLife());
    assertEquals(0.0, config.camera().smoothing().normal().fovHalfLife());
    assertEquals(0.0, config.camera().smoothing().aiming().fovHalfLife());
    assertEquals(PlayerRotationMode.AUTO, config.player().rotationMode());
    assertEquals(NormalPlayerRotationMode.INTEREST_POINT, config.player().normalMode());
    assertEquals(true, config.player().autoRotateInteracting());
    assertEquals(true, config.player().doNotRotateWhenEating());
    assertEquals(ReticleMode.AUTO, config.hud().reticle());
    assertEquals(0.24, config.camera().normal().centeredOffsetY());
    assertEquals(0.0, config.camera().normal().withCentered(true).cameraParameters().anchorNdcX());
    assertEquals(
        0.24, config.camera().normal().withCentered(true).cameraParameters().anchorNdcY());
    assertEquals(
        config.camera().normal().offsetY(),
        config.camera().normal().withCentered(true).offsetY());
    assertEquals(List.of(), config.aiming().holdToAimItemPatterns());
    assertEquals(List.of(), config.aiming().useToAimItemPatterns());
    assertEquals(List.of(), config.aiming().useToFirstPersonItemPatterns());
  }

  @Test
  void profileRejectsInvalidDirectConstruction() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new ThirdPersonConfig.CameraProfile(Double.NaN, 0.0, 0.0, 0.0, 1.0, false));
    assertThrows(
        IllegalArgumentException.class,
        () -> new ThirdPersonConfig.CameraProfile(4.0, 2.0, 0.0, 0.0, 1.0, false));
    assertThrows(
        IllegalArgumentException.class,
        () -> new ThirdPersonConfig.ModeSmoothing(-0.1, 0.0, 0.0, 0.0, 0.0));
    assertThrows(
        IllegalArgumentException.class,
        () -> new ThirdPersonConfig.ModeSmoothing(0.3, 0.0, 0.0, 0.0, 0.0));
  }

  @Test
  void aimingSettingsDefensivelyCopyItemPatternLists() {
    var patterns = new java.util.ArrayList<>(List.of("minecraft:bow"));
    var aiming = new ThirdPersonConfig.AimingSettings(true, patterns, List.of(), List.of());

    patterns.add("minecraft:crossbow");

    assertEquals(List.of("minecraft:bow"), aiming.holdToAimItemPatterns());
    assertThrows(
        UnsupportedOperationException.class,
        () -> aiming.holdToAimItemPatterns().add("minecraft:trident"));
  }
}
