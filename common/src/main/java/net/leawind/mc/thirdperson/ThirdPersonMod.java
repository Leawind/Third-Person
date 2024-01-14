package net.leawind.mc.thirdperson;


import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.config.ConfigManager;
import net.leawind.mc.thirdperson.core.ModConstants;
import net.leawind.mc.thirdperson.event.ModEvents;
import net.leawind.mc.thirdperson.event.ModKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThirdPersonMod {
	public static final  Logger        LOGGER         = LoggerFactory.getLogger(ModConstants.MOD_ID);
	private static final ConfigManager CONFIG_MANAGER = new ConfigManager();

	public static void init () {
		ModKeys.register();
		ModEvents.register();
	}

	public static ConfigManager getConfigManager () {
		return CONFIG_MANAGER;
	}

	public static Config getConfig () {
		return CONFIG_MANAGER.getConfig();
	}
}
