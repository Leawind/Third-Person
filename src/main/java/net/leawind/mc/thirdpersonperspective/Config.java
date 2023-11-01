package net.leawind.mc.thirdpersonperspective;


import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid=ThirdPersonPerspective.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	public static        boolean                      isLoaded                       = false;
	private static final ForgeConfigSpec.Builder      BUILDER                        = new ForgeConfigSpec.Builder();
	private static final ForgeConfigSpec.BooleanValue AUTO_SWITCH_CAMERA_OFFSET_TYPE = BUILDER.comment("自动调整摄像机位，以避免阻挡视线")
																							  .define(
																								  "auto_switch_camera_offset_type",
																								  true);
	private static final ForgeConfigSpec.BooleanValue DO_RENDER_CROSSHAIR            = BUILDER.comment("是否显示准星").define(
		"do_render_crosshair",
		true);
	private static final ForgeConfigSpec.DoubleValue  CAMERA_OFFSET_RATIO_X          = BUILDER.comment(
		"玩家在屏幕中的水平偏移，0表示屏幕中间，1表示边缘（左或右）").defineInRange("camera_offset_ratio_x", 0.5, 0, 1);
	private static final ForgeConfigSpec.DoubleValue  CAMERA_OFFSET_RATIO_Y          = BUILDER.comment(
		"玩家在屏幕中的垂直偏移，0表示屏幕中间，1表示屏幕底部边缘").defineInRange("camera_offset_ratio_y", +0.372, 0, 1);
	private static final ForgeConfigSpec.DoubleValue  CAMERA_OFFSET_RATIO_TOP        = BUILDER.comment(
		"使用头顶机位时，玩家在屏幕中的垂直偏移，0表示屏幕中间，1表示屏幕底部边缘").defineInRange("camera_offset_ratio_top",
																							   +0.373,
																							   0,
																							   1);
	private static final ForgeConfigSpec.DoubleValue  AIM_OFFSET                     = BUILDER.comment("瞄准时相机视线与玩家视线的距离")
																							  .defineInRange(
																								  "aim_camera_offset_distance",
																								  0.7,
																								  0.0,
																								  4.0);
	private static final ForgeConfigSpec.DoubleValue  AIM_OFFSET_TOP                 = BUILDER.comment(
		"使用头顶机位瞄准时，相机视线与玩家视线的距离")
																							  .defineInRange(
																								  "aim_camera_offset_distance_top",
																								  0.6,
																								  0.0,
																								  4.0);
	private static final ForgeConfigSpec.DoubleValue  CAMERA_MAX_DISTANCE_CLOSER     = BUILDER.comment("较近距离下相机离玩家的最大距离")
																							  .defineInRange(
																								  "camera_max_distance_closer",
																								  1.6,
																								  1.0,
																								  64.0);
	private static final ForgeConfigSpec.DoubleValue  CAMERA_MAX_DISTANCE_FARTHER    = BUILDER.comment("较远距离下相机离玩家的最大距离")
																							  .defineInRange(
																								  "camera_max_distance_farther",
																								  4.0,
																								  1.0,
																								  64.0);
	private static final ForgeConfigSpec.DoubleValue  SMOOTH_EYE_SPEED_XZ            = BUILDER.comment(
		"玩家眼睛平滑移动速度系数（水平方向）\n系数 a = k^(1/t)\n表示每经过 t 秒，到目标的距离衰变为原来的 k 倍。").defineInRange(
		"smooth_eye_speed_xz",
		8e-4,
		0.0,
		1.0);
	private static final ForgeConfigSpec.DoubleValue  SMOOTH_EYE_SPEED_Y             = BUILDER.comment(
		"玩家眼睛平滑移动速度系数（垂直方向）\n系数 a = k^(1/t)\n表示每经过 t 秒，到目标的距离衰变为原来的 k 倍。").defineInRange(
		"smooth_eye_speed_y",
		4e-2,
		0.0,
		1.0);
	private static final ForgeConfigSpec.DoubleValue  MOVE_TO_SURFACE_SPEED          = BUILDER.comment("相机远离玩家的速度系数")
																							  .defineInRange(
																								  "move_to_surface_speed",
																								  0.5,
																								  0D,
																								  1.0);
	private static final ForgeConfigSpec.DoubleValue  CAMERA_OFFSET_UPDATE_SPEED     = BUILDER.comment("相机偏移量平滑变化的速度系数，算法同上")
																							  .defineInRange(
																								  "camera_offset_update_speed",
																								  2e-3,
																								  0.0,
																								  1.0);
	public static final  ForgeConfigSpec              SPEC                           = BUILDER.build();
	public static        boolean                      auto_switch_camera_offset_type;
	public static        boolean                      do_render_crosshair;
	public static        float                        camera_offset_ratio_x;
	public static        float                        camera_offset_ratio_y;
	public static        float                        camera_offset_ratio_top;
	public static        float                        aim_offset;
	public static        float                        aim_offset_top;
	public static        double                       camera_max_distance_closer;
	public static        double                       camera_max_distance_farther;
	public static        Vec3                         smooth_eye_speed;
	public static        double                       move_to_surface_speed;
	public static        double                       camera_offset_update_speed;

	@SubscribeEvent
	public static void onLoad (final ModConfigEvent event) {
		isLoaded                       = true;
		auto_switch_camera_offset_type = AUTO_SWITCH_CAMERA_OFFSET_TYPE.get();
		do_render_crosshair            = DO_RENDER_CROSSHAIR.get();
		camera_offset_ratio_x          = CAMERA_OFFSET_RATIO_X.get().floatValue();
		camera_offset_ratio_y          = CAMERA_OFFSET_RATIO_Y.get().floatValue();
		camera_offset_ratio_top        = CAMERA_OFFSET_RATIO_TOP.get().floatValue();
		aim_offset                     = AIM_OFFSET.get().floatValue();
		aim_offset_top                 = AIM_OFFSET_TOP.get().floatValue();
		camera_max_distance_closer     = CAMERA_MAX_DISTANCE_CLOSER.get();
		camera_max_distance_farther    = CAMERA_MAX_DISTANCE_FARTHER.get();
		smooth_eye_speed               = new Vec3(SMOOTH_EYE_SPEED_XZ.get(),
												  SMOOTH_EYE_SPEED_Y.get(),
												  SMOOTH_EYE_SPEED_XZ.get());
		move_to_surface_speed          = MOVE_TO_SURFACE_SPEED.get();
		camera_offset_update_speed     = CAMERA_OFFSET_UPDATE_SPEED.get();
		ThirdPersonPerspective.LOGGER.debug("Config is loaded");
	}
}
