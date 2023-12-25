package net.leawind.mc.thirdperson.forge.config;


import com.mojang.logging.LogUtils;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.config.Config;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid=ThirdPersonMod.MOD_ID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class ConfigForge {
	public static final  Logger                       LOGGER                        = LogUtils.getLogger();
	private static final ForgeConfigSpec.Builder      BUILDER                       = new ForgeConfigSpec.Builder();
	public static final  ForgeConfigSpec.IntValue     AVAILABLE_DISTANCE_COUNT      = BUILDER.translation(KEY(
																								 "available_distance_count"))
																							 .defineInRange(
																								 "available_distance_count",
																								 16,
																								 2,
																								 128);
	public static final  ForgeConfigSpec.DoubleValue  CAMERA_DISTANCE_MIN           = BUILDER.translation(KEY("distance_min"))
																							 .defineInRange(
																								 "camera_distance_min",
																								 0.1d,
																								 0,
																								 2);
	public static final  ForgeConfigSpec.DoubleValue  CAMERA_DISTANCE_MAX           = BUILDER.translation(KEY("distance_max"))
																							 .defineInRange(KEY(
																												"camera_distance_max"),
																											8d,
																											2,
																											16);
	public static final  ForgeConfigSpec.DoubleValue  CAMERA_RAY_TRACE_LENGTH       = BUILDER.translation(KEY(
		"camera_ray_trace_length")).defineInRange("camera_ray_trace_length", 256d, 10, 1024);
	public static final  ForgeConfigSpec.DoubleValue  AIMING_OFFSET_MAX             = BUILDER.translation(KEY(
		"camera_ray_trace_length")).defineInRange("aiming_offset_max", 2d, 0.5, 5);
	public static final  ForgeConfigSpec.BooleanValue IS_ONLY_ONE_THIRD_PERSON_MODE = BUILDER.translation(KEY(
		"is_only_one_third_person_mode")).define("is_only_one_third_person_mode", true);
	private static final ForgeConfigSpec.BooleanValue IS_MOD_ENABLE                 = BUILDER.translation(KEY("is_mod_enable"))
																							 .define("is_mod_enable", true);


	public static final  ForgeConfigSpec              SPEC                          = BUILDER.build();

	private static String KEY (String name) {
		return "options.ltpv." + name;
	}

	@SubscribeEvent
	public static void onLoad (final ModConfigEvent event) {
		Config.is_mod_enable                 = IS_MOD_ENABLE.get();
		Config.available_distance_count      = AVAILABLE_DISTANCE_COUNT.get();
		Config.camera_distance_min           = CAMERA_DISTANCE_MIN.get();
		Config.camera_distance_max           = CAMERA_DISTANCE_MAX.get();
		Config.camera_ray_trace_length       = CAMERA_RAY_TRACE_LENGTH.get();
		Config.aiming_offset_max             = AIMING_OFFSET_MAX.get();
		Config.is_only_one_third_person_mode = IS_ONLY_ONE_THIRD_PERSON_MODE.get();
		Config.onLoad(event);
	}
}
