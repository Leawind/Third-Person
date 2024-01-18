package net.leawind.mc.thirdperson.forge;


import net.leawind.mc.thirdperson.impl.config.Config;
import net.leawind.mc.thirdperson.forge.config.ConfigBuilder;
import net.minecraft.client.gui.screens.Screen;

public class ExpectPlatformImpl {
	public static Screen buildConfigScreen (Config config, Screen parent) {
		return ConfigBuilder.buildConfigScreen(config, parent);
	}
}
