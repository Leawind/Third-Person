package io.github.leawind.thirdperson.internal.integration.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.leawind.thirdperson.internal.core.config.PlayerRotationMode;
import io.github.leawind.thirdperson.internal.core.config.ReticleMode;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import java.util.Locale;

final class JsonConfigCodec {
  private static final Gson GSON =
      new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  private static final Codec<Double> DISTANCE_CODEC = Codec.doubleRange(0.0, 16.0);
  private static final Codec<Double> OFFSET_CODEC = Codec.doubleRange(-1.0, 1.0);
  private static final Codec<Double> FOV_MULTIPLIER_CODEC = Codec.doubleRange(0.25, 2.0);
  private static final Codec<Double> HALF_LIFE_CODEC = Codec.doubleRange(0.0, 0.2);

  private static final Codec<ThirdPersonConfig.CameraProfile> CAMERA_PROFILE_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      DISTANCE_CODEC
                          .fieldOf("distance")
                          .forGetter(ThirdPersonConfig.CameraProfile::distance),
                      OFFSET_CODEC
                          .fieldOf("offsetX")
                          .forGetter(ThirdPersonConfig.CameraProfile::offsetX),
                      OFFSET_CODEC
                          .fieldOf("offsetY")
                          .forGetter(ThirdPersonConfig.CameraProfile::offsetY),
                      FOV_MULTIPLIER_CODEC
                          .fieldOf("fovMultiplier")
                          .forGetter(ThirdPersonConfig.CameraProfile::fovMultiplier))
                  .apply(instance, ThirdPersonConfig.CameraProfile::new));

  private static final Codec<ThirdPersonConfig.ModeSmoothing> MODE_SMOOTHING_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      HALF_LIFE_CODEC
                          .fieldOf("horizontalPivotHalfLife")
                          .forGetter(ThirdPersonConfig.ModeSmoothing::horizontalPivotHalfLife),
                      HALF_LIFE_CODEC
                          .fieldOf("verticalPivotHalfLife")
                          .forGetter(ThirdPersonConfig.ModeSmoothing::verticalPivotHalfLife),
                      HALF_LIFE_CODEC
                          .fieldOf("offsetHalfLife")
                          .forGetter(ThirdPersonConfig.ModeSmoothing::offsetHalfLife),
                      HALF_LIFE_CODEC
                          .fieldOf("distanceHalfLife")
                          .forGetter(ThirdPersonConfig.ModeSmoothing::distanceHalfLife))
                  .apply(instance, ThirdPersonConfig.ModeSmoothing::new));

  private static final Codec<ThirdPersonConfig.SmoothingSettings> SMOOTHING_SETTINGS_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      HALF_LIFE_CODEC
                          .fieldOf("rotationHalfLife")
                          .forGetter(ThirdPersonConfig.SmoothingSettings::rotationHalfLife),
                      HALF_LIFE_CODEC
                          .fieldOf("flyingPivotHalfLife")
                          .forGetter(ThirdPersonConfig.SmoothingSettings::flyingPivotHalfLife),
                      HALF_LIFE_CODEC
                          .fieldOf("adjustingOffsetHalfLife")
                          .forGetter(ThirdPersonConfig.SmoothingSettings::adjustingOffsetHalfLife),
                      HALF_LIFE_CODEC
                          .fieldOf("adjustingDistanceHalfLife")
                          .forGetter(
                              ThirdPersonConfig.SmoothingSettings::adjustingDistanceHalfLife),
                      MODE_SMOOTHING_CODEC
                          .fieldOf("normal")
                          .forGetter(ThirdPersonConfig.SmoothingSettings::normal),
                      MODE_SMOOTHING_CODEC
                          .fieldOf("aiming")
                          .forGetter(ThirdPersonConfig.SmoothingSettings::aiming))
                  .apply(instance, ThirdPersonConfig.SmoothingSettings::new));

  private static final Codec<ThirdPersonConfig.CameraSettings> CAMERA_SETTINGS_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      CAMERA_PROFILE_CODEC
                          .fieldOf("normal")
                          .forGetter(ThirdPersonConfig.CameraSettings::normal),
                      CAMERA_PROFILE_CODEC
                          .fieldOf("aiming")
                          .forGetter(ThirdPersonConfig.CameraSettings::aiming),
                      SMOOTHING_SETTINGS_CODEC
                          .fieldOf("smoothing")
                          .forGetter(ThirdPersonConfig.CameraSettings::smoothing),
                      Codec.BOOL
                          .fieldOf("temporaryFirstPersonInTightSpace")
                          .forGetter(
                              ThirdPersonConfig.CameraSettings::temporaryFirstPersonInTightSpace))
                  .apply(instance, ThirdPersonConfig.CameraSettings::new));

  private static final Codec<ThirdPersonConfig.AimingSettings> AIMING_SETTINGS_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.BOOL
                          .fieldOf("smartAiming")
                          .forGetter(ThirdPersonConfig.AimingSettings::smartAiming))
                  .apply(instance, ThirdPersonConfig.AimingSettings::new));

  private static final Codec<ThirdPersonConfig.PlayerSettings> PLAYER_SETTINGS_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      enumCodec(PlayerRotationMode.class)
                          .fieldOf("rotationMode")
                          .forGetter(ThirdPersonConfig.PlayerSettings::rotationMode))
                  .apply(instance, ThirdPersonConfig.PlayerSettings::new));

  private static final Codec<ThirdPersonConfig.HudSettings> HUD_SETTINGS_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      enumCodec(ReticleMode.class)
                          .fieldOf("reticle")
                          .forGetter(ThirdPersonConfig.HudSettings::reticle))
                  .apply(instance, ThirdPersonConfig.HudSettings::new));

  private static final Codec<ThirdPersonConfig> CONFIG_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.intRange(
                              ThirdPersonConfig.CURRENT_SCHEMA_VERSION,
                              ThirdPersonConfig.CURRENT_SCHEMA_VERSION)
                          .fieldOf("schemaVersion")
                          .forGetter(ThirdPersonConfig::schemaVersion),
                      Codec.BOOL.fieldOf("enabled").forGetter(ThirdPersonConfig::enabled),
                      CAMERA_SETTINGS_CODEC.fieldOf("camera").forGetter(ThirdPersonConfig::camera),
                      AIMING_SETTINGS_CODEC.fieldOf("aiming").forGetter(ThirdPersonConfig::aiming),
                      PLAYER_SETTINGS_CODEC.fieldOf("player").forGetter(ThirdPersonConfig::player),
                      HUD_SETTINGS_CODEC.fieldOf("hud").forGetter(ThirdPersonConfig::hud))
                  .apply(instance, ThirdPersonConfig::new));

  private JsonConfigCodec() {}

  static ThirdPersonConfig decode(String json) {
    JsonElement jsonElement = JsonParser.parseString(json);
    return requireResult(CONFIG_CODEC.parse(JsonOps.INSTANCE, jsonElement), "decode");
  }

  static String encode(ThirdPersonConfig config) {
    JsonElement jsonElement =
        requireResult(CONFIG_CODEC.encodeStart(JsonOps.INSTANCE, config), "encode");
    return GSON.toJson(jsonElement) + System.lineSeparator();
  }

  private static <E extends Enum<E>> Codec<E> enumCodec(Class<E> type) {
    return Codec.STRING.comapFlatMap(
        name -> {
          try {
            return DataResult.success(Enum.valueOf(type, name.toUpperCase(Locale.ROOT)));
          } catch (IllegalArgumentException exception) {
            return DataResult.error(() -> "Unknown " + type.getSimpleName() + " value: " + name);
          }
        },
        value -> value.name().toLowerCase(Locale.ROOT));
  }

  private static <T> T requireResult(DataResult<T> result, String operation) {
    return result
        .result()
        .orElseThrow(
            () -> new IllegalArgumentException("Failed to " + operation + " config: " + result));
  }
}
