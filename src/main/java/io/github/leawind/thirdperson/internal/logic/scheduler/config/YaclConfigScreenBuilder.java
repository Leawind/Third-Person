package io.github.leawind.thirdperson.internal.logic.scheduler.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import io.github.leawind.thirdperson.ThirdPerson;
import io.github.leawind.thirdperson.internal.logic.base.RaycastOrigin;
import io.github.leawind.thirdperson.internal.logic.scheduler.SchedulerRuntime;
import io.github.leawind.thirdperson.internal.logic.scheduler.camera.CameraProfileSlot;
import io.github.leawind.thirdperson.internal.logic.scheduler.camera.CameraSettings;
import io.github.leawind.thirdperson.internal.logic.scheduler.hud.ReticleMode;
import io.github.leawind.thirdperson.internal.logic.scheduler.rotation.ConfiguredPlayerRotationMode;
import io.github.leawind.thirdperson.internal.logic.scheduler.rotation.NormalPlayerRotationMode;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/// YACL projection of the subset of runtime state intended for direct user configuration.
final class YaclConfigScreenBuilder {
  private YaclConfigScreenBuilder() {}

  static Screen build(Screen parent) {
    SchedulerRuntime runtime = SchedulerRuntime.getInstance();
    var aiming = runtime.aimingSettings();
    var player = runtime.playerSettings();
    var hud = runtime.hudSettings();
    var camera = runtime.cameraSettings();
    var defaultSmoothing = CameraSettings.defaultSmoothing();

    return YetAnotherConfigLib.createBuilder()
        .title(text("title"))
        .category(
            ConfigCategory.createBuilder()
                .name(text("category.general"))
                .tooltip(text("category.general.desc"))
                .group(
                    group("aiming")
                        .option(
                            booleanOption(
                                "smart_aiming",
                                true,
                                aiming::smartAiming,
                                aiming::setSmartAiming))
                        .build())
                .group(
                    group("hud")
                        .option(
                            enumOption(
                                "reticle",
                                ReticleMode.AUTO,
                                hud::reticleMode,
                                hud::setReticleMode,
                                ReticleMode.class))
                        .build())
                .build())
        .category(
            ConfigCategory.createBuilder()
                .name(text("category.player_rotation"))
                .tooltip(text("category.player_rotation.desc"))
                .group(
                    group("normal_rotation")
                        .option(
                            enumOption(
                                "rotation_mode",
                                ConfiguredPlayerRotationMode.AUTO,
                                player::rotationMode,
                                player::setRotationMode,
                                ConfiguredPlayerRotationMode.class))
                        .option(
                            enumOption(
                                "normal_rotation_mode",
                                NormalPlayerRotationMode.INTEREST_POINT,
                                player::normalMode,
                                player::setNormalMode,
                                NormalPlayerRotationMode.class))
                        .build())
                .group(
                    group("interaction_rotation")
                        .option(
                            booleanOption(
                                "auto_rotate_interacting",
                                true,
                                player::autoRotateInteracting,
                                player::setAutoRotateInteracting))
                        .option(
                            booleanOption(
                                "do_not_rotate_when_eating",
                                true,
                                player::doNotRotateWhenEating,
                                player::setDoNotRotateWhenEating))
                        .option(
                            enumOption(
                                "raycast_origin",
                                RaycastOrigin.CAMERA,
                                player::raycastOrigin,
                                player::setRaycastOrigin,
                                RaycastOrigin.class))
                        .build())
                .build())
        .category(
            ConfigCategory.createBuilder()
                .name(text("category.item_predicates"))
                .tooltip(text("category.item_predicates.desc"))
                .group(
                    itemPatternOption(
                        "hold_to_aim_item_patterns",
                        List.of(),
                        aiming::holdToAimItemPatterns,
                        aiming::setHoldToAimItemPatterns))
                .group(
                    itemPatternOption(
                        "use_to_aim_item_patterns",
                        List.of(),
                        aiming::useToAimItemPatterns,
                        aiming::setUseToAimItemPatterns))
                .build())
        .category(
            ConfigCategory.createBuilder()
                .name(text("category.smoothing"))
                .tooltip(text("category.smoothing.desc"))
                .group(
                    group("rotation")
                        .option(
                            halfLifeOption(
                                "rotation_half_life",
                                defaultSmoothing.rotationHalfLife(),
                                () -> camera.smoothing().rotationHalfLife(),
                                value ->
                                    camera.updateSmoothing(
                                        current -> current.withRotationHalfLife(value))))
                        .option(
                            halfLifeOption(
                                "flying_pivot_half_life",
                                defaultSmoothing.flyingPivotHalfLife(),
                                () -> camera.smoothing().flyingPivotHalfLife(),
                                value ->
                                    camera.updateSmoothing(
                                        current -> current.withFlyingPivotHalfLife(value))))
                        .build())
                .group(
                    group("camera_adjustment")
                        .option(
                            halfLifeOption(
                                "adjusting_offset_half_life",
                                defaultSmoothing.adjustingOffsetHalfLife(),
                                () -> camera.smoothing().adjustingOffsetHalfLife(),
                                value ->
                                    camera.updateSmoothing(
                                        current -> current.withAdjustingOffsetHalfLife(value))))
                        .option(
                            halfLifeOption(
                                "adjusting_distance_half_life",
                                defaultSmoothing.adjustingDistanceHalfLife(),
                                () -> camera.smoothing().adjustingDistanceHalfLife(),
                                value ->
                                    camera.updateSmoothing(
                                        current -> current.withAdjustingDistanceHalfLife(value))))
                        .build())
                .group(
                    smoothingGroup(
                        "normal_camera",
                        "normal",
                        CameraProfileSlot.NORMAL,
                        camera,
                        defaultSmoothing.normal()))
                .group(
                    smoothingGroup(
                        "aiming_camera",
                        "aiming",
                        CameraProfileSlot.AIMING,
                        camera,
                        defaultSmoothing.aiming()))
                .build())
        .category(
            ConfigCategory.createBuilder()
                .name(text("category.camera"))
                .tooltip(text("category.camera.desc"))
                .group(
                    group("normal_camera")
                        .option(
                            doubleOption(
                                "normal_distance",
                                CameraSettings.defaultNormalProfile().distanceFactor(),
                                () -> camera.normalProfile().distanceFactor(),
                                value ->
                                    camera.updateProfile(
                                        CameraProfileSlot.NORMAL,
                                        profile -> profile.withDistanceFactor(value)),
                                0.0,
                                16.0,
                                0.05))
                        .build())
                .group(
                    group("aiming_camera")
                        .option(
                            doubleOption(
                                "aiming_distance",
                                CameraSettings.defaultAimingProfile().distanceFactor(),
                                () -> camera.aimingProfile().distanceFactor(),
                                value ->
                                    camera.updateProfile(
                                        CameraProfileSlot.AIMING,
                                        profile -> profile.withDistanceFactor(value)),
                                0.0,
                                16.0,
                                0.05))
                        .option(
                            doubleOption(
                                "aiming_fov",
                                CameraSettings.defaultAimingProfile().fovMultiplier(),
                                () -> camera.aimingProfile().fovMultiplier(),
                                value ->
                                    camera.updateProfile(
                                        CameraProfileSlot.AIMING,
                                        profile -> profile.withFovMultiplier(value)),
                                0.25,
                                2.0,
                                0.05))
                        .build())
                .build())
        .build()
        .generateScreen(parent);
  }

