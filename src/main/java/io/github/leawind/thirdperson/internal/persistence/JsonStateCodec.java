package io.github.leawind.thirdperson.internal.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.leawind.thirdperson.internal.core.camera.CameraProfile;
import io.github.leawind.thirdperson.internal.core.camera.CameraSmoothing;
import io.github.leawind.thirdperson.internal.core.camera.ModeSmoothing;
import io.github.leawind.thirdperson.internal.core.hud.ReticleMode;
import io.github.leawind.thirdperson.internal.core.player.NormalPlayerRotationMode;
import io.github.leawind.thirdperson.internal.core.player.PlayerRotationMode;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

final class JsonStateCodec {
  private static final Gson GSON =
      new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  private static final Codec<Double> DISTANCE_CODEC = Codec.doubleRange(0.0, 16.0);
  private static final Codec<Double> OFFSET_CODEC = Codec.doubleRange(-1.0, 1.0);
  private static final Codec<Double> FOV_MULTIPLIER_CODEC = Codec.doubleRange(0.25, 2.0);
  private static final Codec<Double> HALF_LIFE_CODEC = Codec.doubleRange(0.0, 0.2);

  private static final Codec<CameraProfile> CAMERA_PROFILE_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      DISTANCE_CODEC.fieldOf("distance").forGetter(CameraProfile::distanceFactor),
                      OFFSET_CODEC.fieldOf("offsetX").forGetter(CameraProfile::offsetX),
                      OFFSET_CODEC.fieldOf("offsetY").forGetter(CameraProfile::offsetY),
                      OFFSET_CODEC
                          .fieldOf("centeredOffsetY")
                          .forGetter(CameraProfile::centeredOffsetY),
                      FOV_MULTIPLIER_CODEC
                          .fieldOf("fovMultiplier")
                          .forGetter(CameraProfile::fovMultiplier),
                      Codec.BOOL.fieldOf("centered").forGetter(CameraProfile::centered))
                  .apply(instance, CameraProfile::new));

  private static final Codec<ModeSmoothing> MODE_SMOOTHING_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      HALF_LIFE_CODEC
                          .fieldOf("horizontalPivotHalfLife")
                          .forGetter(ModeSmoothing::horizontalPivotHalfLife),
                      HALF_LIFE_CODEC
                          .fieldOf("verticalPivotHalfLife")
                          .forGetter(ModeSmoothing::verticalPivotHalfLife),
                      HALF_LIFE_CODEC
                          .fieldOf("offsetHalfLife")
                          .forGetter(ModeSmoothing::offsetHalfLife),
                      HALF_LIFE_CODEC
                          .fieldOf("distanceHalfLife")
                          .forGetter(ModeSmoothing::distanceHalfLife),
                      HALF_LIFE_CODEC
                          .fieldOf("fovHalfLife")
                          .forGetter(ModeSmoothing::fovHalfLife))
                  .apply(instance, ModeSmoothing::new));

  private static final Codec<CameraSmoothing> CAMERA_SMOOTHING_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      HALF_LIFE_CODEC
                          .fieldOf("rotationHalfLife")
                          .forGetter(CameraSmoothing::rotationHalfLife),
                      HALF_LIFE_CODEC
                          .fieldOf("flyingPivotHalfLife")
                          .forGetter(CameraSmoothing::flyingPivotHalfLife),
                      HALF_LIFE_CODEC
                          .fieldOf("adjustingOffsetHalfLife")
                          .forGetter(CameraSmoothing::adjustingOffsetHalfLife),
                      HALF_LIFE_CODEC
                          .fieldOf("adjustingDistanceHalfLife")
                          .forGetter(CameraSmoothing::adjustingDistanceHalfLife),
                      MODE_SMOOTHING_CODEC.fieldOf("normal").forGetter(CameraSmoothing::normal),
                      MODE_SMOOTHING_CODEC.fieldOf("aiming").forGetter(CameraSmoothing::aiming))
                  .apply(instance, CameraSmoothing::new));

  private static final Codec<ThirdPersonPersistentState.CameraState> CAMERA_STATE_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      CAMERA_PROFILE_CODEC
                          .fieldOf("normal")
                          .forGetter(ThirdPersonPersistentState.CameraState::normal),
                      CAMERA_PROFILE_CODEC
                          .fieldOf("aiming")
                          .forGetter(ThirdPersonPersistentState.CameraState::aiming),
                      CAMERA_SMOOTHING_CODEC
                          .fieldOf("smoothing")
                          .forGetter(ThirdPersonPersistentState.CameraState::smoothing))
                  .apply(instance, ThirdPersonPersistentState.CameraState::new));

  private static final Codec<ThirdPersonPersistentState.AimingState> AIMING_STATE_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.BOOL
                          .fieldOf("smartAiming")
                          .forGetter(ThirdPersonPersistentState.AimingState::smartAiming),
                      Codec.STRING
                          .listOf()
                          .optionalFieldOf("holdToAimItemPatterns", List.of())
                          .forGetter(
                              ThirdPersonPersistentState.AimingState::holdToAimItemPatterns),
                      Codec.STRING
                          .listOf()
                          .optionalFieldOf("useToAimItemPatterns", List.of())
                          .forGetter(ThirdPersonPersistentState.AimingState::useToAimItemPatterns))
                  .apply(instance, ThirdPersonPersistentState.AimingState::new));

  private static final Codec<ThirdPersonPersistentState.PlayerState> PLAYER_STATE_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      enumCodec(PlayerRotationMode.class)
                          .fieldOf("rotationMode")
                          .forGetter(ThirdPersonPersistentState.PlayerState::rotationMode),
                      fieldWithDefault(
                              enumCodec(NormalPlayerRotationMode.class),
                              "normalMode",
                              NormalPlayerRotationMode.INTEREST_POINT)
                          .forGetter(ThirdPersonPersistentState.PlayerState::normalMode),
                      fieldWithDefault(Codec.BOOL, "autoRotateInteracting", true)
                          .forGetter(
                              ThirdPersonPersistentState.PlayerState::autoRotateInteracting),
                      fieldWithDefault(Codec.BOOL, "doNotRotateWhenEating", true)
                          .forGetter(
                              ThirdPersonPersistentState.PlayerState::doNotRotateWhenEating))
                  .apply(instance, ThirdPersonPersistentState.PlayerState::new));

  private static final Codec<ThirdPersonPersistentState.HudState> HUD_STATE_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      enumCodec(ReticleMode.class)
                          .fieldOf("reticle")
                          .forGetter(ThirdPersonPersistentState.HudState::reticle))
                  .apply(instance, ThirdPersonPersistentState.HudState::new));

  private static final Codec<ThirdPersonPersistentState> STATE_CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.intRange(
                              ThirdPersonPersistentState.CURRENT_SCHEMA_VERSION,
                              ThirdPersonPersistentState.CURRENT_SCHEMA_VERSION)
                          .fieldOf("schemaVersion")
                          .forGetter(ThirdPersonPersistentState::schemaVersion),
                      CAMERA_STATE_CODEC
                          .fieldOf("camera")
                          .forGetter(ThirdPersonPersistentState::camera),
                      AIMING_STATE_CODEC
                          .fieldOf("aiming")
                          .forGetter(ThirdPersonPersistentState::aiming),
                      PLAYER_STATE_CODEC
                          .fieldOf("player")
                          .forGetter(ThirdPersonPersistentState::player),
                      HUD_STATE_CODEC.fieldOf("hud").forGetter(ThirdPersonPersistentState::hud))
                  .apply(instance, ThirdPersonPersistentState::new));

  private JsonStateCodec() {}

  static ThirdPersonPersistentState decode(String json) {
    JsonElement jsonElement = JsonParser.parseString(json);
    return requireResult(STATE_CODEC.parse(JsonOps.INSTANCE, jsonElement), "decode");
  }

  static String encode(ThirdPersonPersistentState state) {
    JsonElement jsonElement =
        requireResult(STATE_CODEC.encodeStart(JsonOps.INSTANCE, state), "encode");
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

  private static <T> MapCodec<T> fieldWithDefault(Codec<T> codec, String name, T defaultValue) {
    return codec.optionalFieldOf(name).xmap(value -> value.orElse(defaultValue), Optional::of);
  }

  private static <T> T requireResult(DataResult<T> result, String operation) {
    return result
        .result()
        .orElseThrow(
            () -> new IllegalArgumentException("Failed to " + operation + " state: " + result));
  }
}
