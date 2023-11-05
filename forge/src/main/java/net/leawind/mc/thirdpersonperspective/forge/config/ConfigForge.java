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
	private static final ForgeConfigSpec.BooleanValue IS_MOD_ENABLE              = BUILDER.comment("本模组是否启用").define(
		"is_mod_enable",
		true);
	private static final ForgeConfigSpec.BooleanValue IS_THIRD_PERSON_BY_DEFAULT = BUILDER.comment("是否默认启用第三人称")
																						  .define("is_3rd_person_by_default",
																								  false);
	public static final  ForgeConfigSpec.IntValue     AVAILABLE_DISTANCE_COUNT   = BUILDER.comment("可选的距离数量")
																						  .defineInRange(
																							  "available_distance_count",
																							  32,
																							  2,
																							  128);
	public static final  ForgeConfigSpec.DoubleValue  DISTANCE_MIN               = BUILDER.comment("距离可调范围的下限")
																						  .defineInRange("dev_distance_min",
																										 0.5d,
																										 0,
																										 2);
	public static final  ForgeConfigSpec.DoubleValue  DISTANCE_MAX               = BUILDER.comment("距离可调范围的上限")
																						  .defineInRange("dev_distance_max",
																										 8d,
																										 2,
																										 32);
	public static final  ForgeConfigSpec              SPEC                       = BUILDER.build();

	@SubscribeEvent
	public static void onLoad (final ModConfigEvent event) {
		Config.is_mod_enable              = IS_MOD_ENABLE.get();
		Config.is_third_person_by_default = IS_THIRD_PERSON_BY_DEFAULT.get();
		Config.available_distance_count   = AVAILABLE_DISTANCE_COUNT.get();
		Config.distance_min               = DISTANCE_MIN.get();
		Config.distance_max               = DISTANCE_MAX.get();
		Config.onLoad(event);
	}
}
