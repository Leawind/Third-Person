package io.github.leawind.thirdperson.internal.integration.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.leawind.thirdperson.internal.core.config.PlayerRotationMode;
import io.github.leawind.thirdperson.internal.core.config.ReticleMode;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import org.junit.jupiter.api.Test;

class JsonConfigCodecTest {
  @Test
  void roundTripsDefaults() {
    DecodedConfig decoded = JsonConfigCodec.decode(JsonConfigCodec.encode(ThirdPersonConfig.defaults()));

    assertEquals(ThirdPersonConfig.defaults(), decoded.config());
    assertFalse(decoded.migrated());
  }

  @Test
  void clampsNumbersAndFallsBackForUnknownEnums() {
    DecodedConfig decoded =
        JsonConfigCodec.decode(
            """
            {
              "schemaVersion": 2,
              "camera": {
                "normal": {
                  "distance": 100,
                  "offsetX": -2,
                  "offsetY": 0.25,
                  "fovMultiplier": 0.01
                },
                "smoothing": {
                  "rotationHalfLife": 100,
                  "normal": { "horizontalPivotHalfLife": -10 }
                }
              },
              "player": { "rotationMode": "vanilla" },
              "hud": { "reticle": "on" }
            }
            """);

    assertEquals(16.0, decoded.config().camera().normal().distance());
    assertEquals(-1.0, decoded.config().camera().normal().offsetX());
    assertEquals(0.25, decoded.config().camera().normal().offsetY());
    assertEquals(0.25, decoded.config().camera().normal().fovMultiplier());
    assertEquals(1.0, decoded.config().camera().smoothing().rotationHalfLife());
    assertEquals(
        0.0,
        decoded.config().camera().smoothing().normal().horizontalPivotHalfLife());
    assertEquals(PlayerRotationMode.VANILLA, decoded.config().player().rotationMode());
    assertEquals(ReticleMode.ON, decoded.config().hud().reticle());
  }

  @Test
  void migratesOnlyConservativeLegacyFields() {
    DecodedConfig decoded =
        JsonConfigCodec.decode(
            """
            {
              "is_mod_enabled": false,
              "temp_first_person_in_narrow_space": false,
              "normal_max_distance": 3.5,
              "normal_offset_x": 0.2,
              "normal_offset_y": -0.1,
              "aiming_max_distance": 1.75,
              "aiming_offset_x": -0.4,
              "aiming_offset_y": 0.3,
              "aiming_fov_divisor": 1.25,
              "normal_distance_smooth_halflife": 0.5,
              "aiming_camera_offset_smooth_halflife": 0.015,
              "player_fade_out_enabled": true
            }
            """);

    assertTrue(decoded.migrated());
    assertFalse(decoded.config().enabled());
    assertFalse(decoded.config().camera().temporaryFirstPersonInTightSpace());
    assertEquals(3.5, decoded.config().camera().normal().distance());
    assertEquals(1.75, decoded.config().camera().aiming().distance());
    assertEquals(0.8, decoded.config().camera().aiming().fovMultiplier());
    assertEquals(0.5, decoded.config().camera().smoothing().normal().distanceHalfLife());
    assertEquals(0.015, decoded.config().camera().smoothing().aiming().offsetHalfLife());
  }

  @Test
  void migratesSchemaOnePresetWithoutKeepingMouseRotationLag() {
    DecodedConfig decoded =
        JsonConfigCodec.decode(
            """
            {
              "schemaVersion": 1,
              "camera": { "smoothing": "cinematic" }
            }
            """);

    assertTrue(decoded.migrated());
    assertEquals(0.0, decoded.config().camera().smoothing().rotationHalfLife());
    assertEquals(0.25, decoded.config().camera().smoothing().normal().distanceHalfLife());
    assertEquals(0.25, decoded.config().camera().smoothing().aiming().offsetHalfLife());
  }

  @Test
  void rejectsFutureSchema() {
    assertThrows(
        IllegalArgumentException.class,
        () -> JsonConfigCodec.decode("{\"schemaVersion\": 3}"));
  }
}
