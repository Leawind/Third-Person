package net.leawind.mc.thirdpersonperspective.fabric;


import net.fabricmc.api.ModInitializer;
import net.leawind.mc.thirdpersonperspective.ThirdPersonPerspectiveMod;
import net.leawind.mc.thirdpersonperspective.config.Config;

public class ThirdPersonPerspectiveModFabric implements ModInitializer {
	@Override
	public void onInitialize () {
		ThirdPersonPerspectiveMod.init();
		// TODO
		Config.onLoad(null);
	}
}
