package net.leawind.mc.thirdperson.fabric;


import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.config.Config;
import org.slf4j.Logger;

public class ThirdPersonModFabric implements ModInitializer {
	public static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitialize () {
		ThirdPersonMod.init();
		// TODO
		LOGGER.warn("Fabric config not implemented yet!");
		Config.onLoad(null);
	}
}
