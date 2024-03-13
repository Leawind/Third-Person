package net.leawind.mc.thirdperson.api.config;


import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置的抽象类
 * <p>
 * 定义所有配置选项及其默认值
 */
abstract class AbstractConfig {
	// ================================================================================General //
	@Expose public          boolean      is_mod_enable                             = true;
	@Expose public          boolean      is_third_person_mode                      = true;
	@Expose public          boolean      lock_camera_pitch_angle                   = false;
	//------------------------------Player Rotation
	@Expose public          boolean      player_rotate_with_camera_when_not_aiming = false;
	@Expose public          boolean      rotate_to_moving_direction                = true;
	@Expose public          boolean      auto_rotate_interacting                   = true;
	@Expose public          boolean      do_not_rotate_when_eating                 = true;
	@Expose public          boolean      rotate_interacting_type                   = true;
	@Expose public          boolean      auto_turn_body_drawing_a_bow              = false;
	//------------------------------Camera Distance Adjustment
	@Expose public          int          available_distance_count                  = 16;
	@Expose public          double       camera_distance_min                       = 0.5;
	@Expose public          double       camera_distance_max                       = 8;
	// ================================================================================Miscellaneous //
	@Expose public          String       config_screen_api                         = "YACL";
	@Expose public          boolean      center_offset_when_flying                 = true;
	@Expose public          boolean      use_camera_pick_in_creative               = true;
	@Expose public          double       camera_ray_trace_length                   = 256;
	@Expose public          boolean      enable_target_entity_predict              = true;
	//------------------------------Player Fade out
	@Expose public          boolean      player_fade_out_enabled                   = true;
	//------------------------------Crosshair
	@Expose public          boolean      render_crosshair_when_not_aiming          = true;
	@Expose public          boolean      render_crosshair_when_aiming              = true;
	// =================================================================================Smooth Factors //
	@Expose public          double       flying_smooth_halflife                    = 0.20;
	@Expose public          double       t2f_transition_halflife                   = 0.1;
	//------------------------------Adjusting Camera
	@Expose public          double       adjusting_camera_offset_smooth_halflife   = 0.04;
	@Expose public          double       adjusting_distance_smooth_halflife        = 0.08;
	//------------------------------Normal Mode
	@Expose public          double       normal_smooth_halflife_horizon            = 0.25;
	@Expose public          double       normal_smooth_halflife_vertical           = 0.20;
	@Expose public          double       normal_camera_offset_smooth_halflife      = 0.08;
	@Expose public          double       normal_distance_smooth_halflife           = 0.72;
	//------------------------------Aiming Mode
	@Expose public          double       aiming_smooth_halflife_horizon            = 0.05;
	@Expose public          double       aiming_smooth_halflife_vertical           = 0.05;
	@Expose public          double       aiming_camera_offset_smooth_halflife      = 0.03;
	@Expose public          double       aiming_distance_smooth_halflife           = 0.04;
	// =================================================================================Camera Offset //
	//------------------------------Normal Mode
	@Expose public          boolean      normal_is_centered                        = false;
	@Expose public          double       normal_max_distance                       = 2.5;
	@Expose public          double       normal_offset_x                           = -0.28;
	@Expose public          double       normal_offset_y                           = 0.31;
	@Expose public          double       normal_offset_center                      = 0.24D;
	//------------------------------Aiming Mode
	@Expose public          boolean      aiming_is_centered                        = false;
	@Expose public          double       aiming_max_distance                       = 0.89;
	@Expose public          double       aiming_offset_x                           = -0.47;
	@Expose public          double       aiming_offset_y                           = -0.09;
	@Expose public          double       aiming_offset_center                      = 0.48;
	@Expose public          boolean      determine_aim_mode_by_animation           = true;
	@Expose @NotNull public List<String> hold_to_aim_item_pattern_expressions      = new ArrayList<>();
	@Expose @NotNull public List<String> use_to_aim_item_pattern_expressions       = new ArrayList<>();
	@Expose @NotNull public List<String> use_to_first_person_pattern_expressions   = new ArrayList<>();
}
