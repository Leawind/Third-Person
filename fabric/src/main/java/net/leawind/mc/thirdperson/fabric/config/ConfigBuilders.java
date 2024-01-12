package net.leawind.mc.thirdperson.fabric.config;


import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.DoubleListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.config.ConfigManager;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Consumer;

public class ConfigBuilders {
	public static Screen buildConfigScreen (Config config, Screen parent) {
		final ConfigBuilder builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle(ConfigManager.getText("text.title"))
			.setSavingRunnable(ConfigManager.get()::save);
		final ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		//==============================//
		// Category: general
		//==============================//
		final ConfigCategory CATEGORY_GENERAL = builder.getOrCreateCategory(ConfigManager.getText("option_category.general"));
		{
			CATEGORY_GENERAL.addEntry(buildBooleanEntry("is_mod_enable", true, config.is_mod_enable, v -> config.is_mod_enable = v, config, entryBuilder));
			CATEGORY_GENERAL.addEntry(buildBooleanEntry("lock_camera_pitch_angle", false, config.lock_camera_pitch_angle, v -> config.lock_camera_pitch_angle = v, config, entryBuilder));
			// SubCategory: Player Rotation
			final SubCategoryBuilder SUBCATEGORY_PLAYER_ROTATION = entryBuilder.startSubCategory(ConfigManager.getText("option_group.player_rotation"));
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry("player_rotate_with_camera_when_not_aiming", false, config.player_rotate_with_camera_when_not_aiming, v -> config.player_rotate_with_camera_when_not_aiming = v, config, entryBuilder));
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry("rotate_to_moving_direction", true, config.rotate_to_moving_direction, v -> config.rotate_to_moving_direction = v, config, entryBuilder));
			CATEGORY_GENERAL.addEntry(SUBCATEGORY_PLAYER_ROTATION.build());
			// SubCategory: Player Rotation
			final SubCategoryBuilder SUBCATEGORY_CAMERA_DISTANCE_ADJUSTMENT = entryBuilder.startSubCategory(ConfigManager.getText("option_group.camera_distance_adjustment"));
			SUBCATEGORY_CAMERA_DISTANCE_ADJUSTMENT.add(buildIntSliderEntry("available_distance_count", 16, 2, 64, config.available_distance_count, v -> config.available_distance_count = v, config, entryBuilder));
			SUBCATEGORY_CAMERA_DISTANCE_ADJUSTMENT.add(buildDoubleEntry("camera_distance_min", 1.0, 0.5, 2.0, config.camera_distance_min, v -> config.camera_distance_min = v, config, entryBuilder));
			SUBCATEGORY_CAMERA_DISTANCE_ADJUSTMENT.add(buildDoubleEntry("camera_distance_max", 16D, 2.0, 16D, config.camera_distance_max, v -> config.camera_distance_max = v, config, entryBuilder));
			CATEGORY_GENERAL.addEntry(SUBCATEGORY_CAMERA_DISTANCE_ADJUSTMENT.build());
		}
		//==============================//
		// Category: misc
		//==============================
		final ConfigCategory category_misc = builder.getOrCreateCategory(ConfigManager.getText("option_category.misc"));
		{
			category_misc.addEntry(buildBooleanEntry("center_offset_when_flying", true, config.center_offset_when_flying, v -> config.center_offset_when_flying = v, config, entryBuilder));
			category_misc.addEntry(buildBooleanEntry("turn_with_camera_when_enter_first_person", true, config.turn_with_camera_when_enter_first_person, v -> config.turn_with_camera_when_enter_first_person = v, config, entryBuilder));
			category_misc.addEntry(buildDoubleEntry("camera_ray_trace_length", 256, 32D, 2048D, config.camera_ray_trace_length, v -> config.camera_ray_trace_length = v, config, entryBuilder));
			// SubCategory: Behavior Interecting
			final SubCategoryBuilder SUBCATEGORY_BEHAVIOR_INTERACTING = buildSubCategory("behavior_interacting", entryBuilder);
			SUBCATEGORY_BEHAVIOR_INTERACTING.add(buildBooleanEntry("auto_rotate_interacting", true, config.auto_rotate_interacting, v -> config.auto_rotate_interacting = v, config, entryBuilder));
			SUBCATEGORY_BEHAVIOR_INTERACTING.add(buildBooleanEntry("rotate_interacting_type", true, config.rotate_interacting_type, v -> config.rotate_interacting_type = v, config, entryBuilder));
			category_misc.addEntry(SUBCATEGORY_BEHAVIOR_INTERACTING.build());
			// SubCategory: Player Fade out
			final SubCategoryBuilder SUBCATEGORY_PLAYER_FADE_OUT = buildSubCategory("player_fade_out", entryBuilder);
			SUBCATEGORY_PLAYER_FADE_OUT.add(buildBooleanEntry("player_fade_out_enabled", true, config.player_fade_out_enabled, v -> config.player_fade_out_enabled = v, config, entryBuilder));
			category_misc.addEntry(SUBCATEGORY_PLAYER_FADE_OUT.build());
			// SubCategory: Player Fade out
			final SubCategoryBuilder SUBCATEGORY_CROSSHAIR = buildSubCategory("crosshair", entryBuilder);
			SUBCATEGORY_CROSSHAIR.add(buildBooleanEntry("render_crosshair_when_not_aiming", true, config.render_crosshair_when_not_aiming, v -> config.render_crosshair_when_not_aiming = v, config, entryBuilder));
			category_misc.addEntry(SUBCATEGORY_CROSSHAIR.build());
		}
		//==============================//
		// Category: smooth factors
		//==============================//
		final ConfigCategory CATEGORY_SMOOTH_FACTORS = builder.getOrCreateCategory(ConfigManager.getText("option_category.smooth_factors"));
		{
			category_misc.addEntry(buildSmoothFactorEntry("flying_smooth_factor", 0.5, config.flying_smooth_factor, v -> config.flying_smooth_factor = v, config, entryBuilder));
			// SubCategory: Adjusting Camera
			final SubCategoryBuilder SUBCATEGORY_ADJUSTING_CAMERA = buildSubCategory("adjusting_camera", entryBuilder);
			SUBCATEGORY_ADJUSTING_CAMERA.add(buildSmoothFactorEntry("adjusting_camera_offset_smooth_factor", 0.1, config.adjusting_camera_offset_smooth_factor, v -> config.adjusting_camera_offset_smooth_factor = v, config, entryBuilder));
			SUBCATEGORY_ADJUSTING_CAMERA.add(buildSmoothFactorEntry("adjusting_distance_smooth_factor", 0.1, config.adjusting_distance_smooth_factor, v -> config.adjusting_distance_smooth_factor = v, config, entryBuilder));
			CATEGORY_SMOOTH_FACTORS.addEntry(SUBCATEGORY_ADJUSTING_CAMERA.build());
			// SubCategory: Normal Mode
			final SubCategoryBuilder SUBCATEGORY_NORMAL_MODE = buildSubCategory("normal_mode", entryBuilder);
			SUBCATEGORY_NORMAL_MODE.add(buildSmoothFactorEntry("smooth_factor_horizon", 0.5, config.normal_smooth_factor_horizon, v -> config.normal_smooth_factor_horizon = v, config, entryBuilder));
			SUBCATEGORY_NORMAL_MODE.add(buildSmoothFactorEntry("smooth_factor_vertical", 0.5, config.normal_smooth_factor_vertical, v -> config.normal_smooth_factor_vertical = v, config, entryBuilder));
			SUBCATEGORY_NORMAL_MODE.add(buildSmoothFactorEntry("camera_offset_smooth_factor", 0.5, config.normal_camera_offset_smooth_factor, v -> config.normal_camera_offset_smooth_factor = v, config, entryBuilder));
			SUBCATEGORY_NORMAL_MODE.add(buildSmoothFactorEntry("distance_smooth_factor", 0.64, config.normal_distance_smooth_factor, v -> config.normal_distance_smooth_factor = v, config, entryBuilder));
			CATEGORY_SMOOTH_FACTORS.addEntry(SUBCATEGORY_NORMAL_MODE.build());
			// SubCategory: Aiming Mode
			final SubCategoryBuilder SUBCATEGORY_AIMING_MODE = buildSubCategory("aiming_mode", entryBuilder);
			SUBCATEGORY_AIMING_MODE.add(buildSmoothFactorEntry("smooth_factor_horizon", 0.002, config.aiming_smooth_factor_horizon, v -> config.aiming_smooth_factor_horizon = v, config, entryBuilder));
			SUBCATEGORY_AIMING_MODE.add(buildSmoothFactorEntry("smooth_factor_vertical", 0.002, config.aiming_smooth_factor_vertical, v -> config.aiming_smooth_factor_vertical = v, config, entryBuilder));
			SUBCATEGORY_AIMING_MODE.add(buildSmoothFactorEntry("camera_offset_smooth_factor", 0.1, config.aiming_camera_offset_smooth_factor, v -> config.aiming_camera_offset_smooth_factor = v, config, entryBuilder));
			SUBCATEGORY_AIMING_MODE.add(buildSmoothFactorEntry("distance_smooth_factor", 0.11, config.aiming_distance_smooth_factor, v -> config.aiming_distance_smooth_factor = v, config, entryBuilder));
			CATEGORY_SMOOTH_FACTORS.addEntry(SUBCATEGORY_AIMING_MODE.build());
		}
		//==============================//
		// Category: camera offset
		//==============================//
		final ConfigCategory CATEGORY_CAMERA_OFFSET = builder.getOrCreateCategory(ConfigManager.getText("option_category.camera_offset"));
		{
			// SubCategory: Normal Mode
			final SubCategoryBuilder SUBCATEGORY_NORMAL_MODE = buildSubCategory("normal_mode", entryBuilder);
			SUBCATEGORY_NORMAL_MODE.add(buildDoubleEntry("max_distance", 2.5, 0.5, 32, config.normal_max_distance, v -> config.normal_max_distance = v, config, entryBuilder));
			SUBCATEGORY_NORMAL_MODE.add(buildOffsetRatioEntry("offset_x", -0.28, config.normal_offset_x, v -> config.normal_offset_x = v, config, entryBuilder));
			SUBCATEGORY_NORMAL_MODE.add(buildOffsetRatioEntry("offset_y", 0.31, config.normal_offset_x, v -> config.normal_offset_x = v, config, entryBuilder));
			SUBCATEGORY_NORMAL_MODE.add(buildOffsetRatioEntry("offset_center", 0.24, config.normal_offset_x, v -> config.normal_offset_x = v, config, entryBuilder));
			CATEGORY_CAMERA_OFFSET.addEntry(SUBCATEGORY_NORMAL_MODE.build());
			// SubCategory: Aiming Mode
			final SubCategoryBuilder SUBCATEGORY_AIMING_MODE = buildSubCategory("aiming_mode", entryBuilder);
			SUBCATEGORY_AIMING_MODE.add(buildDoubleEntry("max_distance", 0.89, 0.5, 32, config.aiming_max_distance, v -> config.aiming_max_distance = v, config, entryBuilder));
			SUBCATEGORY_AIMING_MODE.add(buildOffsetRatioEntry("offset_x", -0.47, config.aiming_offset_x, v -> config.aiming_offset_x = v, config, entryBuilder));
			SUBCATEGORY_AIMING_MODE.add(buildOffsetRatioEntry("offset_y", -0.09, config.aiming_offset_x, v -> config.aiming_offset_x = v, config, entryBuilder));
			SUBCATEGORY_AIMING_MODE.add(buildOffsetRatioEntry("offset_center", 0.48, config.aiming_offset_x, v -> config.aiming_offset_x = v, config, entryBuilder));
			CATEGORY_CAMERA_OFFSET.addEntry(SUBCATEGORY_AIMING_MODE.build());
		}
		return builder.build();
	}

	public static SubCategoryBuilder buildSubCategory (String name, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startSubCategory(ConfigManager.getText("option_group." + name))
			.setExpanded(true)
			.setTooltip(ConfigManager.getText("option_group." + name + ".desc"));
	}

	public static BooleanListEntry buildBooleanEntry (String name, boolean defaultValue, boolean currentValue, Consumer<Boolean> setter, Config config, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startBooleanToggle(ConfigManager.getText("option." + name), currentValue)
			.setTooltip(ConfigManager.getText("option." + name + ".desc"))
			.setDefaultValue(defaultValue)
			.setSaveConsumer(setter)
			.build();
	}

	public static DoubleListEntry buildDoubleEntry (String name, double defaultValue, double min, double max, double currentValue, Consumer<Double> setter, Config config, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startDoubleField(ConfigManager.getText("option." + name), currentValue)
			.setTooltip(ConfigManager.getText("option." + name + ".desc"))
			.setDefaultValue(defaultValue)
			.setSaveConsumer(setter)
			.setMin(min)
			.setMax(max)
			.build();
	}

	public static IntegerSliderEntry buildIntSliderEntry (String name, int defaultValue, int min, int max, int currentValue, Consumer<Integer> setter, Config config, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startIntSlider(ConfigManager.getText("option." + name), defaultValue, min, max)
			.setTooltip(ConfigManager.getText("option." + name + ".desc"))
			.setDefaultValue(defaultValue)
			.setSaveConsumer(setter)
			.build();
	}

	public static DoubleListEntry buildSmoothFactorEntry (String name, double defaultValue, double currentValue, Consumer<Double> setter, Config config, ConfigEntryBuilder entryBuilder) {
		return buildDoubleEntry(name, defaultValue, 0, 1, currentValue, setter, config, entryBuilder);
	}

	public static DoubleListEntry buildOffsetRatioEntry (String name, double defaultValue, double currentValue, Consumer<Double> setter, Config config, ConfigEntryBuilder entryBuilder) {
		return buildDoubleEntry(name, defaultValue, -1, 1, currentValue, setter, config, entryBuilder);
	}
}