  private static OptionGroup smoothingGroup(
      String groupKey,
      String optionPrefix,
      CameraProfileSlot slot,
      CameraSettings camera,
      io.github.leawind.thirdperson.internal.logic.scheduler.camera.ModeSmoothing defaults) {
    return group(groupKey)
        .option(
            halfLifeOption(
                optionPrefix + "_pivot_horizontal_half_life",
                defaults.horizontalPivotHalfLife(),
                () -> camera.smoothingFor(slot).horizontalPivotHalfLife(),
                value ->
                    camera.updateModeSmoothing(
                        slot, current -> current.withHorizontalPivotHalfLife(value))))
        .option(
            halfLifeOption(
                optionPrefix + "_pivot_vertical_half_life",
                defaults.verticalPivotHalfLife(),
                () -> camera.smoothingFor(slot).verticalPivotHalfLife(),
                value ->
                    camera.updateModeSmoothing(
                        slot, current -> current.withVerticalPivotHalfLife(value))))
        .option(
            halfLifeOption(
                optionPrefix + "_offset_half_life",
                defaults.offsetHalfLife(),
                () -> camera.smoothingFor(slot).offsetHalfLife(),
                value ->
                    camera.updateModeSmoothing(
                        slot, current -> current.withOffsetHalfLife(value))))
        .option(
            halfLifeOption(
                optionPrefix + "_distance_half_life",
                defaults.distanceHalfLife(),
                () -> camera.smoothingFor(slot).distanceHalfLife(),
                value ->
                    camera.updateModeSmoothing(
                        slot, current -> current.withDistanceHalfLife(value))))
        .option(
            halfLifeOption(
                optionPrefix + "_fov_half_life",
                defaults.fovHalfLife(),
                () -> camera.smoothingFor(slot).fovHalfLife(),
                value ->
                    camera.updateModeSmoothing(
                        slot, current -> current.withFovHalfLife(value))))
        .build();
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

  private static OptionGroup.Builder group(String key) {
    return OptionGroup.createBuilder()
        .name(text("group." + key))
        .description(OptionDescription.of(text("group." + key + ".desc")));
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

  private static Component text(String key) {
    return Component.translatable("config." + ThirdPerson.MOD_ID + "." + key);
  }
}
