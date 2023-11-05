package net.leawind.mc.thirdpersonperspective.config;


import com.mojang.logging.LogUtils;
import net.leawind.mc.util.monolist.MonoList;
import org.slf4j.Logger;

public class Config {
	public static final Logger  LOGGER                     = LogUtils.getLogger();
	//	Configurations
	public static       boolean isLoaded                   = false;
	public static       boolean is_mod_enable              = true;
	public static       boolean is_third_person_by_default = false;
	public static       int     available_distance_count   = 32;
	public static       double  distance_min               = 0.5;
	public static       double  distance_max               = 128;

	// Invoked by Forge or Fabric
	public static void onLoad (final Object event) {
		distanceMonoList = new MonoList(available_distance_count, i -> {
			double b = Math.log(distance_min);
			double a = Math.log(distance_max) - b;
			return Math.exp(a * i / available_distance_count + b);
		});
		isLoaded         = true;
		LOGGER.info("Config is loaded, event: {}", event);
	}

	// Generated configurations
	public static MonoList distanceMonoList;
}
