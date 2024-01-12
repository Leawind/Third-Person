package net.leawind.mc.thirdperson.fabric;


import net.fabricmc.api.ClientModInitializer;
import net.leawind.mc.thirdperson.ThirdPersonMod;

@SuppressWarnings("unused")
public class ThirdPersonModFabric implements ClientModInitializer {
	public void onInitializeClient () {
		ThirdPersonMod.init();
	}
}
