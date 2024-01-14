package net.leawind.mc.thirdperson.config;


import com.google.gson.annotations.Expose;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.math.monolist.StaticMonoList;

/**
 * 模组配置
 */
public class Config {
	// ============================================================ //
	@Expose public boolean            is_mod_enable                             = true;
	@Expose public boolean            lock_camera_pitch_angle                   = false;
	//
	@Expose public boolean            player_rotate_with_camera_when_not_aiming = false;
	@Expose public boolean            rotate_to_moving_direction                = true;
	@Expose public boolean            auto_rotate_interacting                   = true;
	@Expose public boolean            rotate_interacting_type                   = true;
	@Expose public boolean            auto_turn_body_drawing_a_bow              = false;
	//
	@Expose public int                available_distance_count                  = 16;
	@Expose public double             camera_distance_min                       = 0.5;
	@Expose public double             camera_distance_max                       = 8;
	//-------------------------------------------------------------
	@Expose public boolean            center_offset_when_flying                 = true;
	@Expose public boolean            turn_with_camera_when_enter_first_person  = true;
	@Expose public double             camera_ray_trace_length                   = 256;
	//
	@Expose public boolean            player_fade_out_enabled                   = true;
	//
	@Expose public boolean            render_crosshair_when_not_aiming          = true;
	@Expose public boolean            render_crosshair_when_aiming              = true;
	//-------------------------------------------------------------
	@Expose public double             flying_smooth_factor                      = 0.5;
	//
	@Expose public double             adjusting_camera_offset_smooth_factor     = 0.100;
	@Expose public double             adjusting_distance_smooth_factor          = 0.100;
	//
	@Expose public double             normal_smooth_factor_horizon              = 0.500;
	@Expose public double             normal_smooth_factor_vertical             = 0.500;
	@Expose public double             normal_camera_offset_smooth_factor        = 0.500;
	@Expose public double             normal_distance_smooth_factor             = 0.640;
	//
	@Expose public double             aiming_smooth_factor_horizon              = 0.002;
	@Expose public double             aiming_smooth_factor_vertical             = 0.002;
	@Expose public double             aiming_camera_offset_smooth_factor        = 0.100;
	@Expose public double             aiming_distance_smooth_factor             = 0.110;
	//-------------------------------------------------------------
	@Expose public boolean            normal_is_centered                        = false;
	@Expose public double             normal_max_distance                       = 2.5;
	@Expose public double             normal_offset_x                           = -0.28;
	@Expose public double             normal_offset_y                           = 0.31;
	@Expose public double             normal_offset_center                      = 0.24D;
	//
	@Expose public boolean            aiming_is_centered                        = false;
	@Expose public double             aiming_max_distance                       = 0.89;
	@Expose public double             aiming_offset_x                           = -0.47;
	@Expose public double             aiming_offset_y                           = -0.09;
	@Expose public double             aiming_offset_center                      = 0.48;
	// ============================================================ //
	public         StaticMonoList     distanceMonoList;
	// ============================================================ //
	public         CameraOffsetScheme cameraOffsetScheme                        = new CameraOffsetScheme(this);

	/**
	 * 配置项发生变化时更新
	 */
	public void update () {
		updateDistancesMonoList();
	}

	/**
	 * 更新相机到玩家的距离的可调挡位们
	 */
	public void updateDistancesMonoList () {
		distanceMonoList = StaticMonoList.of(available_distance_count, camera_distance_min, camera_distance_max, i -> i * i, Math::sqrt);
	}
}
