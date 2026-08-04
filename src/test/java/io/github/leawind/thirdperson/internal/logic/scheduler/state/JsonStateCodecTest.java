package io.github.leawind.thirdperson.internal.logic.scheduler.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.leawind.thirdperson.internal.logic.base.RaycastOrigin;
import io.github.leawind.thirdperson.internal.logic.scheduler.hud.CrosshairMode;
import io.github.leawind.thirdperson.internal.logic.scheduler.rotation.ConfiguredPlayerRotationMode;
import io.github.leawind.thirdperson.internal.logic.scheduler.rotation.NormalPlayerRotationMode;
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
    JsonObject json = encodedDefaultsObject();
    json.getAsJsonObject("camera").getAsJsonObject("normal").addProperty("distance", 3.0);
    JsonObject player = json.getAsJsonObject("player");
    player.addProperty("rotationMode", "vanilla");
    player.addProperty("normalMode", "parallel_with_camera");
    player.addProperty("autoRotateInteracting", false);
    player.addProperty("raycastOrigin", "player_eye");
    json.getAsJsonObject("sound").addProperty("centerCameraEntitySounds", true);
    JsonObject hud = json.getAsJsonObject("hud");
    hud.addProperty("crosshair", "not_aiming");
    hud.addProperty("hideCrosshairWhenFallFlyingAndNotAiming", false);

    ThirdPersonPersistentState decoded = JsonStateCodec.decode(json.toString());

    assertEquals(3.0, decoded.camera().normal().distanceFactor());
    assertEquals(ConfiguredPlayerRotationMode.VANILLA, decoded.player().rotationMode());
    assertEquals(NormalPlayerRotationMode.PARALLEL_WITH_CAMERA, decoded.player().normalMode());
    assertFalse(decoded.player().autoRotateInteracting());
    assertEquals(RaycastOrigin.PLAYER_EYE, decoded.player().raycastOrigin());
    assertTrue(decoded.sound().centerCameraEntitySounds());
    assertEquals(CrosshairMode.NOT_AIMING, decoded.hud().crosshair());
    assertFalse(decoded.hud().hideCrosshairWhenFallFlyingAndNotAiming());
  }

  @Test
  void missingPlayerSettingsUseLegacyDefaults() {
    JsonObject json = encodedDefaultsObject();
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
    JsonObject json = encodedDefaultsObject();
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
            new ThirdPersonPersistentState.AimingState(true, List.of("#example:ranged"), List.of()),
            defaults.player(),
            defaults.sound(),
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
            defaults.sound(),
            defaults.hud());

    ThirdPersonPersistentState decoded = JsonStateCodec.decode(JsonStateCodec.encode(state));

    assertEquals(0.42, decoded.camera().normal().offsetX());
  }

  @Test
  void rejectsOutOfRangeNumbers() {
    JsonObject json = encodedDefaultsObject();
    json.getAsJsonObject("camera").getAsJsonObject("normal").addProperty("distance", 100.0);

    assertThrows(IllegalArgumentException.class, () -> JsonStateCodec.decode(json.toString()));
  }

  @Test
  void rejectsUnknownEnums() {
    JsonObject json = encodedDefaultsObject();
    json.getAsJsonObject("player").addProperty("rotationMode", "unknown");

    assertThrows(IllegalArgumentException.class, () -> JsonStateCodec.decode(json.toString()));
  }

  @Test
  void rejectsUnsupportedSchemas() {
    JsonObject json = encodedDefaultsObject();
    json.addProperty("schemaVersion", ThirdPersonPersistentState.CURRENT_SCHEMA_VERSION - 1);

    assertThrows(IllegalArgumentException.class, () -> JsonStateCodec.decode(json.toString()));
    assertThrows(
        IllegalArgumentException.class, () -> JsonStateCodec.decode("{\"is_mod_enabled\":true}"));
  }

  private static JsonObject encodedDefaultsObject() {
    return JsonParser.parseString(JsonStateCodec.encode(ThirdPersonPersistentState.defaults()))
        .getAsJsonObject();
  }
}
