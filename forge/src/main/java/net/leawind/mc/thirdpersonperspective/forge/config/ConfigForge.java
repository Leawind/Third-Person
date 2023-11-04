package net.leawind.mc.thirdpersonperspective.forge.config;


import com.mojang.logging.LogUtils;
import net.leawind.mc.thirdpersonperspective.ThirdPersonPerspectiveMod;
import net.leawind.mc.thirdpersonperspective.config.Config;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid=ThirdPersonPerspectiveMod.MOD_ID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class ConfigForge {
	public static final  Logger                       LOGGER                     = LogUtils.getLogger();
	private static final ForgeConfigSpec.Builder      BUILDER                    = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.BooleanValue IS_MOD_ENABLE              = BUILDER.comment("是否启用模组").define(
		"is_mod_enable",
		true);
	private static final ForgeConfigSpec.BooleanValue IS_THIRD_PERSON_BY_DEFAULT = BUILDER.comment("是否默认启用第三人称")
																						  .define("is_3rd_person_by_default",
																								  false);
	public static final  ForgeConfigSpec              SPEC                       = BUILDER.build();

	@SubscribeEvent
	public static void onLoad (final ModConfigEvent event) {
		Config.is_mod_enable              = IS_MOD_ENABLE.get();
		Config.is_third_person_by_default = IS_THIRD_PERSON_BY_DEFAULT.get();
		Config.onLoad(event);
	}
}
