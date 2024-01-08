package net.leawind.mc.thirdperson.forge;


import dev.architectury.platform.forge.EventBuses;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.config.ConfigManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ThirdPersonMod.MOD_ID)
public class ThirdPersonModForge {
	@SuppressWarnings("unused")
	public static final Logger LOGGER = LoggerFactory.getLogger(ThirdPersonMod.MOD_ID);

	public ThirdPersonModForge () {
		// 仅在客户端运行
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			EventBuses.registerModEventBus(ThirdPersonMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
			ThirdPersonMod.init();
			// 配置屏幕
			ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> ConfigManager.get().getConfigScreen(screen)));
		});
	}
}
