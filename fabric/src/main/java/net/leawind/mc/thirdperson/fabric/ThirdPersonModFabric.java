package net.leawind.mc.thirdperson.fabric;


import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import org.slf4j.Logger;

@SuppressWarnings("unused")
public class ThirdPersonModFabric implements ClientModInitializer {
	public static final Logger LOGGER = LogUtils.getLogger();

	public void onInitializeClient () {
		ThirdPersonMod.init();
	}
}
