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
import io.github.leawind.thirdperson.internal.core.base.RaycastOrigin;
import io.github.leawind.thirdperson.internal.core.schedule.SchedulerRuntime;
import io.github.leawind.thirdperson.internal.core.schedule.camera.CameraProfileSlot;
import io.github.leawind.thirdperson.internal.core.schedule.camera.CameraSettings;
import io.github.leawind.thirdperson.internal.core.schedule.hud.CrosshairMode;
import io.github.leawind.thirdperson.internal.core.schedule.rotation.NormalPlayerRotationMode;
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
    var sound = runtime.soundSettings();
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
                                "smart_aiming", true, aiming::smartAiming, aiming::setSmartAiming))
                        .option(
                            doubleOption(
                                "aiming_fov",
                                CameraSettings.defaultAimingProfile().fovMultiplier(),
                                () -> camera.aimingProfile().fovMultiplier(),
                                value1 ->
                                    camera.updateProfile(
                                        CameraProfileSlot.AIMING,
                                        profile -> profile.withFovMultiplier(value1)),
                                0.25,
                                2.0,
                                0.05))
                        .build())
                .group(
                    group("hud")
                        .option(
                            enumOption(
                                "crosshair",
                                CrosshairMode.ALWAYS,
                                hud::crosshairMode,
                                hud::setCrosshairMode,
                                CrosshairMode.class))
                        .option(
                            booleanOption(
                                "hide_crosshair_when_fall_flying_and_not_aiming",
                                true,
                                hud::hideCrosshairWhenFallFlyingAndNotAiming,
                                hud::setHideCrosshairWhenFallFlyingAndNotAiming))
                        .build())
                .group(
                    group("interaction_rotation")
                        .option(
                            enumOption(
                                "normal_rotation_mode",
                                NormalPlayerRotationMode.INTEREST_POINT,
                                player::normalMode,
                                player::setNormalMode,
                                NormalPlayerRotationMode.class))
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
                .group(
                    group("sound")
                        .option(
                            booleanOption(
                                "center_camera_entity_sounds",
                                false,
                                sound::centerCameraEntitySounds,
                                sound::setCenterCameraEntitySounds))
                        .build())
                .build())
        .category(
            ConfigCategory.createBuilder()
                .name(text("category.pivot"))
                .tooltip(text("category.pivot.desc"))
                .option(
                    halfLifeOption(
                        "flying_pivot_position_half_life",
                        defaultSmoothing.flyingPivotPositionHalfLife(),
                        () -> camera.smoothing().flyingPivotPositionHalfLife(),
                        value ->
                            camera.updateSmoothing(
                                current -> current.withFlyingPivotPositionHalfLife(value))))
                .group(
                    pivotSmoothingGroup(
                        "normal_camera",
                        "normal",
                        CameraProfileSlot.NORMAL,
                        camera,
                        defaultSmoothing.normal()))
                .group(
                    pivotSmoothingGroup(
                        "aiming_camera",
                        "aiming",
                        CameraProfileSlot.AIMING,
                        camera,
                        defaultSmoothing.aiming()))
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
                    compositionSmoothingGroup(
                        "normal_camera",
                        "normal",
                        CameraProfileSlot.NORMAL,
                        camera,
                        defaultSmoothing.normal()))
                .group(
                    compositionSmoothingGroup(
                        "aiming_camera",
                        "aiming",
                        CameraProfileSlot.AIMING,
                        camera,
                        defaultSmoothing.aiming()))
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
        .build()
        .generateScreen(parent);
  }

  private static OptionGroup pivotSmoothingGroup(
      String groupKey,
      String optionPrefix,
      CameraProfileSlot slot,
      CameraSettings camera,
      io.github.leawind.thirdperson.internal.core.schedule.camera.ModeSmoothing defaults) {
    return group(groupKey)
        .option(
            halfLifeOption(
                optionPrefix + "_pivot_position_half_life",
                defaults.pivotPositionHalfLife(),
                () -> camera.smoothingFor(slot).pivotPositionHalfLife(),
                value ->
                    camera.updateModeSmoothing(
                        slot, current -> current.withPivotPositionHalfLife(value))))
        .build();
  }

  private static OptionGroup compositionSmoothingGroup(
      String groupKey,
      String optionPrefix,
      CameraProfileSlot slot,
      CameraSettings camera,
      io.github.leawind.thirdperson.internal.core.schedule.camera.ModeSmoothing defaults) {
    return group(groupKey)
        .option(
            halfLifeOption(
                optionPrefix + "_offset_half_life",
                defaults.offsetHalfLife(),
                () -> camera.smoothingFor(slot).offsetHalfLife(),
                value ->
                    camera.updateModeSmoothing(slot, current -> current.withOffsetHalfLife(value))))
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
                    camera.updateModeSmoothing(slot, current -> current.withFovHalfLife(value))))
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
