package net.leawind.mc.thirdperson.fabric;


import net.fabricmc.api.ClientModInitializer;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class ThirdPersonModFabric implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(ThirdPersonMod.MOD_ID);

	public void onInitializeClient () {
		ThirdPersonMod.init();
	}
}
