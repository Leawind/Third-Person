package io.github.leawind.thirdperson.internal.integration.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.leawind.thirdperson.internal.core.config.NormalPlayerRotationMode;
import io.github.leawind.thirdperson.internal.core.config.PlayerRotationMode;
import io.github.leawind.thirdperson.internal.core.config.ReticleMode;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import org.junit.jupiter.api.Test;

class JsonConfigCodecTest {
  @Test
  void roundTripsDefaults() {
    ThirdPersonConfig config = ThirdPersonConfig.defaults();

    assertEquals(config, JsonConfigCodec.decode(JsonConfigCodec.encode(config)));
  }

  @Test
  void decodesValidEnumAndNumericValues() {
    String json =
        JsonConfigCodec.encode(ThirdPersonConfig.defaults())
            .replace("\"distance\": 4.0", "\"distance\": 8.0")
            .replace("\"rotationMode\": \"auto\"", "\"rotationMode\": \"vanilla\"")
            .replace(
                "\"normalMode\": \"interest_point\"",
                "\"normalMode\": \"parallel_with_camera\"")
            .replace("\"autoRotateInteracting\": true", "\"autoRotateInteracting\": false")
            .replace("\"reticle\": \"auto\"", "\"reticle\": \"on\"");

    ThirdPersonConfig decoded = JsonConfigCodec.decode(json);

    assertEquals(8.0, decoded.camera().normal().distance());
    assertEquals(PlayerRotationMode.VANILLA, decoded.player().rotationMode());
    assertEquals(NormalPlayerRotationMode.PARALLEL_WITH_CAMERA, decoded.player().normalMode());
    assertEquals(false, decoded.player().autoRotateInteracting());
    assertEquals(ReticleMode.ON, decoded.hud().reticle());
  }

  @Test
  void oldSchemaTwoPlayerSettingsUseLegacyDefaultsForNewFields() {
    JsonObject json =
        JsonParser.parseString(JsonConfigCodec.encode(ThirdPersonConfig.defaults()))
            .getAsJsonObject();
    JsonObject player = json.getAsJsonObject("player");
    player.remove("normalMode");
    player.remove("autoRotateInteracting");
    player.remove("doNotRotateWhenEating");

    ThirdPersonConfig decoded = JsonConfigCodec.decode(json.toString());

    assertEquals(NormalPlayerRotationMode.INTEREST_POINT, decoded.player().normalMode());
    assertEquals(true, decoded.player().autoRotateInteracting());
    assertEquals(true, decoded.player().doNotRotateWhenEating());
  }

  @Test
  void roundTripsCustomItemPredicates() {
    ThirdPersonConfig defaults = ThirdPersonConfig.defaults();
    ThirdPersonConfig config =
        new ThirdPersonConfig(
            defaults.schemaVersion(),
            defaults.enabled(),
            defaults.camera(),
            new ThirdPersonConfig.AimingSettings(
                true, java.util.List.of("#example:ranged"), java.util.List.of(), java.util.List.of()),
            defaults.player(),
            defaults.hud());

    ThirdPersonConfig decoded = JsonConfigCodec.decode(JsonConfigCodec.encode(config));

    assertEquals(
        java.util.List.of("#example:ranged"), decoded.aiming().holdToAimItemPatterns());
  }

  @Test
  void rejectsOutOfRangeNumbers() {
    String json =
        JsonConfigCodec.encode(ThirdPersonConfig.defaults())
            .replace("\"distance\": 4.0", "\"distance\": 100.0");

    assertThrows(IllegalArgumentException.class, () -> JsonConfigCodec.decode(json));
  }

  @Test
  void rejectsUnknownEnums() {
    String json =
        JsonConfigCodec.encode(ThirdPersonConfig.defaults())
            .replace("\"rotationMode\": \"auto\"", "\"rotationMode\": \"unknown\"");

    assertThrows(IllegalArgumentException.class, () -> JsonConfigCodec.decode(json));
  }

  @Test
  void rejectsOtherSchemasWithoutMigrating() {
    String json =
        JsonConfigCodec.encode(ThirdPersonConfig.defaults())
            .replace("\"schemaVersion\": 2", "\"schemaVersion\": 1");

    assertThrows(IllegalArgumentException.class, () -> JsonConfigCodec.decode(json));
    assertThrows(
        IllegalArgumentException.class,
        () -> JsonConfigCodec.decode("{\"is_mod_enabled\":true}"));
  }
}
