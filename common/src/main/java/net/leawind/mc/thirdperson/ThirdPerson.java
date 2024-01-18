package net.leawind.mc.thirdperson;


import net.leawind.mc.thirdperson.api.ModConstants;
import net.leawind.mc.thirdperson.api.config.ConfigManager;
import net.leawind.mc.thirdperson.event.ThirdPersonEvents;
import net.leawind.mc.thirdperson.event.ThirdPersonKeys;
import net.leawind.mc.thirdperson.impl.config.Config;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThirdPerson {
	public static final  Logger        LOGGER                   = LoggerFactory.getLogger(ModConstants.MOD_ID);
	private static final ConfigManager CONFIG_MANAGER           = ConfigManager.create();
	public static        float         lastPartialTick          = 1;
	public static        double        lastCameraSetupTimeStamp = 0;

	public static void init () {
		CONFIG_MANAGER.tryLoad();
		ThirdPersonKeys.register();
		ThirdPersonEvents.register();
	}

	public static @NotNull ConfigManager getConfigManager () {
		return CONFIG_MANAGER;
	}

	public static @NotNull Config getConfig () {
		return CONFIG_MANAGER.getConfig();
	}
}
