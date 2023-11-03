package net.leawind.mc.thirdpersonperspective.forge;


import dev.architectury.platform.forge.EventBuses;
import net.leawind.mc.thirdpersonperspective.ThirdPersonPerspectiveMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ThirdPersonPerspectiveMod.MOD_ID)
public class ThirdPersonPerspectiveModForge {
	public ThirdPersonPerspectiveModForge () {
		// Submit our event bus to let architectury register our content on the right time
		EventBuses.registerModEventBus(ThirdPersonPerspectiveMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
		ThirdPersonPerspectiveMod.init();
	}
}
