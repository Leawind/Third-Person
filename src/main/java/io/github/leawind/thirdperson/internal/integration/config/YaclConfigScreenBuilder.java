package io.github.leawind.thirdperson.internal.integration.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.core.config.PlayerRotationMode;
import io.github.leawind.thirdperson.internal.core.config.ReticleMode;
import io.github.leawind.thirdperson.internal.core.config.SmoothingPreset;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/// Isolated behind [ConfigScreenManager] so YACL remains optional at runtime.
final class YaclConfigScreenBuilder {
  private YaclConfigScreenBuilder() {}

  static Screen build(Screen parent) {
    var draft = new ConfigDraft(ThirdPersonRuntime.getInstance().config());
    ThirdPersonConfig defaults = ThirdPersonConfig.defaults();

    return YetAnotherConfigLib.createBuilder()
        .title(text("title"))
        .save(draft::save)
        .category(
            ConfigCategory.createBuilder()
                .name(text("category.general"))
                .tooltip(text("category.general.desc"))
                .option(
                    booleanOption(
                        "enabled",
                        defaults.enabled(),
                        () -> draft.value().enabled(),
                        draft::setEnabled))
                .option(
                    enumOption(
                        "smoothing",
                        defaults.camera().smoothing(),
                        () -> draft.value().camera().smoothing(),
                        draft::setSmoothing,
                        SmoothingPreset.class))
                .option(
                    booleanOption(
                        "tight_space",
                        defaults.camera().temporaryFirstPersonInTightSpace(),
                        () -> draft.value().camera().temporaryFirstPersonInTightSpace(),
                        draft::setTemporaryFirstPersonInTightSpace))
                .option(
                    booleanOption(
                        "smart_aiming",
                        defaults.aiming().smartAiming(),
                        () -> draft.value().aiming().smartAiming(),
                        draft::setSmartAiming))
                .option(
                    enumOption(
                        "rotation_mode",
                        defaults.player().rotationMode(),
                        () -> draft.value().player().rotationMode(),
                        draft::setPlayerRotationMode,
                        PlayerRotationMode.class))
                .option(
                    enumOption(
                        "reticle",
                        defaults.hud().reticle(),
                        () -> draft.value().hud().reticle(),
                        draft::setReticleMode,
                        ReticleMode.class))
                .build())
        .category(
            ConfigCategory.createBuilder()
                .name(text("category.normal_camera"))
                .tooltip(text("category.normal_camera.desc"))
                .option(
                    doubleOption(
                        "normal_distance",
                        defaults.camera().normal().distance(),
                        () -> draft.value().camera().normal().distance(),
                        value -> draft.updateNormal(profile -> profileWithDistance(profile, value)),
                        0.0,
                        16.0,
                        0.05))
                .option(
                    doubleOption(
                        "normal_offset_x",
                        defaults.camera().normal().offsetX(),
                        () -> draft.value().camera().normal().offsetX(),
                        value -> draft.updateNormal(profile -> profileWithOffsetX(profile, value)),
                        -1.0,
                        1.0,
                        0.01))
                .option(
                    doubleOption(
                        "normal_offset_y",
                        defaults.camera().normal().offsetY(),
                        () -> draft.value().camera().normal().offsetY(),
                        value -> draft.updateNormal(profile -> profileWithOffsetY(profile, value)),
                        -1.0,
                        1.0,
                        0.01))
                .build())
        .category(
            ConfigCategory.createBuilder()
                .name(text("category.aiming_camera"))
                .tooltip(text("category.aiming_camera.desc"))
                .option(
                    doubleOption(
                        "aiming_distance",
                        defaults.camera().aiming().distance(),
                        () -> draft.value().camera().aiming().distance(),
                        value -> draft.updateAiming(profile -> profileWithDistance(profile, value)),
                        0.0,
                        16.0,
                        0.05))
                .option(
                    doubleOption(
                        "aiming_offset_x",
                        defaults.camera().aiming().offsetX(),
                        () -> draft.value().camera().aiming().offsetX(),
                        value -> draft.updateAiming(profile -> profileWithOffsetX(profile, value)),
                        -1.0,
                        1.0,
                        0.01))
                .option(
                    doubleOption(
                        "aiming_offset_y",
                        defaults.camera().aiming().offsetY(),
                        () -> draft.value().camera().aiming().offsetY(),
                        value -> draft.updateAiming(profile -> profileWithOffsetY(profile, value)),
                        -1.0,
                        1.0,
                        0.01))
                .option(
                    doubleOption(
                        "aiming_fov",
                        defaults.camera().aiming().fovMultiplier(),
                        () -> draft.value().camera().aiming().fovMultiplier(),
                        value -> draft.updateAiming(profile -> profileWithFov(profile, value)),
                        0.25,
                        2.0,
                        0.05))
                .build())
        .build()
        .generateScreen(parent);
  }

  private static Option<Boolean> booleanOption(
      String key, boolean defaultValue, Supplier<Boolean> getter, Consumer<Boolean> setter) {
    return Option.<Boolean>createBuilder()
        .name(text("option." + key))
        .description(OptionDescription.of(text("option." + key + ".desc")))
        .binding(defaultValue, getter, setter)
        .controller(TickBoxControllerBuilder::create)
        .build();
  }

