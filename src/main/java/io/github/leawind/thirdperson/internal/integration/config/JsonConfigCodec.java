package io.github.leawind.thirdperson.internal.integration.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.leawind.thirdperson.internal.core.config.ConfigValidation;
import io.github.leawind.thirdperson.internal.core.config.PlayerRotationMode;
import io.github.leawind.thirdperson.internal.core.config.ReticleMode;
import io.github.leawind.thirdperson.internal.core.config.SmoothingPreset;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import java.util.Locale;

final class JsonConfigCodec {
  private static final Gson GSON =
      new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  private JsonConfigCodec() {}

  static DecodedConfig decode(String json) {
    JsonElement parsed = JsonParser.parseString(json);
    if (!parsed.isJsonObject()) {
      throw new IllegalArgumentException("Config root must be a JSON object");
    }

    JsonObject root = parsed.getAsJsonObject();
    boolean legacy = !root.has("schemaVersion") && root.has("is_mod_enabled");
    return legacy ? decodeLegacy(root) : new DecodedConfig(decodeCurrent(root), false);
  }

  static String encode(ThirdPersonConfig config) {
    JsonObject root = new JsonObject();
    root.addProperty("schemaVersion", config.schemaVersion());
    root.addProperty("enabled", config.enabled());

    JsonObject camera = new JsonObject();
    camera.add("normal", encodeProfile(config.camera().normal()));
    camera.add("aiming", encodeProfile(config.camera().aiming()));
    camera.addProperty("smoothing", lowerName(config.camera().smoothing()));
    camera.addProperty(
        "temporaryFirstPersonInTightSpace",
        config.camera().temporaryFirstPersonInTightSpace());
    root.add("camera", camera);

    JsonObject aiming = new JsonObject();
    aiming.addProperty("smartAiming", config.aiming().smartAiming());
    root.add("aiming", aiming);

    JsonObject player = new JsonObject();
    player.addProperty("rotationMode", lowerName(config.player().rotationMode()));
    root.add("player", player);

    JsonObject hud = new JsonObject();
    hud.addProperty("reticle", lowerName(config.hud().reticle()));
    root.add("hud", hud);
    return GSON.toJson(root) + System.lineSeparator();
  }

  private static ThirdPersonConfig decodeCurrent(JsonObject root) {
    ThirdPersonConfig defaults = ThirdPersonConfig.defaults();
    int schemaVersion = integer(root, "schemaVersion", ThirdPersonConfig.CURRENT_SCHEMA_VERSION);
    if (schemaVersion != ThirdPersonConfig.CURRENT_SCHEMA_VERSION) {
      throw new IllegalArgumentException("Unsupported config schema version: " + schemaVersion);
    }
    JsonObject camera = object(root, "camera");
    JsonObject aiming = object(root, "aiming");
    JsonObject player = object(root, "player");
    JsonObject hud = object(root, "hud");

    return new ThirdPersonConfig(
        ThirdPersonConfig.CURRENT_SCHEMA_VERSION,
        bool(root, "enabled", defaults.enabled()),
        new ThirdPersonConfig.CameraSettings(
            decodeProfile(object(camera, "normal"), defaults.camera().normal()),
            decodeProfile(object(camera, "aiming"), defaults.camera().aiming()),
            enumValue(
                camera,
                "smoothing",
                SmoothingPreset.class,
                defaults.camera().smoothing()),
            bool(
                camera,
                "temporaryFirstPersonInTightSpace",
                defaults.camera().temporaryFirstPersonInTightSpace())),
        new ThirdPersonConfig.AimingSettings(
            bool(aiming, "smartAiming", defaults.aiming().smartAiming())),
        new ThirdPersonConfig.PlayerSettings(
            enumValue(
                player,
                "rotationMode",
                PlayerRotationMode.class,
                defaults.player().rotationMode())),
        new ThirdPersonConfig.HudSettings(
            enumValue(hud, "reticle", ReticleMode.class, defaults.hud().reticle())));
  }

