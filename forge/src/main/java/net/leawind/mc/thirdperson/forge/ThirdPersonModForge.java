package net.leawind.mc.thirdperson.forge;


import com.mojang.logging.LogUtils;
import dev.architectury.platform.forge.EventBuses;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.config.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ThirdPersonMod.MOD_ID)
public class ThirdPersonModForge {
	public static final Logger LOGGER = LogUtils.getLogger();

	public ThirdPersonModForge () {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			EventBuses.registerModEventBus(ThirdPersonMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
			ThirdPersonMod.init();
			// Config Menu
			ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
														   () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> Config.getConfigScreen(
															   screen)));
		});
	}
}
