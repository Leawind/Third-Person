package net.leawind.mc.thirdperson.forge;


import net.leawind.mc.thirdperson.ExpectPlatform;
import net.leawind.mc.thirdperson.forge.config.ConfigBuilders;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fml.loading.FMLPaths;
import net.leawind.mc.thirdperson.config.Config;

import java.nio.file.Path;

public class ExpectPlatformImpl {
	/**
	 * This is our actual method to {@link ExpectPlatform#getConfigDirectory()}.
	 */
	public static Path getConfigDirectory () {
		return FMLPaths.CONFIGDIR.get();
	}

	public static Screen buildConfigScreen (Config config, Screen parent) {
		return ConfigBuilders.buildConfigScreen(config, parent);
	}
}
