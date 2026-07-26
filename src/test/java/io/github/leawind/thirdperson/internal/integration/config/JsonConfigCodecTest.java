package io.github.leawind.thirdperson.internal.integration.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
            .replace("\"reticle\": \"auto\"", "\"reticle\": \"on\"");

    ThirdPersonConfig decoded = JsonConfigCodec.decode(json);

    assertEquals(8.0, decoded.camera().normal().distance());
    assertEquals(PlayerRotationMode.VANILLA, decoded.player().rotationMode());
    assertEquals(ReticleMode.ON, decoded.hud().reticle());
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
