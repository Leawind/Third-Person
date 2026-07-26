package io.github.leawind.thirdperson.internal.integration.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.application.ThirdPersonRuntime;
import io.github.leawind.thirdperson.internal.core.config.PlayerRotationMode;
import io.github.leawind.thirdperson.internal.core.config.ReticleMode;
import io.github.leawind.thirdperson.internal.core.config.ThirdPersonConfig;
import java.util.List;
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
                .name(text("category.item_predicates"))
                .tooltip(text("category.item_predicates.desc"))
                .group(
                    itemPatternOption(
                        "hold_to_aim_item_patterns",
                        defaults.aiming().holdToAimItemPatterns(),
                        () -> draft.value().aiming().holdToAimItemPatterns(),
                        draft::setHoldToAimItemPatterns))
                .group(
                    itemPatternOption(
                        "use_to_aim_item_patterns",
                        defaults.aiming().useToAimItemPatterns(),
                        () -> draft.value().aiming().useToAimItemPatterns(),
                        draft::setUseToAimItemPatterns))
                .group(
                    itemPatternOption(
                        "use_to_first_person_item_patterns",
                        defaults.aiming().useToFirstPersonItemPatterns(),
                        () -> draft.value().aiming().useToFirstPersonItemPatterns(),
                        draft::setUseToFirstPersonItemPatterns))
                .build())
        .category(
            ConfigCategory.createBuilder()
                .name(text("category.smoothing"))
                .tooltip(text("category.smoothing.desc"))
                .option(
                    halfLifeOption(
                        "rotation_half_life",
                        defaults.camera().smoothing().rotationHalfLife(),
                        () -> draft.value().camera().smoothing().rotationHalfLife(),
                        draft::setRotationHalfLife))
                .option(
                    halfLifeOption(
                        "flying_pivot_half_life",
                        defaults.camera().smoothing().flyingPivotHalfLife(),
                        () -> draft.value().camera().smoothing().flyingPivotHalfLife(),
                        draft::setFlyingPivotHalfLife))
                .option(
                    halfLifeOption(
                        "adjusting_offset_half_life",
                        defaults.camera().smoothing().adjustingOffsetHalfLife(),
                        () -> draft.value().camera().smoothing().adjustingOffsetHalfLife(),
                        draft::setAdjustingOffsetHalfLife))
                .option(
                    halfLifeOption(
                        "adjusting_distance_half_life",
                        defaults.camera().smoothing().adjustingDistanceHalfLife(),
                        () -> draft.value().camera().smoothing().adjustingDistanceHalfLife(),
                        draft::setAdjustingDistanceHalfLife))
                .option(
                    halfLifeOption(
                        "normal_pivot_horizontal_half_life",
                        defaults.camera().smoothing().normal().horizontalPivotHalfLife(),
                        () -> draft.value().camera().smoothing().normal().horizontalPivotHalfLife(),
                        draft::setNormalHorizontalPivotHalfLife))
                .option(
                    halfLifeOption(
                        "normal_pivot_vertical_half_life",
                        defaults.camera().smoothing().normal().verticalPivotHalfLife(),
                        () -> draft.value().camera().smoothing().normal().verticalPivotHalfLife(),
                        draft::setNormalVerticalPivotHalfLife))
                .option(
                    halfLifeOption(
                        "normal_offset_half_life",
                        defaults.camera().smoothing().normal().offsetHalfLife(),
                        () -> draft.value().camera().smoothing().normal().offsetHalfLife(),
                        draft::setNormalOffsetHalfLife))
                .option(
                    halfLifeOption(
                        "normal_distance_half_life",
                        defaults.camera().smoothing().normal().distanceHalfLife(),
                        () -> draft.value().camera().smoothing().normal().distanceHalfLife(),
                        draft::setNormalDistanceHalfLife))
                .option(
                    halfLifeOption(
                        "normal_fov_half_life",
                        defaults.camera().smoothing().normal().fovHalfLife(),
                        () -> draft.value().camera().smoothing().normal().fovHalfLife(),
                        draft::setNormalFovHalfLife))
                .option(
                    halfLifeOption(
                        "aiming_pivot_horizontal_half_life",
                        defaults.camera().smoothing().aiming().horizontalPivotHalfLife(),
                        () -> draft.value().camera().smoothing().aiming().horizontalPivotHalfLife(),
                        draft::setAimingHorizontalPivotHalfLife))
                .option(
                    halfLifeOption(
                        "aiming_pivot_vertical_half_life",
                        defaults.camera().smoothing().aiming().verticalPivotHalfLife(),
                        () -> draft.value().camera().smoothing().aiming().verticalPivotHalfLife(),
                        draft::setAimingVerticalPivotHalfLife))
                .option(
                    halfLifeOption(
                        "aiming_offset_half_life",
                        defaults.camera().smoothing().aiming().offsetHalfLife(),
                        () -> draft.value().camera().smoothing().aiming().offsetHalfLife(),
                        draft::setAimingOffsetHalfLife))
                .option(
                    halfLifeOption(
                        "aiming_distance_half_life",
                        defaults.camera().smoothing().aiming().distanceHalfLife(),
                        () -> draft.value().camera().smoothing().aiming().distanceHalfLife(),
                        draft::setAimingDistanceHalfLife))
                .option(
                    halfLifeOption(
                        "aiming_fov_half_life",
                        defaults.camera().smoothing().aiming().fovHalfLife(),
                        () -> draft.value().camera().smoothing().aiming().fovHalfLife(),
                        draft::setAimingFovHalfLife))
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
                .option(
                    doubleOption(
                        "normal_centered_offset_y",
                        defaults.camera().normal().centeredOffsetY(),
                        () -> draft.value().camera().normal().centeredOffsetY(),
                        value ->
                            draft.updateNormal(
                                profile -> profileWithCenteredOffsetY(profile, value)),
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
                        "aiming_centered_offset_y",
                        defaults.camera().aiming().centeredOffsetY(),
                        () -> draft.value().camera().aiming().centeredOffsetY(),
                        value ->
                            draft.updateAiming(
                                profile -> profileWithCenteredOffsetY(profile, value)),
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
      String key, E defaultValue, Supplier<E> getter, Consumer<E> setter, Class<E> enumClass) {
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
                            text("value." + key + "." + value.name().toLowerCase(Locale.ROOT))))
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
    return numberOption(key, defaultValue, getter, setter, min, max, step, 2);
  }

  private static Option<Double> halfLifeOption(
      String key, double defaultValue, Supplier<Double> getter, Consumer<Double> setter) {
    return numberOption(key, defaultValue, getter, setter, 0.0, 0.2, 0.001, 4);
  }

  private static ListOption<String> itemPatternOption(
      String key,
      List<String> defaultValue,
      Supplier<List<String>> getter,
      Consumer<List<String>> setter) {
    return ListOption.<String>createBuilder()
        .name(text("option." + key))
        .description(OptionDescription.of(text("option." + key + ".desc")))
        .binding(defaultValue, getter, setter)
        .controller(StringControllerBuilder::create)
        .initial("")
        .maximumNumberOfEntries(1024)
        .insertEntriesAtEnd(true)
        .collapsed(false)
        .build();
  }

  private static Option<Double> numberOption(
      String key,
      double defaultValue,
      Supplier<Double> getter,
      Consumer<Double> setter,
      double min,
      double max,
      double step,
      int decimalPlaces) {
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
                            Component.literal(
                                String.format(Locale.ROOT, "%." + decimalPlaces + "f", value))))
        .build();
  }

  private static ThirdPersonConfig.CameraProfile profileWithDistance(
      ThirdPersonConfig.CameraProfile profile, double distance) {
    return new ThirdPersonConfig.CameraProfile(
        distance,
        profile.offsetX(),
        profile.offsetY(),
        profile.centeredOffsetY(),
        profile.fovMultiplier(),
        profile.centered());
  }

  private static ThirdPersonConfig.CameraProfile profileWithOffsetX(
      ThirdPersonConfig.CameraProfile profile, double offsetX) {
    return new ThirdPersonConfig.CameraProfile(
        profile.distance(),
        offsetX,
        profile.offsetY(),
        profile.centeredOffsetY(),
        profile.fovMultiplier(),
        profile.centered());
  }

  private static ThirdPersonConfig.CameraProfile profileWithOffsetY(
      ThirdPersonConfig.CameraProfile profile, double offsetY) {
    return new ThirdPersonConfig.CameraProfile(
        profile.distance(),
        profile.offsetX(),
        offsetY,
        profile.centeredOffsetY(),
        profile.fovMultiplier(),
        profile.centered());
  }

  private static ThirdPersonConfig.CameraProfile profileWithCenteredOffsetY(
      ThirdPersonConfig.CameraProfile profile, double centeredOffsetY) {
    return new ThirdPersonConfig.CameraProfile(
        profile.distance(),
        profile.offsetX(),
        profile.offsetY(),
        centeredOffsetY,
        profile.fovMultiplier(),
        profile.centered());
  }

  private static ThirdPersonConfig.CameraProfile profileWithFov(
      ThirdPersonConfig.CameraProfile profile, double fovMultiplier) {
    return new ThirdPersonConfig.CameraProfile(
        profile.distance(),
        profile.offsetX(),
        profile.offsetY(),
        profile.centeredOffsetY(),
        fovMultiplier,
        profile.centered());
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

    private void setTemporaryFirstPersonInTightSpace(boolean enabled) {
      var camera = value.camera();
      setCamera(
          new ThirdPersonConfig.CameraSettings(
              camera.normal(), camera.aiming(), camera.smoothing(), enabled));
    }

    private void setSmartAiming(boolean enabled) {
      var aiming = value.aiming();
      setAiming(
          new ThirdPersonConfig.AimingSettings(
              enabled,
              aiming.holdToAimItemPatterns(),
              aiming.useToAimItemPatterns(),
              aiming.useToFirstPersonItemPatterns()));
    }

    private void setHoldToAimItemPatterns(List<String> patterns) {
      var aiming = value.aiming();
      setAiming(
          new ThirdPersonConfig.AimingSettings(
              aiming.smartAiming(),
              patterns,
              aiming.useToAimItemPatterns(),
              aiming.useToFirstPersonItemPatterns()));
    }

    private void setUseToAimItemPatterns(List<String> patterns) {
      var aiming = value.aiming();
      setAiming(
          new ThirdPersonConfig.AimingSettings(
              aiming.smartAiming(),
              aiming.holdToAimItemPatterns(),
              patterns,
              aiming.useToFirstPersonItemPatterns()));
    }

    private void setUseToFirstPersonItemPatterns(List<String> patterns) {
      var aiming = value.aiming();
      setAiming(
          new ThirdPersonConfig.AimingSettings(
              aiming.smartAiming(),
              aiming.holdToAimItemPatterns(),
              aiming.useToAimItemPatterns(),
              patterns));
    }

    private void setAiming(ThirdPersonConfig.AimingSettings aiming) {
      value =
          new ThirdPersonConfig(
              value.schemaVersion(),
              value.enabled(),
              value.camera(),
              aiming,
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

    private void setRotationHalfLife(double halfLife) {
      updateSmoothing(
          smoothing ->
              new ThirdPersonConfig.SmoothingSettings(
                  halfLife,
                  smoothing.flyingPivotHalfLife(),
                  smoothing.adjustingOffsetHalfLife(),
                  smoothing.adjustingDistanceHalfLife(),
                  smoothing.normal(),
                  smoothing.aiming()));
    }

    private void setFlyingPivotHalfLife(double halfLife) {
      updateSmoothing(
          smoothing ->
              new ThirdPersonConfig.SmoothingSettings(
                  smoothing.rotationHalfLife(),
                  halfLife,
                  smoothing.adjustingOffsetHalfLife(),
                  smoothing.adjustingDistanceHalfLife(),
                  smoothing.normal(),
                  smoothing.aiming()));
    }

    private void setAdjustingOffsetHalfLife(double halfLife) {
      updateSmoothing(
          smoothing ->
              new ThirdPersonConfig.SmoothingSettings(
                  smoothing.rotationHalfLife(),
                  smoothing.flyingPivotHalfLife(),
                  halfLife,
                  smoothing.adjustingDistanceHalfLife(),
                  smoothing.normal(),
                  smoothing.aiming()));
    }

    private void setAdjustingDistanceHalfLife(double halfLife) {
      updateSmoothing(
          smoothing ->
              new ThirdPersonConfig.SmoothingSettings(
                  smoothing.rotationHalfLife(),
                  smoothing.flyingPivotHalfLife(),
                  smoothing.adjustingOffsetHalfLife(),
                  halfLife,
                  smoothing.normal(),
                  smoothing.aiming()));
    }

    private void setNormalHorizontalPivotHalfLife(double halfLife) {
      updateNormalSmoothing(
          smoothing ->
              new ThirdPersonConfig.ModeSmoothing(
                  halfLife,
                  smoothing.verticalPivotHalfLife(),
                  smoothing.offsetHalfLife(),
                  smoothing.distanceHalfLife(),
                  smoothing.fovHalfLife()));
    }

    private void setNormalVerticalPivotHalfLife(double halfLife) {
      updateNormalSmoothing(
          smoothing ->
              new ThirdPersonConfig.ModeSmoothing(
                  smoothing.horizontalPivotHalfLife(),
                  halfLife,
                  smoothing.offsetHalfLife(),
                  smoothing.distanceHalfLife(),
                  smoothing.fovHalfLife()));
    }

    private void setNormalOffsetHalfLife(double halfLife) {
      updateNormalSmoothing(
          smoothing ->
              new ThirdPersonConfig.ModeSmoothing(
                  smoothing.horizontalPivotHalfLife(),
                  smoothing.verticalPivotHalfLife(),
                  halfLife,
                  smoothing.distanceHalfLife(),
                  smoothing.fovHalfLife()));
    }

    private void setNormalDistanceHalfLife(double halfLife) {
      updateNormalSmoothing(
          smoothing ->
              new ThirdPersonConfig.ModeSmoothing(
                  smoothing.horizontalPivotHalfLife(),
                  smoothing.verticalPivotHalfLife(),
                  smoothing.offsetHalfLife(),
                  halfLife,
                  smoothing.fovHalfLife()));
    }

    private void setNormalFovHalfLife(double halfLife) {
      updateNormalSmoothing(
          smoothing ->
              new ThirdPersonConfig.ModeSmoothing(
                  smoothing.horizontalPivotHalfLife(),
                  smoothing.verticalPivotHalfLife(),
                  smoothing.offsetHalfLife(),
                  smoothing.distanceHalfLife(),
                  halfLife));
    }

    private void setAimingHorizontalPivotHalfLife(double halfLife) {
      updateAimingSmoothing(
          smoothing ->
              new ThirdPersonConfig.ModeSmoothing(
                  halfLife,
                  smoothing.verticalPivotHalfLife(),
                  smoothing.offsetHalfLife(),
                  smoothing.distanceHalfLife(),
                  smoothing.fovHalfLife()));
    }

    private void setAimingVerticalPivotHalfLife(double halfLife) {
      updateAimingSmoothing(
          smoothing ->
              new ThirdPersonConfig.ModeSmoothing(
                  smoothing.horizontalPivotHalfLife(),
                  halfLife,
                  smoothing.offsetHalfLife(),
                  smoothing.distanceHalfLife(),
                  smoothing.fovHalfLife()));
    }

    private void setAimingOffsetHalfLife(double halfLife) {
      updateAimingSmoothing(
          smoothing ->
              new ThirdPersonConfig.ModeSmoothing(
                  smoothing.horizontalPivotHalfLife(),
                  smoothing.verticalPivotHalfLife(),
                  halfLife,
                  smoothing.distanceHalfLife(),
                  smoothing.fovHalfLife()));
    }

    private void setAimingDistanceHalfLife(double halfLife) {
      updateAimingSmoothing(
          smoothing ->
              new ThirdPersonConfig.ModeSmoothing(
                  smoothing.horizontalPivotHalfLife(),
                  smoothing.verticalPivotHalfLife(),
                  smoothing.offsetHalfLife(),
                  halfLife,
                  smoothing.fovHalfLife()));
    }

    private void setAimingFovHalfLife(double halfLife) {
      updateAimingSmoothing(
          smoothing ->
              new ThirdPersonConfig.ModeSmoothing(
                  smoothing.horizontalPivotHalfLife(),
                  smoothing.verticalPivotHalfLife(),
                  smoothing.offsetHalfLife(),
                  smoothing.distanceHalfLife(),
                  halfLife));
    }

    private void updateNormalSmoothing(UnaryOperator<ThirdPersonConfig.ModeSmoothing> update) {
      updateSmoothing(
          smoothing ->
              new ThirdPersonConfig.SmoothingSettings(
                  smoothing.rotationHalfLife(),
                  smoothing.flyingPivotHalfLife(),
                  smoothing.adjustingOffsetHalfLife(),
                  smoothing.adjustingDistanceHalfLife(),
                  update.apply(smoothing.normal()),
                  smoothing.aiming()));
    }

    private void updateAimingSmoothing(UnaryOperator<ThirdPersonConfig.ModeSmoothing> update) {
      updateSmoothing(
          smoothing ->
              new ThirdPersonConfig.SmoothingSettings(
                  smoothing.rotationHalfLife(),
                  smoothing.flyingPivotHalfLife(),
                  smoothing.adjustingOffsetHalfLife(),
                  smoothing.adjustingDistanceHalfLife(),
                  smoothing.normal(),
                  update.apply(smoothing.aiming())));
    }

    private void updateSmoothing(UnaryOperator<ThirdPersonConfig.SmoothingSettings> update) {
      var camera = value.camera();
      setCamera(
          new ThirdPersonConfig.CameraSettings(
              camera.normal(),
              camera.aiming(),
              update.apply(camera.smoothing()),
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
