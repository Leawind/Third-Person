package net.leawind.mc.thirdperson.fabric;


import net.leawind.mc.thirdperson.ExpectPlatform;
import net.leawind.mc.thirdperson.impl.config.Config;
import net.leawind.mc.thirdperson.fabric.config.ConfigBuilders;
import net.minecraft.client.gui.screens.Screen;

/**
 * 在 {@link ExpectPlatform} 中申明，在此处实现
 */
public class ExpectPlatformImpl {
	public static Screen buildConfigScreen (Config config, Screen parent) {
		return ConfigBuilders.buildConfigScreen(config, parent);
	}
}
