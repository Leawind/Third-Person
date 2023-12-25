package net.leawind.mc.thirdperson.forge;


import com.mojang.logging.LogUtils;
import dev.architectury.platform.forge.EventBuses;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.forge.config.ConfigForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ThirdPersonMod.MOD_ID)
public class ThirdPersonModForge {
	public static final Logger LOGGER = LogUtils.getLogger();

	public ThirdPersonModForge () {
		EventBuses.registerModEventBus(ThirdPersonMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
		// Submit our event bus to let architectury register our content on the right time
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigForge.SPEC);
		ThirdPersonMod.init();
	}
}
