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
	public static final  Logger                       LOGGER                   = LogUtils.getLogger();
	private static final ForgeConfigSpec.Builder      BUILDER                  = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.BooleanValue IS_MOD_ENABLE            = BUILDER.translation(
		"options.ltpv.is_mod_enable").define("is_mod_enable",
											 true);
	public static final  ForgeConfigSpec.IntValue     AVAILABLE_DISTANCE_COUNT = BUILDER.translation(
		"options.ltpv.available_distance_count").defineInRange("available_distance_count", 32, 2, 128);
	public static final  ForgeConfigSpec.DoubleValue  DISTANCE_MIN             = BUILDER.translation(
		"options.ltpv" + ".distance_min").defineInRange("distance_min", 0.5d, 0, 2);
	public static final  ForgeConfigSpec.DoubleValue  DISTANCE_MAX             = BUILDER.translation(
		"options.ltpv" + ".distance_max").defineInRange("distance_max", 8d, 2, 32);
	public static final  ForgeConfigSpec.DoubleValue  CAMERA_RAY_TRACE_LENGTH  = BUILDER.translation(
		"options.ltpv" + ".camera_ray_trace_length").defineInRange("camera_ray_trace_length", 128d, 10, 1024);
	public static final  ForgeConfigSpec              SPEC                     = BUILDER.build();

	@SubscribeEvent
	public static void onLoad (final ModConfigEvent event) {
		Config.is_mod_enable            = IS_MOD_ENABLE.get();
		Config.available_distance_count = AVAILABLE_DISTANCE_COUNT.get();
		Config.distance_min             = DISTANCE_MIN.get();
		Config.distance_max             = DISTANCE_MAX.get();
		Config.camera_ray_trace_length  = CAMERA_RAY_TRACE_LENGTH.get();
		Config.onLoad(event);
	}
}