  private static DecodedConfig decodeLegacy(JsonObject root) {
    ThirdPersonConfig defaults = ThirdPersonConfig.defaults();
    var normal =
        profileFromValues(
            number(root, "normal_max_distance", defaults.camera().normal().distance()),
            number(root, "normal_offset_x", defaults.camera().normal().offsetX()),
            number(root, "normal_offset_y", defaults.camera().normal().offsetY()),
            1.0,
            defaults.camera().normal());
    double divisor = number(root, "aiming_fov_divisor", 1.0 / defaults.camera().aiming().fovMultiplier());
    double aimingFov = Double.isFinite(divisor) && divisor > 0.0 ? 1.0 / divisor : defaults.camera().aiming().fovMultiplier();
    var aiming =
        profileFromValues(
            number(root, "aiming_max_distance", defaults.camera().aiming().distance()),
            number(root, "aiming_offset_x", defaults.camera().aiming().offsetX()),
            number(root, "aiming_offset_y", defaults.camera().aiming().offsetY()),
            aimingFov,
            defaults.camera().aiming());

    return new DecodedConfig(
        new ThirdPersonConfig(
            ThirdPersonConfig.CURRENT_SCHEMA_VERSION,
            bool(root, "is_mod_enabled", defaults.enabled()),
            new ThirdPersonConfig.CameraSettings(
                normal,
                aiming,
                defaults.camera().smoothing(),
                bool(
                    root,
                    "temp_first_person_in_narrow_space",
                    defaults.camera().temporaryFirstPersonInTightSpace())),
            defaults.aiming(),
            defaults.player(),
            defaults.hud()),
        true);
  }

  private static ThirdPersonConfig.CameraProfile decodeProfile(
      JsonObject object, ThirdPersonConfig.CameraProfile defaults) {
    return profileFromValues(
        number(object, "distance", defaults.distance()),
        number(object, "offsetX", defaults.offsetX()),
        number(object, "offsetY", defaults.offsetY()),
        number(object, "fovMultiplier", defaults.fovMultiplier()),
        defaults);
  }

  private static ThirdPersonConfig.CameraProfile profileFromValues(
      double distance,
      double offsetX,
      double offsetY,
      double fovMultiplier,
      ThirdPersonConfig.CameraProfile defaults) {
    return new ThirdPersonConfig.CameraProfile(
        ConfigValidation.finiteClamped(distance, 0.0, 16.0, defaults.distance()),
        ConfigValidation.finiteClamped(offsetX, -1.0, 1.0, defaults.offsetX()),
        ConfigValidation.finiteClamped(offsetY, -1.0, 1.0, defaults.offsetY()),
        ConfigValidation.finiteClamped(fovMultiplier, 0.25, 2.0, defaults.fovMultiplier()));
  }

  private static JsonObject encodeProfile(ThirdPersonConfig.CameraProfile profile) {
    JsonObject object = new JsonObject();
    object.addProperty("distance", profile.distance());
    object.addProperty("offsetX", profile.offsetX());
    object.addProperty("offsetY", profile.offsetY());
    object.addProperty("fovMultiplier", profile.fovMultiplier());
    return object;
  }

  private static JsonObject object(JsonObject parent, String name) {
    JsonElement value = parent.get(name);
    return value != null && value.isJsonObject() ? value.getAsJsonObject() : new JsonObject();
  }

  private static boolean bool(JsonObject object, String name, boolean fallback) {
    JsonElement value = object.get(name);
    try {
      return value != null && value.isJsonPrimitive() ? value.getAsBoolean() : fallback;
    } catch (RuntimeException ignored) {
      return fallback;
    }
  }

  private static double number(JsonObject object, String name, double fallback) {
    JsonElement value = object.get(name);
    try {
      return value != null && value.isJsonPrimitive() ? value.getAsDouble() : fallback;
    } catch (RuntimeException ignored) {
      return fallback;
    }
  }

  private static int integer(JsonObject object, String name, int fallback) {
    JsonElement value = object.get(name);
    try {
      return value != null && value.isJsonPrimitive() ? value.getAsInt() : fallback;
    } catch (RuntimeException ignored) {
      return fallback;
    }
  }

  private static <E extends Enum<E>> E enumValue(
      JsonObject object, String name, Class<E> type, E fallback) {
    JsonElement value = object.get(name);
    if (value == null || !value.isJsonPrimitive()) {
      return fallback;
    }
    try {
      return Enum.valueOf(type, value.getAsString().toUpperCase(Locale.ROOT));
    } catch (RuntimeException ignored) {
      return fallback;
    }
  }

  private static String lowerName(Enum<?> value) {
    return value.name().toLowerCase(Locale.ROOT);
  }
}
