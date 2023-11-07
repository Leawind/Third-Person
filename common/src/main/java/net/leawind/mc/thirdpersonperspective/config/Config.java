package net.leawind.mc.thirdpersonperspective.config;


import com.mojang.logging.LogUtils;
import net.leawind.mc.util.monolist.MonoList;
import org.slf4j.Logger;

public class Config {
	public static final Logger  LOGGER                   = LogUtils.getLogger();
	//	Configurations
	public static       boolean isLoaded                 = false;
	public static       boolean is_mod_enable            = true;
	public static       int     available_distance_count = 32;
	public static       double  camera_distance_min      = 0.5;
	public static       double  camera_distance_max      = 32;
	public static       double  camera_ray_trace_length  = 256;

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

	// 根据上面的配置选项生成的配置
	public static MonoList distanceMonoList;
}
