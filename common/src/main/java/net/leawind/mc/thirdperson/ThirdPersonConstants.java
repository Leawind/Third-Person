package net.leawind.mc.thirdperson;


import net.leawind.mc.thirdperson.impl.core.EntityAgentImpl;
import net.leawind.mc.util.annotations.VersionSensitive;
import net.minecraft.client.Minecraft;

import java.io.File;

public final class ThirdPersonConstants {
	public static final                   String MOD_ID                                = "leawind_third_person";
	public static final                   String MOD_NAME                              = "Leawind's Third Person";
	public static final                   String KEY_CATEGORY                          = "key.categories." + MOD_ID;
	public static final                   File   CONFIG_FILE                           = Minecraft.getInstance().gameDirectory.toPath().resolve("config/" + MOD_ID + ".json").toFile();
	public static final                   long   CONFIG_LAZY_SAVE_DELAY                = 60000L;
	/**
	 * 成像平面到相机的距离，这是一个固定值，硬编码在Minecraft源码中。
	 *
	 * @see net.minecraft.client.Camera#getNearPlane()
	 */
	public static final                   double NEAR_PLANE_DISTANCE                   = 0.050;
	/**
	 * 服务器允许的最大交互距离
	 *
	 * @see net.minecraft.server.network.ServerGamePacketListenerImpl#MAX_INTERACTION_DISTANCE
	 */
	@VersionSensitive public static final double MAX_INTERACTION_DISTANCE              = 6.0;
	public static final                   long   CAMERA_FOLLOW_DELAY                   = 5000L;
	public static final                   double CAMERA_PITCH_DEGREE_LIMIT             = 89.800;
	public static final                   double CAMERA_THROUGH_WALL_DETECTION         = 0.180;
	/**
	 * 当准星放在相机实体上时，将相机实体的不透明度降低到这个值。
	 */
	public static final                   double GAZE_OPACITY                          = 0.32;
	public static final                   double OPACITY_HALFLIFE                      = 0.0625;
	/**
	 * 渲染相机实体的透明度阈值，当不透明度低于这个值时，将不渲染实体。
	 *
	 * @see EntityAgentImpl#getSmoothOpacity()
	 */
	public static final                   float  RENDERED_OPACITY_THRESHOLD            = 0.01F;
	public static final                   double FIRST_PERSON_TRANSITION_END_THRESHOLD = 0.05;
}
