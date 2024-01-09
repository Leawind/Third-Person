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
	@NotNull
	public static Config get () {
		return ConfigManager.get().getConfig();
	}

	public Config () {
	}

	// ============================================================ //
	@Expose public boolean            is_mod_enable                             = true;
	@Expose public boolean            player_rotate_with_camera_when_not_aiming = false;
	@Expose public boolean            render_crosshair_when_not_aiming          = true;
	@Expose public boolean            render_crosshair_when_aiming              = true;
	@Expose public boolean            center_offset_when_flying                 = true;
	@Expose public boolean            rotate_to_moving_direction                = true;
	@Expose public boolean            lock_camera_pitch_angle                   = false;
	@Expose public boolean            turn_with_camera_when_enter_first_person  = true;
	@Expose public boolean            player_fade_out_enabled                   = true;
	@Expose public double             camera_ray_trace_length                   = 256;
	@Expose public boolean            auto_rotate_interacting                   = true;
	@Expose public boolean            rotate_interacting_type                   = true;
	@Expose public int                available_distance_count                  = 16;
	@Expose public double             camera_distance_min                       = 0.5;
	@Expose public double             camera_distance_max                       = 8;
	@Expose public double             flying_smooth_factor                      = 0.5;
	@Expose public double             adjusting_camera_offset_smooth_factor     = 0.100;
	@Expose public double             adjusting_distance_smooth_factor          = 0.100;
	@Expose public double             normal_smooth_factor_horizon              = 0.500;
	@Expose public double             normal_smooth_factor_vertical             = 0.500;
	@Expose public double             normal_camera_offset_smooth_factor        = 0.500;
	@Expose public double             normal_distance_smooth_factor             = 0.640;
	@Expose public double             aiming_smooth_factor_horizon              = 0.002;
	@Expose public double             aiming_smooth_factor_vertical             = 0.002;
	@Expose public double             aiming_camera_offset_smooth_factor        = 0.100;
	@Expose public double             aiming_distance_smooth_factor             = 0.110;
	@Expose public double             normal_max_distance                       = 2.5;
	@Expose public double             normal_offset_x                           = -0.28;
	@Expose public double             normal_offset_y                           = 0.31;
	@Expose public double             normal_offset_center                      = 0.24D;
	@Expose public double             aiming_max_distance                       = 0.89;
	@Expose public double             aiming_offset_x                           = -0.47;
	@Expose public double             aiming_offset_y                           = -0.09;
	@Expose public double             aiming_offset_center                      = 0.48;
	// ============================================================ //
	public         StaticMonoList     distanceMonoList;
	public         CameraOffsetScheme cameraOffsetScheme                        = CameraOffsetScheme.DEFAULT.bindConfig(this);
	// ============================================================ //

	/**
	 * 加载完成时调用
	 * <p>
	 * 更新次生配置
	 */
	public void update () {
		updateToCameraDistances();
		updateToCameraOffsetScheme();
	}

	/**
	 * 更新相机到玩家的距离的可调挡位们
	 */
	public void updateToCameraDistances () {
		distanceMonoList = StaticMonoList.of(available_distance_count, camera_distance_min, camera_distance_max, i -> i * i, Math::sqrt);
	}

	/**
	 * 更新相机偏移方案
	 */
	public void updateToCameraOffsetScheme () {
		// maxDist, offsetValue
		CameraOffsetScheme scheme = CameraOffsetScheme.create(normal_max_distance, normal_offset_x, normal_offset_y, aiming_max_distance, aiming_offset_x, aiming_offset_y);
		// Normal mode //
		scheme.normalMode.setDistanceSmoothFactor(normal_distance_smooth_factor)    //
						 .setOffsetSmoothFactor(normal_camera_offset_smooth_factor)    //
						 .setEyeSmoothFactor(normal_smooth_factor_horizon, normal_smooth_factor_vertical)    //
						 .setCenterOffsetRatio(normal_offset_center);
		// Aiming mode //
		scheme.aimingMode.setDistanceSmoothFactor(aiming_distance_smooth_factor)    //
						 .setOffsetSmoothFactor(aiming_camera_offset_smooth_factor)    //
						 .setEyeSmoothFactor(aiming_smooth_factor_horizon, aiming_smooth_factor_vertical)    //
						 .setCenterOffsetRatio(aiming_offset_center);
		// apply
		cameraOffsetScheme = scheme;
	}

	/**
	 * 从相机偏移方案对象加载配置项
	 * <p>
	 * 当玩家调整偏移量等选项时，会直接修改 CameraOffsetScheme 对象，而不是直接修改 Config
	 * <p>
	 * 所以修改完后需要立即将改动应用到 Config
	 */
	public void updateFromCameraOffsetScheme () {
		// Normal mode //
		normal_max_distance  = cameraOffsetScheme.normalMode.getMaxDistance();
		normal_offset_x      = cameraOffsetScheme.normalMode.getSideOffsetRatio().x;
		normal_offset_y      = cameraOffsetScheme.normalMode.getSideOffsetRatio().y;
		normal_offset_center = cameraOffsetScheme.normalMode.getCenterOffsetRatio();
		// Aiming mode //
		aiming_max_distance  = cameraOffsetScheme.aimingMode.getMaxDistance();
		aiming_offset_x      = cameraOffsetScheme.aimingMode.getSideOffsetRatio().x;
		aiming_offset_y      = cameraOffsetScheme.aimingMode.getSideOffsetRatio().y;
		aiming_offset_center = cameraOffsetScheme.aimingMode.getCenterOffsetRatio();
	}
}