  private static <E extends Enum<E>> Option<E> enumOption(
      String key,
      E defaultValue,
      Supplier<E> getter,
      Consumer<E> setter,
      Class<E> enumClass) {
    return Option.<E>createBuilder()
        .name(text("option." + key))
        .description(OptionDescription.of(text("option." + key + ".desc")))
        .binding(defaultValue, getter, setter)
        .controller(
            option ->
                EnumControllerBuilder.create(option)
                    .enumClass(enumClass)
                    .formatValue(
                        value ->
                            text(
                                "value."
                                    + key
                                    + "."
                                    + value.name().toLowerCase(Locale.ROOT))))
        .build();
  }

  private static Option<Double> doubleOption(
      String key,
      double defaultValue,
      Supplier<Double> getter,
      Consumer<Double> setter,
      double min,
      double max,
      double step) {
    return Option.<Double>createBuilder()
        .name(text("option." + key))
        .description(OptionDescription.of(text("option." + key + ".desc")))
        .binding(defaultValue, getter, setter)
        .controller(
            option ->
                DoubleSliderControllerBuilder.create(option)
                    .range(min, max)
                    .step(step)
                    .formatValue(
                        value ->
                            Component.literal(String.format(Locale.ROOT, "%.2f", value))))
        .build();
  }

  private static ThirdPersonConfig.CameraProfile profileWithDistance(
      ThirdPersonConfig.CameraProfile profile, double distance) {
    return new ThirdPersonConfig.CameraProfile(
        distance, profile.offsetX(), profile.offsetY(), profile.fovMultiplier());
  }

  private static ThirdPersonConfig.CameraProfile profileWithOffsetX(
      ThirdPersonConfig.CameraProfile profile, double offsetX) {
    return new ThirdPersonConfig.CameraProfile(
        profile.distance(), offsetX, profile.offsetY(), profile.fovMultiplier());
  }

  private static ThirdPersonConfig.CameraProfile profileWithOffsetY(
      ThirdPersonConfig.CameraProfile profile, double offsetY) {
    return new ThirdPersonConfig.CameraProfile(
        profile.distance(), profile.offsetX(), offsetY, profile.fovMultiplier());
  }

  private static ThirdPersonConfig.CameraProfile profileWithFov(
      ThirdPersonConfig.CameraProfile profile, double fovMultiplier) {
    return new ThirdPersonConfig.CameraProfile(
        profile.distance(), profile.offsetX(), profile.offsetY(), fovMultiplier);
  }

  private static Component text(String key) {
    return Component.translatable("config." + ThirdPerson.MOD_ID + "." + key);
  }

  private static final class ConfigDraft {
    private ThirdPersonConfig value;

    private ConfigDraft(ThirdPersonConfig value) {
      this.value = value;
    }

    private ThirdPersonConfig value() {
      return value;
    }

    private void save() {
      ThirdPersonRuntime.getInstance().updateConfig(value);
      MinecraftConfigIntegration.saveNow(value);
    }

    private void setEnabled(boolean enabled) {
      value =
          new ThirdPersonConfig(
              value.schemaVersion(),
              enabled,
              value.camera(),
              value.aiming(),
              value.player(),
              value.hud());
    }

    private void setSmoothing(SmoothingPreset smoothing) {
      var camera = value.camera();
      setCamera(
          new ThirdPersonConfig.CameraSettings(
              camera.normal(),
              camera.aiming(),
              smoothing,
              camera.temporaryFirstPersonInTightSpace()));
    }

    private void setTemporaryFirstPersonInTightSpace(boolean enabled) {
      var camera = value.camera();
      setCamera(
          new ThirdPersonConfig.CameraSettings(
              camera.normal(), camera.aiming(), camera.smoothing(), enabled));
    }

    private void setSmartAiming(boolean enabled) {
      value =
          new ThirdPersonConfig(
              value.schemaVersion(),
              value.enabled(),
              value.camera(),
              new ThirdPersonConfig.AimingSettings(enabled),
              value.player(),
              value.hud());
    }

    private void setPlayerRotationMode(PlayerRotationMode mode) {
      value =
          new ThirdPersonConfig(
              value.schemaVersion(),
              value.enabled(),
              value.camera(),
              value.aiming(),
              new ThirdPersonConfig.PlayerSettings(mode),
              value.hud());
    }

    private void setReticleMode(ReticleMode mode) {
      value =
          new ThirdPersonConfig(
              value.schemaVersion(),
              value.enabled(),
              value.camera(),
              value.aiming(),
              value.player(),
              new ThirdPersonConfig.HudSettings(mode));
    }

    private void updateNormal(UnaryOperator<ThirdPersonConfig.CameraProfile> update) {
      var camera = value.camera();
      setCamera(
          new ThirdPersonConfig.CameraSettings(
              update.apply(camera.normal()),
              camera.aiming(),
              camera.smoothing(),
              camera.temporaryFirstPersonInTightSpace()));
    }

    private void updateAiming(UnaryOperator<ThirdPersonConfig.CameraProfile> update) {
      var camera = value.camera();
      setCamera(
          new ThirdPersonConfig.CameraSettings(
              camera.normal(),
              update.apply(camera.aiming()),
              camera.smoothing(),
              camera.temporaryFirstPersonInTightSpace()));
    }

    private void setCamera(ThirdPersonConfig.CameraSettings camera) {
      value =
          new ThirdPersonConfig(
              value.schemaVersion(),
              value.enabled(),
              camera,
              value.aiming(),
              value.player(),
              value.hud());
    }
  }
}
