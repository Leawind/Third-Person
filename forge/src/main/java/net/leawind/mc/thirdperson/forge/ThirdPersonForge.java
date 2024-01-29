package net.leawind.mc.thirdperson.forge;


import dev.architectury.platform.forge.EventBuses;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.ModConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModConstants.MOD_ID)
public class ThirdPersonForge {
	public ThirdPersonForge () {
		// 仅在客户端运行 
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			EventBuses.registerModEventBus(ModConstants.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
			ThirdPerson.init();
			// 配置屏幕
			ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> ThirdPerson.CONFIG_MANAGER.getConfigScreen(screen)));
		});
	}
}
