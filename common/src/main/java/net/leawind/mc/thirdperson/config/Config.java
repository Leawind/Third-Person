package net.leawind.mc.thirdperson.config;


import com.google.gson.annotations.Expose;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.util.monolist.StaticMonoList;
import org.jetbrains.annotations.NotNull;

/**
 * 模组配置项
 * <p>
 * 普通配置项可由用户在配置屏幕中调整，程序可以直接使用其值。
 * <p>
 * 也有一些配置项比较特殊。以相机离玩家的距离的调整范围为例，以下3个选项可以由玩家直接调整：
 * <p>
 * available_distance_count, camera_distance_min, camera_distance_max
 * <p>
 * 但让程序直接使用这些值就有点不方便了。需要先根据这些值生成一个 MonoList 对象，再让程序直接访问这个 MonoList对象。
 * <p>
 * 如果每次使用时都实例化一个新的MonoList对象就太浪费了，所以最好在修改配置后立即生成它并记录在Config的某一字段中（distanceMonoList）
 * <p>
 * 所以原本的3个选项不必被外界访问，可以使用private。
 */
public class Config {
	/**
	 * 获取配置实例
	 */
	@NotNull
	public static Config get () {
		return ConfigManager.get().getConfig();
	}

	// ============================================================ //
	@Expose public boolean            is_mod_enable                             = true;
	@Expose public boolean            lock_camera_pitch_angle                   = false;
	//
	@Expose public boolean            player_rotate_with_camera_when_not_aiming = false;
	@Expose public boolean            rotate_to_moving_direction                = true;
	@Expose public boolean            auto_rotate_interacting                   = true;
	@Expose public boolean            rotate_interacting_type                   = true;
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
	public         CameraOffsetScheme cameraOffsetScheme                        = new CameraOffsetScheme(this);
	// ============================================================ //

	/**
	 * 加载完成时调用
	 * <p>
	 * 更新次生配置
	 */
	public void update () {
		updateCameraDistances();
	}

	/**
	 * 更新相机到玩家的距离的可调挡位们
	 */
	public void updateCameraDistances () {
		distanceMonoList = StaticMonoList.of(available_distance_count, camera_distance_min, camera_distance_max, i -> i * i, Math::sqrt);
	}
}
