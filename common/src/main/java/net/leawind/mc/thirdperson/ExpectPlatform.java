package net.leawind.mc.thirdperson;


import net.leawind.mc.thirdperson.impl.config.Config;
import net.minecraft.client.gui.screens.Screen;

public class ExpectPlatform {
	@dev.architectury.injectables.annotations.ExpectPlatform
	public static Screen buildConfigScreen (Config config, Screen parent) {
		throw new AssertionError();
	}
}
