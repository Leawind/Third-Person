package io.github.leawind.thirdperson.internal.logic.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.leawind.thirdperson.internal.logic.base.RaycastOrigin;
import java.util.List;
import org.junit.jupiter.api.Test;

class JsonStateCodecTest {
  @Test
  void roundTripsDefaults() {
    ThirdPersonPersistentState state = ThirdPersonPersistentState.defaults();

    assertEquals(state, JsonStateCodec.decode(JsonStateCodec.encode(state)));
  }

  @Test
  void decodesValidEnumAndNumericValues() {
    String json =
        JsonStateCodec.encode(ThirdPersonPersistentState.defaults())
            .replace("\"distance\": 1.5625", "\"distance\": 3.0")
            .replace("\"rotationMode\": \"auto\"", "\"rotationMode\": \"vanilla\"")
            .replace(
                "\"normalMode\": \"interest_point\"",
                "\"normalMode\": \"parallel_with_camera\"")
            .replace("\"autoRotateInteracting\": true", "\"autoRotateInteracting\": false")
            .replace("\"raycastOrigin\": \"camera\"", "\"raycastOrigin\": \"player_eye\"")
            .replace("\"reticle\": \"auto\"", "\"reticle\": \"on\"");

    ThirdPersonPersistentState decoded = JsonStateCodec.decode(json);

    assertEquals(3.0, decoded.camera().normal().distanceFactor());
    assertEquals(ConfiguredPlayerRotationMode.VANILLA, decoded.player().rotationMode());
    assertEquals(NormalPlayerRotationMode.PARALLEL_WITH_CAMERA, decoded.player().normalMode());
    assertFalse(decoded.player().autoRotateInteracting());
    assertEquals(RaycastOrigin.PLAYER_EYE, decoded.player().raycastOrigin());
    assertEquals(ReticleMode.ON, decoded.hud().reticle());
  }

  @Test
  void missingPlayerSettingsUseLegacyDefaults() {
    JsonObject json =
        JsonParser.parseString(JsonStateCodec.encode(ThirdPersonPersistentState.defaults()))
            .getAsJsonObject();
    JsonObject player = json.getAsJsonObject("player");
    player.remove("normalMode");
    player.remove("autoRotateInteracting");
    player.remove("doNotRotateWhenEating");
    player.remove("raycastOrigin");

    ThirdPersonPersistentState decoded = JsonStateCodec.decode(json.toString());

    assertEquals(NormalPlayerRotationMode.INTEREST_POINT, decoded.player().normalMode());
    assertEquals(true, decoded.player().autoRotateInteracting());
    assertEquals(true, decoded.player().doNotRotateWhenEating());
    assertEquals(RaycastOrigin.CAMERA, decoded.player().raycastOrigin());
  }

  @Test
  void ignoresRemovedSettingsFromExistingFiles() {
    JsonObject json =
        JsonParser.parseString(JsonStateCodec.encode(ThirdPersonPersistentState.defaults()))
            .getAsJsonObject();
    json.addProperty("enabled", false);
    json.getAsJsonObject("aiming")
        .add("useToFirstPersonItemPatterns", JsonParser.parseString("[\"minecraft:spyglass\"]"));

    assertEquals(ThirdPersonPersistentState.defaults(), JsonStateCodec.decode(json.toString()));
  }

  @Test
  void roundTripsCustomItemPredicates() {
    ThirdPersonPersistentState defaults = ThirdPersonPersistentState.defaults();
    var state =
        new ThirdPersonPersistentState(
            defaults.schemaVersion(),
            defaults.camera(),
            new ThirdPersonPersistentState.AimingState(
                true, List.of("#example:ranged"), List.of()),
            defaults.player(),
            defaults.hud());

    ThirdPersonPersistentState decoded = JsonStateCodec.decode(JsonStateCodec.encode(state));

    assertEquals(List.of("#example:ranged"), decoded.aiming().holdToAimItemPatterns());
  }

  @Test
  void roundTripsCameraOffsetsThatAreNotConfigScreenOptions() {
    ThirdPersonPersistentState defaults = ThirdPersonPersistentState.defaults();
    var state =
        new ThirdPersonPersistentState(
            defaults.schemaVersion(),
            new ThirdPersonPersistentState.CameraState(
                defaults.camera().normal().withOffsetX(0.42),
                defaults.camera().aiming(),
                defaults.camera().smoothing()),
            defaults.aiming(),
            defaults.player(),
            defaults.hud());

    ThirdPersonPersistentState decoded = JsonStateCodec.decode(JsonStateCodec.encode(state));

    assertEquals(0.42, decoded.camera().normal().offsetX());
  }

  @Test
  void rejectsOutOfRangeNumbers() {
    String json =
        JsonStateCodec.encode(ThirdPersonPersistentState.defaults())
            .replace("\"distance\": 1.5625", "\"distance\": 100.0");

    assertThrows(IllegalArgumentException.class, () -> JsonStateCodec.decode(json));
  }

  @Test
  void rejectsUnknownEnums() {
    String json =
        JsonStateCodec.encode(ThirdPersonPersistentState.defaults())
            .replace("\"rotationMode\": \"auto\"", "\"rotationMode\": \"unknown\"");

    assertThrows(IllegalArgumentException.class, () -> JsonStateCodec.decode(json));
  }

  @Test
  void rejectsUnsupportedSchemas() {
    String json =
        JsonStateCodec.encode(ThirdPersonPersistentState.defaults())
            .replace("\"schemaVersion\": 2", "\"schemaVersion\": 1");

    assertThrows(IllegalArgumentException.class, () -> JsonStateCodec.decode(json));
    assertThrows(
        IllegalArgumentException.class, () -> JsonStateCodec.decode("{\"is_mod_enabled\":true}"));
  }
}
