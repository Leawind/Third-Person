package net.leawind.mc.thirdperson.fabric;


import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.config.Config;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ThirdPersonModFabric implements ClientModInitializer {
	public static final Logger LOGGER = LogUtils.getLogger();

	public void onInitializeClient () {
		ThirdPersonMod.init();
		// TODO
		LOGGER.warn("Fabric config not implemented yet!");
		Config.onLoad(null);
	}
}
