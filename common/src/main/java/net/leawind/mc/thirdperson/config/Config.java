package net.leawind.mc.thirdperson.config;


import com.mojang.logging.LogUtils;
import net.leawind.mc.thirdperson.ExpectPlatform;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.util.monolist.MonoList;
import org.slf4j.Logger;

import java.nio.file.Path;

/**
 * 配置项
 * <p>
 * 玩家可以通过修改配置文件或在配置屏幕中修改这些配置项。修改完成后会触发 onLoad 事件处理函数
 */
public class Config {
	public static final Logger   LOGGER                        = LogUtils.getLogger();
	//	配置文件路径
	public static final Path     CONFIG_FILE_PATH              = ExpectPlatform.getConfigDirectory().resolve(
		ThirdPersonMod.MOD_ID + ".json5");
	//	配置项们
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
	 * 初始化配置
	 */
	public static void init () {
		onLoad("No event");
	}

	/**
	 * 加载配置完成时调用
	 * <p>
	 * [Forge] Lnet/leawind/mc/thirdperson/forge/config/ConfigForge
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

	/**
	 * 根据配置项的名称获取配置键
	 */
	private static String getKey (String name) {
		return "options." + ThirdPersonMod.MOD_ID + "." + name;
	}
}
