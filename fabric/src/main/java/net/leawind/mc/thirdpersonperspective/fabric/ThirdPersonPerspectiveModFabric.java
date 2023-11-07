package net.leawind.mc.thirdpersonperspective.fabric;


import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.leawind.mc.thirdpersonperspective.ThirdPersonPerspectiveMod;
import net.leawind.mc.thirdpersonperspective.config.Config;
import org.slf4j.Logger;

public class ThirdPersonPerspectiveModFabric implements ModInitializer {
	public static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitialize () {
		ThirdPersonPerspectiveMod.init();
		// TODO
		LOGGER.warn("Fabric config not implemented yet!");
		Config.onLoad(null);
	}
}
