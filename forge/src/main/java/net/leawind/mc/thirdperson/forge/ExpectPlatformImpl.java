package net.leawind.mc.thirdperson.forge;


import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.forge.config.ConfigBuilders;
import net.minecraft.client.gui.screens.Screen;

public class ExpectPlatformImpl {
	public static Screen buildConfigScreen (Config config, Screen parent) {
		return ConfigBuilders.buildConfigScreen(config, parent);
	}
}
