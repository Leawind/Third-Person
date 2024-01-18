package net.leawind.mc.thirdperson.fabric.config;


import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.DoubleListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import me.shedaniel.clothconfig2.gui.entries.StringListListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.api.config.ConfigManager;
import net.leawind.mc.thirdperson.impl.config.Config;
import net.leawind.mc.thirdperson.impl.config.DefaultConfig;
import net.leawind.mc.util.api.ItemPattern;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;
import java.util.function.Consumer;

public interface ConfigBuilder {
	static Screen buildConfigScreen (Config config, Screen parent) {
		final me.shedaniel.clothconfig2.api.ConfigBuilder builder = me.shedaniel.clothconfig2.api.ConfigBuilder.create()    //
																											   .setParentScreen(parent)    //
																											   .setTitle(ConfigManager.getText("text.title"))    //
																											   .setSavingRunnable(ThirdPersonMod.getConfigManager()::trySave);
		final ConfigEntryBuilder entryBuilder = builder.entryBuilder();
		DefaultConfig            defaults     = DefaultConfig.get();
		//==============================//
		// Category: general
		//==============================//
		final ConfigCategory CATEGORY_GENERAL = builder.getOrCreateCategory(ConfigManager.getText("option_category.general"));
		{
			CATEGORY_GENERAL.addEntry(buildBooleanEntry("is_mod_enable", defaults.is_mod_enable, config.is_mod_enable, v -> config.is_mod_enable = v, entryBuilder));
			CATEGORY_GENERAL.addEntry(buildBooleanEntry("lock_camera_pitch_angle", defaults.lock_camera_pitch_angle, config.lock_camera_pitch_angle, v -> config.lock_camera_pitch_angle = v, entryBuilder));
			// SubCategory: Player Rotation
			final SubCategoryBuilder SUBCATEGORY_PLAYER_ROTATION = buildSubCategory("player_rotation", entryBuilder);
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry("player_rotate_with_camera_when_not_aiming",
															  defaults.player_rotate_with_camera_when_not_aiming,
															  config.player_rotate_with_camera_when_not_aiming,
															  v -> config.player_rotate_with_camera_when_not_aiming = v,
															  entryBuilder));
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry("rotate_to_moving_direction", defaults.rotate_to_moving_direction, config.rotate_to_moving_direction, v -> config.rotate_to_moving_direction = v, entryBuilder));
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry("auto_rotate_interacting", defaults.auto_rotate_interacting, config.auto_rotate_interacting, v -> config.auto_rotate_interacting = v, entryBuilder));
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry("rotate_interacting_type", defaults.rotate_interacting_type, config.rotate_interacting_type, v -> config.rotate_interacting_type = v, entryBuilder));
			SUBCATEGORY_PLAYER_ROTATION.add(buildBooleanEntry("auto_turn_body_drawing_a_bow", defaults.auto_turn_body_drawing_a_bow, config.auto_turn_body_drawing_a_bow, v -> config.auto_turn_body_drawing_a_bow = v, entryBuilder));
			CATEGORY_GENERAL.addEntry(SUBCATEGORY_PLAYER_ROTATION.build());
			// SubCategory: Camera Distance Adjustment
			final SubCategoryBuilder Subcategory_Camera_Distance_Adjustment = buildSubCategory("camera_distance_adjustment", entryBuilder);
			Subcategory_Camera_Distance_Adjustment.add(buildIntSliderEntry("available_distance_count", 2, 64, defaults.available_distance_count, config.available_distance_count, v -> config.available_distance_count = v, entryBuilder));
			Subcategory_Camera_Distance_Adjustment.add(buildDoubleEntry("camera_distance_min", 0.5, 2.0, defaults.camera_distance_min, config.camera_distance_min, v -> config.camera_distance_min = v, entryBuilder));
			Subcategory_Camera_Distance_Adjustment.add(buildDoubleEntry("camera_distance_max", 2.0, 16D, defaults.camera_distance_max, config.camera_distance_max, v -> config.camera_distance_max = v, entryBuilder));
			CATEGORY_GENERAL.addEntry(Subcategory_Camera_Distance_Adjustment.build());
		}
		//==============================//
		// Category: misc
		//==============================
		final ConfigCategory CATEGORY_MISC = builder.getOrCreateCategory(ConfigManager.getText("option_category.misc"));
		{
			CATEGORY_MISC.addEntry(buildBooleanEntry("center_offset_when_flying", defaults.center_offset_when_flying, config.center_offset_when_flying, v -> config.center_offset_when_flying = v, entryBuilder));
			CATEGORY_MISC.addEntry(buildBooleanEntry("turn_with_camera_when_enter_first_person",
													 defaults.turn_with_camera_when_enter_first_person,
													 config.turn_with_camera_when_enter_first_person,
													 v -> config.turn_with_camera_when_enter_first_person = v,
													 entryBuilder));
			CATEGORY_MISC.addEntry(buildDoubleEntry("camera_ray_trace_length", 32D, 2048D, defaults.camera_ray_trace_length, config.camera_ray_trace_length, v -> config.camera_ray_trace_length = v, entryBuilder));
			// SubCategory: Player Fade out
			final SubCategoryBuilder Subcategory_Player_Fade_Out = buildSubCategory("player_fade_out", entryBuilder);
			Subcategory_Player_Fade_Out.add(buildBooleanEntry("player_fade_out_enabled", defaults.player_fade_out_enabled, config.player_fade_out_enabled, v -> config.player_fade_out_enabled = v, entryBuilder));
			CATEGORY_MISC.addEntry(Subcategory_Player_Fade_Out.build());
			// SubCategory: Crosshair
			final SubCategoryBuilder Subcategory_Crosshair = buildSubCategory("crosshair", entryBuilder);
			Subcategory_Crosshair.add(buildBooleanEntry("render_crosshair_when_not_aiming", defaults.render_crosshair_when_not_aiming, config.render_crosshair_when_not_aiming, v -> config.render_crosshair_when_not_aiming = v, entryBuilder));
			Subcategory_Crosshair.add(buildBooleanEntry("render_crosshair_when_aiming", defaults.render_crosshair_when_aiming, config.render_crosshair_when_aiming, v -> config.render_crosshair_when_aiming = v, entryBuilder));
			CATEGORY_MISC.addEntry(Subcategory_Crosshair.build());
		}
		//==============================//
		// Category: smooth factors
		//==============================//
		final ConfigCategory CATEGORY_SMOOTH_FACTORS = builder.getOrCreateCategory(ConfigManager.getText("option_category.smooth_factors"));
		{
			CATEGORY_MISC.addEntry(buildSmoothFactorEntry("flying_smooth_factor", defaults.flying_smooth_factor, config.flying_smooth_factor, v -> config.flying_smooth_factor = v, entryBuilder));
			// SubCategory: Adjusting Camera
			final SubCategoryBuilder Subcategory_Adjusting_Camera = buildSubCategory("adjusting_camera", entryBuilder);
			Subcategory_Adjusting_Camera.add(buildSmoothFactorEntry("adjusting_camera_offset_smooth_factor",
																	defaults.adjusting_camera_offset_smooth_factor,
																	config.adjusting_camera_offset_smooth_factor,
																	v -> config.adjusting_camera_offset_smooth_factor = v,
																	entryBuilder));
			Subcategory_Adjusting_Camera.add(buildSmoothFactorEntry("adjusting_distance_smooth_factor", defaults.adjusting_distance_smooth_factor, config.adjusting_distance_smooth_factor, v -> config.adjusting_distance_smooth_factor = v, entryBuilder));
			CATEGORY_SMOOTH_FACTORS.addEntry(Subcategory_Adjusting_Camera.build());
			// SubCategory: Normal Mode
			final SubCategoryBuilder SubCategory_Normal_Mode = buildSubCategory("normal_mode", entryBuilder);
			SubCategory_Normal_Mode.add(buildSmoothFactorEntry("smooth_factor_horizon", defaults.normal_smooth_factor_horizon, config.normal_smooth_factor_horizon, v -> config.normal_smooth_factor_horizon = v, entryBuilder));
			SubCategory_Normal_Mode.add(buildSmoothFactorEntry("smooth_factor_vertical", defaults.normal_smooth_factor_vertical, config.normal_smooth_factor_vertical, v -> config.normal_smooth_factor_vertical = v, entryBuilder));
			SubCategory_Normal_Mode.add(buildSmoothFactorEntry("camera_offset_smooth_factor", defaults.normal_camera_offset_smooth_factor, config.normal_camera_offset_smooth_factor, v -> config.normal_camera_offset_smooth_factor = v, entryBuilder));
			SubCategory_Normal_Mode.add(buildSmoothFactorEntry("distance_smooth_factor", defaults.normal_distance_smooth_factor, config.normal_distance_smooth_factor, v -> config.normal_distance_smooth_factor = v, entryBuilder));
			CATEGORY_SMOOTH_FACTORS.addEntry(SubCategory_Normal_Mode.build());
			// SubCategory: Aiming Mode
			final SubCategoryBuilder Subcategory_Aiming_Mode = buildSubCategory("aiming_mode", entryBuilder);
			Subcategory_Aiming_Mode.add(buildSmoothFactorEntry("smooth_factor_horizon", defaults.aiming_smooth_factor_horizon, config.aiming_smooth_factor_horizon, v -> config.aiming_smooth_factor_horizon = v, entryBuilder));
			Subcategory_Aiming_Mode.add(buildSmoothFactorEntry("smooth_factor_vertical", defaults.aiming_smooth_factor_vertical, config.aiming_smooth_factor_vertical, v -> config.aiming_smooth_factor_vertical = v, entryBuilder));
			Subcategory_Aiming_Mode.add(buildSmoothFactorEntry("camera_offset_smooth_factor", defaults.aiming_camera_offset_smooth_factor, config.aiming_camera_offset_smooth_factor, v -> config.aiming_camera_offset_smooth_factor = v, entryBuilder));
			Subcategory_Aiming_Mode.add(buildSmoothFactorEntry("distance_smooth_factor", defaults.aiming_distance_smooth_factor, config.aiming_distance_smooth_factor, v -> config.aiming_distance_smooth_factor = v, entryBuilder));
			CATEGORY_SMOOTH_FACTORS.addEntry(Subcategory_Aiming_Mode.build());
		}
		//==============================//
		// Category: camera offset
		//==============================//
		final ConfigCategory CATEGORY_CAMERA_OFFSET = builder.getOrCreateCategory(ConfigManager.getText("option_category.camera_offset"));
		{
			// SubCategory: Normal Mode
			final SubCategoryBuilder SubCategory_Normal_Mode = buildSubCategory("normal_mode", entryBuilder);
			SubCategory_Normal_Mode.add(buildDoubleEntry("max_distance", 0.5, 32, defaults.normal_max_distance, config.normal_max_distance, v -> config.normal_max_distance = v, entryBuilder));
			SubCategory_Normal_Mode.add(buildDoubleEntry("offset_x", -1, +1, defaults.normal_offset_x, config.normal_offset_x, v -> config.normal_offset_x = v, entryBuilder));
			SubCategory_Normal_Mode.add(buildDoubleEntry("offset_y", -1, +1, defaults.normal_offset_y, config.normal_offset_y, v -> config.normal_offset_y = v, entryBuilder));
			SubCategory_Normal_Mode.add(buildBooleanEntry("is_centered", defaults.normal_is_centered, config.normal_is_centered, v -> config.normal_is_centered = v, entryBuilder));
			SubCategory_Normal_Mode.add(buildDoubleEntry("offset_center", -1, +1, defaults.normal_offset_center, config.normal_offset_center, v -> config.normal_offset_center = v, entryBuilder));
			CATEGORY_CAMERA_OFFSET.addEntry(SubCategory_Normal_Mode.build());
			// SubCategory: Aiming Mode
			final SubCategoryBuilder Subcategory_Aiming_Mode = buildSubCategory("aiming_mode", entryBuilder);
			Subcategory_Aiming_Mode.add(buildDoubleEntry("max_distance", 0.5, 32, defaults.aiming_max_distance, config.aiming_max_distance, v -> config.aiming_max_distance = v, entryBuilder));
			Subcategory_Aiming_Mode.add(buildDoubleEntry("offset_x", -1, +1, defaults.aiming_offset_x, config.aiming_offset_x, v -> config.aiming_offset_x = v, entryBuilder));
			Subcategory_Aiming_Mode.add(buildDoubleEntry("offset_y", -1, +1, defaults.aiming_offset_y, config.aiming_offset_y, v -> config.aiming_offset_y = v, entryBuilder));
			Subcategory_Aiming_Mode.add(buildBooleanEntry("is_centered", defaults.aiming_is_centered, config.aiming_is_centered, v -> config.aiming_is_centered = v, entryBuilder));
			Subcategory_Aiming_Mode.add(buildDoubleEntry("offset_center", -1, +1, defaults.aiming_offset_center, config.aiming_offset_center, v -> config.aiming_offset_center = v, entryBuilder));
			CATEGORY_CAMERA_OFFSET.addEntry(Subcategory_Aiming_Mode.build());
		}
		//==============================//
		// Category: Aiming Check
		//==============================//
		final ConfigCategory CATEGORY_AIMING_CHECK = builder.getOrCreateCategory(ConfigManager.getText("option_category.aiming_check"));
		{
			CATEGORY_AIMING_CHECK.addEntry(buildBooleanEntry("enable_buildin_aim_item_rules", defaults.enable_buildin_aim_item_rules, config.enable_buildin_aim_item_rules, v -> config.enable_buildin_aim_item_rules = v, entryBuilder));
			CATEGORY_AIMING_CHECK.addEntry(buildStringListEntry("aim_item_rules", defaults.aim_item_rules, config.aim_item_rules, v -> config.aim_item_rules = v, entryBuilder));
			CATEGORY_AIMING_CHECK.addEntry(buildStringListEntry("use_aim_item_rules", defaults.use_aim_item_rules, config.use_aim_item_rules, v -> config.use_aim_item_rules = v, entryBuilder));
		}
		return builder.build();
	}

	static StringListListEntry buildStringListEntry (String name, List<String> defaultValue, List<String> currentValue, Consumer<java.util.List<String>> setter, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startStrList(ConfigManager.getText("option." + name), currentValue)
						   .setTooltip(ConfigManager.getText("option." + name + ".desc"))
						   .setSaveConsumer(setter)
						   .setDefaultValue(defaultValue)
						   .setDeleteButtonEnabled(true)
						   .setCellErrorSupplier(ItemPattern::supplyError)
						   .setExpanded(true)
						   .build();
	}

	static SubCategoryBuilder buildSubCategory (String name, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startSubCategory(ConfigManager.getText("option_group." + name)).setExpanded(true).setTooltip(ConfigManager.getText("option_group." + name + ".desc"));
	}

	static BooleanListEntry buildBooleanEntry (String name, boolean defaultValue, boolean currentValue, Consumer<Boolean> setter, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startBooleanToggle(ConfigManager.getText("option." + name), currentValue).setTooltip(ConfigManager.getText("option." + name + ".desc")).setDefaultValue(defaultValue).setSaveConsumer(setter).build();
	}

	static DoubleListEntry buildDoubleEntry (String name, double min, double max, double defaultValue, double currentValue, Consumer<Double> setter, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startDoubleField(ConfigManager.getText("option." + name), currentValue).setTooltip(ConfigManager.getText("option." + name + ".desc")).setDefaultValue(defaultValue).setSaveConsumer(setter).setMin(min).setMax(max).build();
	}

	static IntegerSliderEntry buildIntSliderEntry (String name, int min, int max, int defaultValue, int currentValue, Consumer<Integer> setter, ConfigEntryBuilder entryBuilder) {
		return entryBuilder.startIntSlider(ConfigManager.getText("option." + name), currentValue, min, max).setTooltip(ConfigManager.getText("option." + name + ".desc")).setDefaultValue(defaultValue).setSaveConsumer(setter).build();
	}

	static DoubleListEntry buildSmoothFactorEntry (String name, double defaultValue, double currentValue, Consumer<Double> setter, ConfigEntryBuilder entryBuilder) {
		return buildDoubleEntry(name, 0, 1, defaultValue, currentValue, setter, entryBuilder);
	}
}
