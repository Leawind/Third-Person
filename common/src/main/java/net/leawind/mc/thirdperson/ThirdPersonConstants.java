package net.leawind.mc.thirdperson;


import net.leawind.mc.thirdperson.impl.core.EntityAgentImpl;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.level.ClipContext;

import java.io.File;

@SuppressWarnings("unused")
public final class ThirdPersonConstants {
	public static final String            MOD_ID                                = "leawind_third_person";
	public static final String            MOD_NAME                              = "Leawind's Third Person";
	public static final String            KEY_CATEGORY                          = "key.categories." + MOD_ID;
	public static final File              CONFIG_FILE                           = Minecraft.getInstance().gameDirectory.toPath().resolve("config/" + MOD_ID + ".json").toFile();
	public static final long              CONFIG_LAZY_SAVE_DELAY                = 60000L;
	/**
	 * 成像平面到相机的距离，这是一个固定值，硬编码在Minecraft源码中。
	 *
	 * @see Camera#getNearPlane()
	 */
	public static final double            NEAR_PLANE_DISTANCE                   = 0.050;
	/**
	 * 蹲下前进时的速度乘数，硬编码
	 *
	 * @see KeyboardInput#tick(boolean)
	 */
	public static final double            SLOWLY_MULTIPLIER                     = 0.3;
	public static final double            CAMERA_PITCH_DEGREE_LIMIT             = 89.800;
	public static final double            CAMERA_THROUGH_WALL_DETECTION         = 0.180;
	/**
	 * 当准星放在相机实体上时，将相机实体的不透明度降低到这个值。
	 */
	public static final double            GAZE_OPACITY                          = 0.32;
	public static final double            OPACITY_HALFLIFE                      = 0.0625;
	/**
	 * 渲染相机实体的不透明度下限阈值，当不透明度低于这个值时，将不渲染实体。
	 *
	 * @see EntityAgentImpl#getSmoothOpacity()
	 */
	public static final float             RENDERED_OPACITY_THRESHOLD_MIN        = 0.01F;
	/**
	 * 渲染相机实体的不透明度上限阈值，当不透明度高于这个值时，将以原版方式渲染实体。
	 */
	public static final float             RENDERED_OPACITY_THRESHOLD_MAX        = 0.99F;
	/**
	 * 预测目标实体时仅考虑视锥角内的实体
	 */
	public static final double            TARGET_PREDICTION_DEGREES_LIMIT       = 30;
	public static final double            FIRST_PERSON_TRANSITION_END_THRESHOLD = 0.05;
	/**
	 * 平滑眼睛的半衰期乘数
	 */
	public static final double            EYE_HALFLIFE_MULTIPLIER               = 0.1;
	/**
	 * 阻挡相机的方块外形获取器
	 */
	public static final ClipContext.Block CAMERA_OBSTACLE_BLOCK_SHAPE_GETTER    = ClipContext.Block.VISUAL;
}
