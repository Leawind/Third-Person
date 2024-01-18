package net.leawind.mc.thirdperson.impl.config;


import net.leawind.mc.thirdperson.api.config.AbstractConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public final class DefaultConfig extends AbstractConfig {
	private static final DefaultConfig instance = (DefaultConfig)setToDefault(new DefaultConfig());

	public static AbstractConfig setToDefault (AbstractConfig config) {
		config.is_mod_enable           = true;
		config.lock_camera_pitch_angle = false;
		//
		config.player_rotate_with_camera_when_not_aiming = false;
		config.rotate_to_moving_direction                = true;
		config.auto_rotate_interacting                   = true;
		config.rotate_interacting_type                   = true;
		config.auto_turn_body_drawing_a_bow              = false;
		//
		config.available_distance_count = 16;
		config.camera_distance_min      = 0.5;
		config.camera_distance_max      = 8;
		//--------------------------------------
		config.center_offset_when_flying                = true;
		config.turn_with_camera_when_enter_first_person = true;
		config.camera_ray_trace_length                  = 256;
		//
		config.player_fade_out_enabled = true;
		//
		config.render_crosshair_when_not_aiming = true;
		config.render_crosshair_when_aiming     = true;
		//--------------------------------------
		config.flying_smooth_factor = 0.5;
		//
		config.adjusting_camera_offset_smooth_factor = 0.100;
		config.adjusting_distance_smooth_factor      = 0.100;
		//
		config.normal_smooth_factor_horizon       = 0.500;
		config.normal_smooth_factor_vertical      = 0.500;
		config.normal_camera_offset_smooth_factor = 0.500;
		config.normal_distance_smooth_factor      = 0.640;
		//
		config.aiming_smooth_factor_horizon       = 0.002;
		config.aiming_smooth_factor_vertical      = 0.002;
		config.aiming_camera_offset_smooth_factor = 0.100;
		config.aiming_distance_smooth_factor      = 0.110;
		//--------------------------------------
		config.normal_is_centered   = false;
		config.normal_max_distance  = 2.5;
		config.normal_offset_x      = -0.28;
		config.normal_offset_y      = 0.31;
		config.normal_offset_center = 0.24D;
		//
		config.aiming_is_centered   = false;
		config.aiming_max_distance  = 0.89;
		config.aiming_offset_x      = -0.47;
		config.aiming_offset_y      = -0.09;
		config.aiming_offset_center = 0.48;
		//---------------------------------------
		config.enable_buildin_aim_item_rules = true;
		config.aim_item_rules                = new ArrayList<>();
		config.use_aim_item_rules            = new ArrayList<>();
		return config;
	}

	public static @NotNull DefaultConfig get () {
		return instance;
	}
}
