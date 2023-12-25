package net.leawind.mc.thirdpersonperspective.config;


import com.mojang.logging.LogUtils;
import net.leawind.mc.util.monolist.MonoList;
import org.slf4j.Logger;

/**
 * 配置项
 * <p>
 * 玩家可以通过修改配置文件或在配置屏幕中修改这些配置项。修改完成后会触发 onLoad 事件处理函数
 */
public class Config {
	public static final Logger   LOGGER                        = LogUtils.getLogger();
	//	Configurations
	public static       boolean  isLoaded                      = false;
	public static       boolean  is_mod_enable                 = true;
	public static       int      available_distance_count      = 16;
	public static       double   camera_distance_min           = 0.1;
	public static       double   camera_distance_max           = 8;
	public static       double   camera_ray_trace_length       = 256;
	public static       double   aiming_offset_max             = 2.0;
	public static       boolean  is_only_one_third_person_mode = true;
	// 根据上面的配置选项生成的配置（在onLoad中更新）
	public static       MonoList distanceMonoList;

	/**
	 * 加载配置完成时调用
	 * <p>
	 * [Forge] Lnet/leawind/mc/thirdpersonperspective/forge/config/ConfigForge
	 *
	 * @param event forge:ModConfigEvent, fabric:?
	 */
	public static void onLoad (final Object event) {
		distanceMonoList = MonoList.of(available_distance_count,
									   camera_distance_min,
									   camera_distance_max,
									   i -> i * i,
									   Math::sqrt);
		isLoaded         = true;
		LOGGER.info("Config is loaded, event: {}", event);
	}
}
