package net.leawind.mc.thirdperson.config;


import com.google.gson.annotations.Expose;

public class AbstractConfig {
	// ============================================================ //
	@Expose public boolean  is_mod_enable;
	@Expose public boolean  lock_camera_pitch_angle;
	//
	@Expose public boolean  player_rotate_with_camera_when_not_aiming;
	@Expose public boolean  rotate_to_moving_direction;
	@Expose public boolean  auto_rotate_interacting;
	@Expose public boolean  rotate_interacting_type;
	@Expose public boolean  auto_turn_body_drawing_a_bow;
	//
	@Expose public int      available_distance_count;
	@Expose public double   camera_distance_min;
	@Expose public double   camera_distance_max;
	//------------------------------------------------------------
	@Expose public boolean  center_offset_when_flying;
	@Expose public boolean  turn_with_camera_when_enter_first_person;
	@Expose public double   camera_ray_trace_length;
	//
	@Expose public boolean  player_fade_out_enabled;
	//
	@Expose public boolean  render_crosshair_when_not_aiming;
	@Expose public boolean  render_crosshair_when_aiming;
	//------------------------------------------------------------
	@Expose public double   flying_smooth_factor;
	//
	@Expose public double   adjusting_camera_offset_smooth_factor;
	@Expose public double   adjusting_distance_smooth_factor;
	//
	@Expose public double   normal_smooth_factor_horizon;
	@Expose public double   normal_smooth_factor_vertical;
	@Expose public double   normal_camera_offset_smooth_factor;
	@Expose public double   normal_distance_smooth_factor;
	//
	@Expose public double   aiming_smooth_factor_horizon;
	@Expose public double   aiming_smooth_factor_vertical;
	@Expose public double   aiming_camera_offset_smooth_factor;
	@Expose public double   aiming_distance_smooth_factor;
	//------------------------------------------------------------
	@Expose public boolean  normal_is_centered;
	@Expose public double   normal_max_distance;
	@Expose public double   normal_offset_x;
	@Expose public double   normal_offset_y;
	@Expose public double   normal_offset_center;
	//
	@Expose public boolean  aiming_is_centered;
	@Expose public double   aiming_max_distance;
	@Expose public double   aiming_offset_x;
	@Expose public double   aiming_offset_y;
	@Expose public double   aiming_offset_center;
	//-------------------------------------------------------------
	@Expose public String[] aim_items;
	@Expose public String[] use_aim_items;

	public AbstractConfig () {
		setToDefault();
	}

	public void setToDefault () {
		is_mod_enable           = true;
		lock_camera_pitch_angle = false;
		//
		player_rotate_with_camera_when_not_aiming = false;
		rotate_to_moving_direction                = true;
		auto_rotate_interacting                   = true;
		rotate_interacting_type                   = true;
		auto_turn_body_drawing_a_bow              = false;
		//
		available_distance_count = 16;
		camera_distance_min      = 0.5;
		camera_distance_max      = 8;
		//--------------------------------------
		center_offset_when_flying                = true;
		turn_with_camera_when_enter_first_person = true;
		camera_ray_trace_length                  = 256;
		//
		player_fade_out_enabled = true;
		//
		render_crosshair_when_not_aiming = true;
		render_crosshair_when_aiming     = true;
		//--------------------------------------
		flying_smooth_factor = 0.5;
		//
		adjusting_camera_offset_smooth_factor = 0.100;
		adjusting_distance_smooth_factor      = 0.100;
		//
		normal_smooth_factor_horizon       = 0.500;
		normal_smooth_factor_vertical      = 0.500;
		normal_camera_offset_smooth_factor = 0.500;
		normal_distance_smooth_factor      = 0.640;
		//
		aiming_smooth_factor_horizon       = 0.002;
		aiming_smooth_factor_vertical      = 0.002;
		aiming_camera_offset_smooth_factor = 0.100;
		aiming_distance_smooth_factor      = 0.110;
		//--------------------------------------
		normal_is_centered   = false;
		normal_max_distance  = 2.5;
		normal_offset_x      = -0.28;
		normal_offset_y      = 0.31;
		normal_offset_center = 0.24D;
		//
		aiming_is_centered   = false;
		aiming_max_distance  = 0.89;
		aiming_offset_x      = -0.47;
		aiming_offset_y      = -0.09;
		aiming_offset_center = 0.48;
		//---------------------------------------
		aim_items     = new String[]{};
		use_aim_items = new String[]{};
	}
}
