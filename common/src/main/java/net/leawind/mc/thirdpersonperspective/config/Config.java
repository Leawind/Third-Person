package net.leawind.mc.thirdpersonperspective.config;


import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class Config {
	public static final Logger  LOGGER   = LogUtils.getLogger();
	public static       boolean isLoaded = false;
	public static       boolean is_mod_enable;
	public static       boolean is_third_person_by_default;

	public static void onLoad (final Object event) {
		isLoaded = true;
		LOGGER.info("Config is loaded, event: {}", event);
	}
}
