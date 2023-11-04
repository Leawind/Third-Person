package net.leawind.mc.thirdpersonperspective.config;


import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class Config {
	public static final Logger  LOGGER                     = LogUtils.getLogger();
	//	Configurations
	public static       boolean isLoaded                   = false;
	public static       boolean is_mod_enable              = false;
	public static       boolean is_third_person_by_default = false;

	// Invoked by Forge or Fabric
	public static void onLoad (final Object event) {
		isLoaded = true;
		LOGGER.info("Config is loaded, event: {}", event);
	}
}
