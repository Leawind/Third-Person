package net.leawind.mc.fabric.thirdperson;


import net.fabricmc.api.ClientModInitializer;
import net.leawind.mc.thirdperson.ThirdPerson;

@SuppressWarnings("unused")
public final class ThirdPersonFabric implements ClientModInitializer {
	public void onInitializeClient () {
		ThirdPerson.init();
	}
}
