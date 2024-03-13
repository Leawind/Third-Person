package net.leawind.mc.thirdperson.impl.screen;


import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.api.config.Config;
import net.leawind.mc.thirdperson.api.config.ConfigManager;
import net.leawind.mc.thirdperson.api.screen.ConfigScreenBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class YaclConfigScreenBuilder implements ConfigScreenBuilder {
	@Override
	public @NotNull Screen build (@NotNull Config config, @Nullable Screen parent) {
		Config defaults = Config.DEFAULTS;
		return YetAnotherConfigLib.createBuilder() //
								  .title(ConfigManager.getText("text.title")) //
								  .save(ThirdPerson.CONFIG_MANAGER::trySave) //
								  .category(ConfigCategory.createBuilder() //
														  .name(ConfigManager.getText("option_category.general")) //
														  .tooltip(ConfigManager.getText("option_category.general.desc")) //
														  .option(booleanOption("is_mod_enable", defaults.is_mod_enable, () -> config.is_mod_enable, v -> config.is_mod_enable = v).build()) //
														  .option(booleanOption("is_third_person_mode", defaults.is_third_person_mode, () -> config.is_third_person_mode, v -> config.is_third_person_mode = v).build()) //
														  .option(booleanOption("lock_camera_pitch_angle", defaults.lock_camera_pitch_angle, () -> config.lock_camera_pitch_angle, v -> config.lock_camera_pitch_angle = v).build()) //
														  .option(option("config_screen_api", defaults.config_screen_api, () -> config.config_screen_api, v -> config.config_screen_api = v) //
																																															 .controller(opt -> CyclingListControllerBuilder.create(opt) //
																																																											.values(ConfigScreenBuilders.getAvailableBuidlers().keySet()) //
																																																											.formatValue(Component::literal)) //
																																															 .available(ConfigScreenBuilders.getAvailableBuidlers().size() > 1) //
																																															 .build()) //
														  .group(group("player_rotation") //
																						  .option(booleanOption("player_rotate_with_camera_when_not_aiming", defaults.player_rotate_with_camera_when_not_aiming, () -> config.player_rotate_with_camera_when_not_aiming, v -> config.player_rotate_with_camera_when_not_aiming = v).build()) //
																						  .option(booleanOption("rotate_to_moving_direction", defaults.rotate_to_moving_direction, () -> config.rotate_to_moving_direction, v -> config.rotate_to_moving_direction = v).build()) //
																						  .option(booleanOption("auto_rotate_interacting", defaults.auto_rotate_interacting, () -> config.auto_rotate_interacting, v -> config.auto_rotate_interacting = v).build()) //
																						  .option(booleanOption("do_not_rotate_when_eating", defaults.do_not_rotate_when_eating, () -> config.do_not_rotate_when_eating, v -> config.do_not_rotate_when_eating = v).build()) //
																						  .option(booleanOption("rotate_interacting_type", defaults.rotate_interacting_type, () -> config.rotate_interacting_type, v -> config.rotate_interacting_type = v).build()) //
																						  .option(booleanOption("auto_turn_body_drawing_a_bow", defaults.auto_turn_body_drawing_a_bow, () -> config.auto_turn_body_drawing_a_bow, v -> config.auto_turn_body_drawing_a_bow = v).build()) //
																						  .build()) //
														  // SubCategory: Camera Distance Adjustment
														  .group(group("camera_distance_adjustment") //
																									 .option(option("available_distance_count", defaults.available_distance_count, 2, 64, 1, () -> config.available_distance_count, v -> config.available_distance_count = v).build()) //
																									 .option(option("camera_distance_min", defaults.camera_distance_min, 0.5D, 2.0D, 0.1D, () -> config.camera_distance_min, v -> config.camera_distance_min = v).build()) //
																									 .option(option("camera_distance_max", defaults.camera_distance_max, 2D, 16D, 1D, () -> config.camera_distance_max, v -> config.camera_distance_max = v).build()) //
																									 .build()) //
														  .build()) //
								  .category(ConfigCategory.createBuilder() //
														  .name(ConfigManager.getText("option_category.misc")) //
														  .tooltip(ConfigManager.getText("option_category.misc.desc")) //
														  .option(booleanOption("center_offset_when_flying", defaults.center_offset_when_flying, () -> config.center_offset_when_flying, v -> config.center_offset_when_flying = v).build()) //
														  .option(booleanOption("use_camera_pick_in_creative", defaults.use_camera_pick_in_creative, () -> config.use_camera_pick_in_creative, v -> config.use_camera_pick_in_creative = v).build()) //
														  .option(option("camera_ray_trace_length", defaults.camera_ray_trace_length, 32D, 2048D, 1D, () -> config.camera_ray_trace_length, v -> config.camera_ray_trace_length = v).build()) //
														  .option(booleanOption("enable_target_entity_predict", defaults.enable_target_entity_predict, () -> config.enable_target_entity_predict, v -> config.enable_target_entity_predict = v).build()) //
														  .group(group("player_fade_out")//
																						 .option(booleanOption("player_fade_out_enabled", defaults.player_fade_out_enabled, () -> config.player_fade_out_enabled, v -> config.player_fade_out_enabled = v).build()) //
																						 .build()) //
														  .group(group("crosshair") //
																					.option(booleanOption("render_crosshair_when_not_aiming", defaults.render_crosshair_when_not_aiming, () -> config.render_crosshair_when_not_aiming, v -> config.render_crosshair_when_not_aiming = v).build()) //
																					.option(booleanOption("render_crosshair_when_aiming", defaults.render_crosshair_when_aiming, () -> config.render_crosshair_when_aiming, v -> config.render_crosshair_when_aiming = v).build()) //
																					.build()) //
														  .build()) //
								  .category(ConfigCategory.createBuilder() //
														  .name(ConfigManager.getText("option_category.smooth_halflife")) //
														  .tooltip(ConfigManager.getText("option_category.smooth_halflife.desc")) //
														  .option(smoothingOption("flying_smooth_halflife", defaults.flying_smooth_halflife, () -> config.flying_smooth_halflife, v -> config.flying_smooth_halflife = v).build()) //
														  .option(smoothingOption("t2f_transition_halflife", defaults.t2f_transition_halflife, () -> config.t2f_transition_halflife, v -> config.t2f_transition_halflife = v).build()) //
														  .group(group("adjusting_camera") //
																						   .option(smoothingOption("adjusting_camera_offset_smooth_halflife", defaults.adjusting_camera_offset_smooth_halflife, () -> config.adjusting_camera_offset_smooth_halflife, v -> config.adjusting_camera_offset_smooth_halflife = v).build()) //
																						   .option(smoothingOption("adjusting_distance_smooth_halflife", defaults.adjusting_distance_smooth_halflife, () -> config.adjusting_distance_smooth_halflife, v -> config.adjusting_distance_smooth_halflife = v).build()) //
																						   .build()) //
														  .group(group("normal_mode") //
																					  .option(smoothingOption("smooth_halflife_horizon", defaults.normal_smooth_halflife_horizon, () -> config.normal_smooth_halflife_horizon, v -> config.normal_smooth_halflife_horizon = v).build()) //
																					  .option(smoothingOption("smooth_halflife_vertical", defaults.normal_smooth_halflife_vertical, () -> config.normal_smooth_halflife_vertical, v -> config.normal_smooth_halflife_vertical = v).build()) //
																					  .option(smoothingOption("camera_offset_smooth_halflife", defaults.normal_camera_offset_smooth_halflife, () -> config.normal_camera_offset_smooth_halflife, v -> config.normal_camera_offset_smooth_halflife = v).build()) //
																					  .option(smoothingOption("distance_smooth_halflife", defaults.normal_distance_smooth_halflife, () -> config.normal_distance_smooth_halflife, v -> config.normal_distance_smooth_halflife = v).build()) //
																					  .build()) //
														  .group(group("aiming_mode") //
																					  .option(smoothingOption("smooth_halflife_horizon", defaults.aiming_smooth_halflife_horizon, () -> config.aiming_smooth_halflife_horizon, v -> config.aiming_smooth_halflife_horizon = v).build()) //
																					  .option(smoothingOption("smooth_halflife_vertical", defaults.aiming_smooth_halflife_vertical, () -> config.aiming_smooth_halflife_vertical, v -> config.aiming_smooth_halflife_vertical = v).build()) //
																					  .option(smoothingOption("camera_offset_smooth_halflife", defaults.aiming_camera_offset_smooth_halflife, () -> config.aiming_camera_offset_smooth_halflife, v -> config.aiming_camera_offset_smooth_halflife = v).build()) //
																					  .option(smoothingOption("distance_smooth_halflife", defaults.aiming_distance_smooth_halflife, () -> config.aiming_distance_smooth_halflife, v -> config.aiming_distance_smooth_halflife = v).build()) //
																					  .build()) //
														  .build()) //
								  .category(ConfigCategory.createBuilder() //
														  .name(ConfigManager.getText("option_category.camera_offset")) //
														  .tooltip(ConfigManager.getText("option_category.camera_offset.desc")) //
														  .group(group("normal_mode") //
																					  .option(option("max_distance", defaults.normal_max_distance, 0.5D, 32D, 0.25, () -> config.normal_max_distance, v -> config.normal_max_distance = v).build()) //
																					  .option(option("offset_x", defaults.normal_offset_x, -1D, +1D, 0.01, () -> config.normal_offset_x, v -> config.normal_offset_x = v).build()) //
																					  .option(option("offset_y", defaults.normal_offset_y, -1D, +1D, 0.01, () -> config.normal_offset_y, v -> config.normal_offset_y = v).build()) //
																					  .option(booleanOption("is_centered", defaults.normal_is_centered, () -> config.normal_is_centered, v -> config.normal_is_centered = v).build()) //
																					  .option(option("offset_center", defaults.normal_offset_center, -1D, +1D, 0.01, () -> config.normal_offset_center, v -> config.normal_offset_center = v).build()) //
																					  .build()) //
														  .group(group("aiming_mode") //
																					  .option(option("max_distance", defaults.aiming_max_distance, 0.5D, 32D, 0.25, () -> config.aiming_max_distance, v -> config.aiming_max_distance = v).build()) //
																					  .option(option("offset_x", defaults.aiming_offset_x, -1D, +1D, 0.01, () -> config.aiming_offset_x, v -> config.aiming_offset_x = v).build()) //
																					  .option(option("offset_y", defaults.aiming_offset_y, -1D, +1D, 0.01, () -> config.aiming_offset_y, v -> config.aiming_offset_y = v).build()) //
																					  .option(booleanOption("is_centered", defaults.aiming_is_centered, () -> config.aiming_is_centered, v -> config.aiming_is_centered = v).build()) //
																					  .option(option("offset_center", defaults.aiming_offset_center, -1D, +1D, 0.01, () -> config.aiming_offset_center, v -> config.aiming_offset_center = v).build()) //
																					  .build()) //
														  .build()) //
								  .category(ConfigCategory.createBuilder() //
														  .name(ConfigManager.getText("option_category.aiming_check")) //
														  .tooltip(ConfigManager.getText("option_category.aiming_check.desc")) //
														  .option(booleanOption("determine_aim_mode_by_animation", defaults.determine_aim_mode_by_animation, () -> config.determine_aim_mode_by_animation, v -> config.determine_aim_mode_by_animation = v).build()) //
														  .option(itemPatternsOption("hold_to_aim_item_pattern_expressions", defaults.hold_to_aim_item_pattern_expressions, () -> config.hold_to_aim_item_pattern_expressions, v -> config.hold_to_aim_item_pattern_expressions = v).build()) //
														  .option(itemPatternsOption("use_to_aim_item_pattern_expressions", defaults.use_to_aim_item_pattern_expressions, () -> config.use_to_aim_item_pattern_expressions, v -> config.use_to_aim_item_pattern_expressions = v).build()) //
														  .option(itemPatternsOption("use_to_first_person_pattern_expressions", defaults.use_to_first_person_pattern_expressions, () -> config.use_to_first_person_pattern_expressions, v -> config.use_to_first_person_pattern_expressions = v).build()) //
														  .build()) //
								  .build().generateScreen(parent);
	}

	private OptionGroup.Builder group (String name) {
		return OptionGroup.createBuilder() //
						  .name(ConfigManager.getText("option_group." + name)) //
						  .description(OptionDescription.of(ConfigManager.getText("option_group." + name + ".desc")));
	}

	private <T> Option.Builder<T> option (String name, T defaultValue, Supplier<T> getter, Consumer<T> setter) {
		return Option.<T>createBuilder() //
					 .name(ConfigManager.getText("option." + name)) //
					 .description(OptionDescription.of(ConfigManager.getText("option." + name + ".desc"))) //
					 .binding(defaultValue, getter, setter);
	}

	private Option.Builder<Boolean> booleanOption (String name, boolean defaultValue, Supplier<Boolean> getter, Consumer<Boolean> setter) {
		return option(name, defaultValue, getter, setter).controller(TickBoxControllerBuilder::create);
	}

	private Option.Builder<Integer> option (String name, int defaultValue, int min, int max, int step, Supplier<Integer> getter, Consumer<Integer> setter) {
		return option(name, defaultValue, getter, setter).controller(opt -> IntegerSliderControllerBuilder.create(opt).range(min, max).step(step));
	}

	private Option.Builder<Double> option (String name, double defaultValue, double min, double max, double step, Supplier<Double> getter, Consumer<Double> setter) {
		return option(name, defaultValue, getter, setter).controller(opt -> DoubleSliderControllerBuilder.create(opt).range(min, max).step(step));
	}

	private Option.Builder<Double> smoothingOption (String name, double defaultValue, Supplier<Double> getter, Consumer<Double> setter) {
		return option(name, defaultValue, getter, setter).controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0D, 2D).step(0.01));
	}

	private ListOption.Builder<String> itemPatternsOption (String name, List<String> defaultValue, Supplier<List<String>> getter, Consumer<List<String>> setter) {
		return ListOption.<String>createBuilder() //
						 .name(ConfigManager.getText("option." + name)) //
						 .description(OptionDescription.of(ConfigManager.getText("option." + name + ".desc"))) //
						 .binding(defaultValue, getter, setter) //
						 .controller(StringControllerBuilder::create) //
						 .initial("") //
			;
	}
}
