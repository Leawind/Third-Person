package net.leawind.mc.thirdperson.api.config;


import com.google.gson.annotations.Expose;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AbstractConfig {
	// ================================================================================= //
	@Expose public          boolean      is_mod_enable;
	@Expose public          boolean      lock_camera_pitch_angle;
	//------------------------------
	@Expose public          boolean      player_rotate_with_camera_when_not_aiming;
	@Expose public          boolean      rotate_to_moving_direction;
	@Expose public          boolean      auto_rotate_interacting;
	@Expose public          boolean      rotate_interacting_type;
	@Expose public          boolean      auto_turn_body_drawing_a_bow;
	//------------------------------
	@Expose public          int          available_distance_count;
	@Expose public          double       camera_distance_min;
	@Expose public          double       camera_distance_max;
	// ================================================================================= //
	@Expose public          boolean      center_offset_when_flying;
	@Expose public          boolean      turn_with_camera_when_enter_first_person;
	@Expose public          double       camera_ray_trace_length;
	//------------------------------
	@Expose public          boolean      player_fade_out_enabled;
	//------------------------------
	@Expose public          boolean      render_crosshair_when_not_aiming;
	@Expose public          boolean      render_crosshair_when_aiming;
	// ================================================================================= //
	@Expose public          double       flying_smooth_factor;
	//------------------------------
	@Expose public          double       adjusting_camera_offset_smooth_factor;
	@Expose public          double       adjusting_distance_smooth_factor;
	//------------------------------
	@Expose public          double       normal_smooth_factor_horizon;
	@Expose public          double       normal_smooth_factor_vertical;
	@Expose public          double       normal_camera_offset_smooth_factor;
	@Expose public          double       normal_distance_smooth_factor;
	//------------------------------
	@Expose public          double       aiming_smooth_factor_horizon;
	@Expose public          double       aiming_smooth_factor_vertical;
	@Expose public          double       aiming_camera_offset_smooth_factor;
	@Expose public          double       aiming_distance_smooth_factor;
	// ================================================================================= //
	@Expose public          boolean      normal_is_centered;
	@Expose public          double       normal_max_distance;
	@Expose public          double       normal_offset_x;
	@Expose public          double       normal_offset_y;
	@Expose public          double       normal_offset_center;
	//------------------------------
	@Expose public          boolean      aiming_is_centered;
	@Expose public          double       aiming_max_distance;
	@Expose public          double       aiming_offset_x;
	@Expose public          double       aiming_offset_y;
	@Expose public          double       aiming_offset_center;
	// ================================================================================= //
	@Expose public          boolean      enable_buildin_aim_item_rules;
	@Expose @NotNull public List<String> aim_item_rules     = new ArrayList<>();
	@Expose @NotNull public List<String> use_aim_item_rules = new ArrayList<>();
	// ================================================================================= //
}
