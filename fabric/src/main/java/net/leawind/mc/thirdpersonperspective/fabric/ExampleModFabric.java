package net.leawind.mc.thirdpersonperspective.fabric;


import net.fabricmc.api.ModInitializer;
import net.leawind.mc.thirdpersonperspective.ExampleMod;

public class ExampleModFabric implements ModInitializer {
	@Override
	public void onInitialize () {
		ExampleMod.init();
	}
}
