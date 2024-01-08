package net.leawind.mc.thirdperson.fabric;


import net.fabricmc.loader.api.FabricLoader;
import net.leawind.mc.thirdperson.ExpectPlatform;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.fabric.config.ConfigBuilders;
import net.minecraft.client.gui.screens.Screen;

import java.nio.file.Path;

/**
 * 在 {@link ExpectPlatform} 中申明，在此处实现
 */
public class ExpectPlatformImpl {
	/**
	 * This is our actual method to {@link ExpectPlatform#getConfigDirectory()}.
	 */
	public static Path getConfigDirectory () {
		return FabricLoader.getInstance().getConfigDir();
	}

	public static Screen buildConfigScreen (Config config, Screen parent) {
		return ConfigBuilders.buildConfigScreen(config, parent);
	}
}
