package net.leawind.mc.thirdperson.forge.config;


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
			CATEGORY_GENERAL.addEntry(buildBooleanEntry(true, "is_mod_enable", config.is_mod_enable, v -> config.is_mod_enable = v, entryBuilder));
			CATEGORY_GENERAL.addEntry(buildBooleanEntry(false, "lock_camera_pitch_angle", config.lock_camera_pitch_angle, v -> config.lock_camera_pitch_angle = v, entryBuilder));
			// SubCategory: Player Rotation
			final SubCategoryBuilder SUBCATEGORY_PLAYER_ROTATION = buildSubCategory("player_rotation", entryBuilder);
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry(false, "player_rotate_with_camera_when_not_aiming", config.player_rotate_with_camera_when_not_aiming, v -> config.player_rotate_with_camera_when_not_aiming = v, entryBuilder));
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry(true, "rotate_to_moving_direction", config.rotate_to_moving_direction, v -> config.rotate_to_moving_direction = v, entryBuilder));
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry(true, "auto_rotate_interacting", config.auto_rotate_interacting, v -> config.auto_rotate_interacting = v, entryBuilder));
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry(true, "rotate_interacting_type", config.rotate_interacting_type, v -> config.rotate_interacting_type = v, entryBuilder));
			CATEGORY_GENERAL.addEntry(SUBCATEGORY_PLAYER_ROTATION.build());
			// SubCategory: Camera Distance Adjustment
			final SubCategoryBuilder SUBCATEGORY_CAMERA_DISTANCE_ADJUSTMENT = buildSubCategory("camera_distance_adjustment", entryBuilder);
			SUBCATEGORY_CAMERA_DISTANCE_ADJUSTMENT.add(buildIntSliderEntry(16, "available_distance_count", 2, 64, config.available_distance_count, v -> config.available_distance_count = v, entryBuilder));
			SUBCATEGORY_CAMERA_DISTANCE_ADJUSTMENT.add(buildDoubleEntry(1.0, "camera_distance_min", 0.5, 2.0, config.camera_distance_min, v -> config.camera_distance_min = v, entryBuilder));
			SUBCATEGORY_CAMERA_DISTANCE_ADJUSTMENT.add(buildDoubleEntry(16D, "camera_distance_max", 2.0, 16D, config.camera_distance_max, v -> config.camera_distance_max = v, entryBuilder));
			CATEGORY_GENERAL.addEntry(SUBCATEGORY_CAMERA_DISTANCE_ADJUSTMENT.build());
		}
		//==============================//
		// Category: misc
		//==============================
		final ConfigCategory category_misc = builder.getOrCreateCategory(ConfigManager.getText("option_category.misc"));
		{
			category_misc.addEntry(buildBooleanEntry(true, "center_offset_when_flying", config.center_offset_when_flying, v -> config.center_offset_when_flying = v, entryBuilder));
			category_misc.addEntry(buildBooleanEntry(true, "turn_with_camera_when_enter_first_person", config.turn_with_camera_when_enter_first_person, v -> config.turn_with_camera_when_enter_first_person = v, entryBuilder));
			category_misc.addEntry(buildDoubleEntry(256, "camera_ray_trace_length", 32D, 2048D, config.camera_ray_trace_length, v -> config.camera_ray_trace_length = v, entryBuilder));
			// SubCategory: Player Fade out
			final SubCategoryBuilder SUBCATEGORY_PLAYER_FADE_OUT = buildSubCategory("player_fade_out", entryBuilder);
			SUBCATEGORY_PLAYER_FADE_OUT.add(buildBooleanEntry(true, "player_fade_out_enabled", config.player_fade_out_enabled, v -> config.player_fade_out_enabled = v, entryBuilder));
			category_misc.addEntry(SUBCATEGORY_PLAYER_FADE_OUT.build());
			// SubCategory: Crosshair
			final SubCategoryBuilder SUBCATEGORY_CROSSHAIR = buildSubCategory("crosshair", entryBuilder);
			SUBCATEGORY_CROSSHAIR.add(buildBooleanEntry(true, "render_crosshair_when_not_aiming", config.render_crosshair_when_not_aiming, v -> config.render_crosshair_when_not_aiming = v, entryBuilder));
			category_misc.addEntry(SUBCATEGORY_CROSSHAIR.build());
		}
		//==============================//
		// Category: smooth factors
		//==============================//
		final ConfigCategory CATEGORY_SMOOTH_FACTORS = builder.getOrCreateCategory(ConfigManager.getText("option_category.smooth_factors"));
		{
			category_misc.addEntry(buildSmoothFactorEntry(0.5, "flying_smooth_factor", config.flying_smooth_factor, v -> config.flying_smooth_factor = v, entryBuilder));
			// SubCategory: Adjusting Camera
			final SubCategoryBuilder SUBCATEGORY_ADJUSTING_CAMERA = buildSubCategory("adjusting_camera", entryBuilder);
			SUBCATEGORY_ADJUSTING_CAMERA.add(buildSmoothFactorEntry(0.1, "adjusting_camera_offset_smooth_factor", config.adjusting_camera_offset_smooth_factor, v -> config.adjusting_camera_offset_smooth_factor = v, entryBuilder));
			SUBCATEGORY_ADJUSTING_CAMERA.add(buildSmoothFactorEntry(0.1, "adjusting_distance_smooth_factor", config.adjusting_distance_smooth_factor, v -> config.adjusting_distance_smooth_factor = v, entryBuilder));
			CATEGORY_SMOOTH_FACTORS.addEntry(SUBCATEGORY_ADJUSTING_CAMERA.build());
			// SubCategory: Normal Mode
			final SubCategoryBuilder SUBCATEGORY_NORMAL_MODE = buildSubCategory("normal_mode", entryBuilder);
			SUBCATEGORY_NORMAL_MODE.add(buildSmoothFactorEntry(0.500, "smooth_factor_horizon", config.normal_smooth_factor_horizon, v -> config.normal_smooth_factor_horizon = v, entryBuilder));
			SUBCATEGORY_NORMAL_MODE.add(buildSmoothFactorEntry(0.500, "smooth_factor_vertical", config.normal_smooth_factor_vertical, v -> config.normal_smooth_factor_vertical = v, entryBuilder));
			SUBCATEGORY_NORMAL_MODE.add(buildSmoothFactorEntry(0.500, "camera_offset_smooth_factor", config.normal_camera_offset_smooth_factor, v -> config.normal_camera_offset_smooth_factor = v, entryBuilder));
			SUBCATEGORY_NORMAL_MODE.add(buildSmoothFactorEntry(0.640, "distance_smooth_factor", config.normal_distance_smooth_factor, v -> config.normal_distance_smooth_factor = v, entryBuilder));
			CATEGORY_SMOOTH_FACTORS.addEntry(SUBCATEGORY_NORMAL_MODE.build());
			// SubCategory: Aiming Mode
			final SubCategoryBuilder SUBCATEGORY_AIMING_MODE = buildSubCategory("aiming_mode", entryBuilder);
			SUBCATEGORY_AIMING_MODE.add(buildSmoothFactorEntry(0.002, "smooth_factor_horizon", config.aiming_smooth_factor_horizon, v -> config.aiming_smooth_factor_horizon = v, entryBuilder));
			SUBCATEGORY_AIMING_MODE.add(buildSmoothFactorEntry(0.002, "smooth_factor_vertical", config.aiming_smooth_factor_vertical, v -> config.aiming_smooth_factor_vertical = v, entryBuilder));
			SUBCATEGORY_AIMING_MODE.add(buildSmoothFactorEntry(0.100, "camera_offset_smooth_factor", config.aiming_camera_offset_smooth_factor, v -> config.aiming_camera_offset_smooth_factor = v, entryBuilder));
			SUBCATEGORY_AIMING_MODE.add(buildSmoothFactorEntry(0.110, "distance_smooth_factor", config.aiming_distance_smooth_factor, v -> config.aiming_distance_smooth_factor = v, entryBuilder));
			CATEGORY_SMOOTH_FACTORS.addEntry(SUBCATEGORY_AIMING_MODE.build());
		}
		//==============================//
		// Category: camera offset
		//==============================//
		final ConfigCategory CATEGORY_CAMERA_OFFSET = builder.getOrCreateCategory(ConfigManager.getText("option_category.camera_offset"));
		{
			// SubCategory: Normal Mode
			final SubCategoryBuilder SUBCATEGORY_NORMAL_MODE = buildSubCategory("normal_mode", entryBuilder);
			SUBCATEGORY_NORMAL_MODE.add(buildDoubleEntry(+2.50, "max_distance", 0.5, 32, config.normal_max_distance, v -> config.normal_max_distance = v, entryBuilder));
			SUBCATEGORY_NORMAL_MODE.add(buildDoubleEntry(-0.28, "offset_x", -1, +1, config.normal_offset_x, v -> config.normal_offset_x = v, entryBuilder));
			SUBCATEGORY_NORMAL_MODE.add(buildDoubleEntry(+0.31, "offset_y", -1, +1, config.normal_offset_y, v -> config.normal_offset_y = v, entryBuilder));
			SUBCATEGORY_NORMAL_MODE.add(buildBooleanEntry(false, "is_centered", config.normal_is_centered, v -> config.normal_is_centered = v, entryBuilder));
			SUBCATEGORY_NORMAL_MODE.add(buildDoubleEntry(+0.24, "offset_center", -1, +1, config.normal_offset_center, v -> config.normal_offset_center = v, entryBuilder));
			CATEGORY_CAMERA_OFFSET.addEntry(SUBCATEGORY_NORMAL_MODE.build());
			// SubCategory: Aiming Mode
			final SubCategoryBuilder SUBCATEGORY_AIMING_MODE = buildSubCategory("aiming_mode", entryBuilder);
			SUBCATEGORY_AIMING_MODE.add(buildDoubleEntry(+0.89, "max_distance", 0.5, 32, config.aiming_max_distance, v -> config.aiming_max_distance = v, entryBuilder));
			SUBCATEGORY_AIMING_MODE.add(buildDoubleEntry(-0.47, "offset_x", -1, +1, config.aiming_offset_x, v -> config.aiming_offset_x = v, entryBuilder));
			SUBCATEGORY_AIMING_MODE.add(buildDoubleEntry(-0.09, "offset_y", -1, +1, config.aiming_offset_y, v -> config.aiming_offset_y = v, entryBuilder));
			SUBCATEGORY_AIMING_MODE.add(buildBooleanEntry(false, "is_centered", config.aiming_is_centered, v -> config.aiming_is_centered = v, entryBuilder));
			SUBCATEGORY_AIMING_MODE.add(buildDoubleEntry(+0.48, "offset_center", -1, +1, config.aiming_offset_center, v -> config.aiming_offset_center = v, entryBuilder));
			CATEGORY_CAMERA_OFFSET.addEntry(SUBCATEGORY_AIMING_MODE.build());
		}
		return builder.build();
	}

	public static SubCategoryBuilder buildSubCategory (String name, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startSubCategory(ConfigManager.getText("option_group." + name))
			.setExpanded(true)
			.setTooltip(ConfigManager.getText("option_group." + name + ".desc"));
	}

	public static BooleanListEntry buildBooleanEntry (boolean defaultValue, String name, boolean currentValue, Consumer<Boolean> setter, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startBooleanToggle(ConfigManager.getText("option." + name), currentValue)
			.setTooltip(ConfigManager.getText("option." + name + ".desc"))
			.setDefaultValue(defaultValue)
			.setSaveConsumer(setter)
			.build();
	}

	public static DoubleListEntry buildDoubleEntry (double defaultValue, String name, double min, double max, double currentValue, Consumer<Double> setter, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startDoubleField(ConfigManager.getText("option." + name), currentValue)
			.setTooltip(ConfigManager.getText("option." + name + ".desc"))
			.setDefaultValue(defaultValue)
			.setSaveConsumer(setter)
			.setMin(min)
			.setMax(max)
			.build();
	}

	public static IntegerSliderEntry buildIntSliderEntry (int defaultValue, String name, int min, int max, int currentValue, Consumer<Integer> setter, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startIntSlider(ConfigManager.getText("option." + name), currentValue, min, max)
			.setTooltip(ConfigManager.getText("option." + name + ".desc"))
			.setDefaultValue(defaultValue)
			.setSaveConsumer(setter)
			.build();
	}

	public static DoubleListEntry buildSmoothFactorEntry (double defaultValue, String name, double currentValue, Consumer<Double> setter, ConfigEntryBuilder entryBuilder) {
		return buildDoubleEntry(defaultValue, name, 0, 1, currentValue, setter, entryBuilder);
	}
}
