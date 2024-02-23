package net.leawind.mc.thirdperson;


import net.leawind.mc.util.annotations.VersionSensitive;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.util.List;

public final class ThirdPersonConstants {
	public static final                               String       MOD_ID                                       = "leawind_third_person";
	public static final                               String       KEY_CATEGORY                                 = "key.categories." + MOD_ID;
	public static final                               File         CONFIG_FILE                                  = Minecraft.getInstance().gameDirectory.toPath().resolve("config/" + MOD_ID + ".json").toFile();
	public static final                               double       EYE_POSITIOIN_SMOOTH_WEIGHT                  = 8;
	public static final                               double       DISTANCE_TO_EYE_SMOOTH_WEIGHT                = 4;
	public static final                               double       OFFSET_RATIO_SMOOTH_WEIGHT                   = 12;
	/**
	 * 成像平面到相机的距离，这是一个固定值，硬编码在Minecraft源码中。
	 * <p>
	 * 取自 {@link net.minecraft.client.Camera#getNearPlane()}
	 */
	public static final                               double       NEAR_PLANE_DISTANCE                          = 0.050;
	/**
	 * 服务器允许的最大交互距离
	 *
	 * @see net.minecraft.server.network.ServerGamePacketListenerImpl#MAX_INTERACTION_DISTANCE
	 */
	@VersionSensitive public static final             double       MAX_INTERACTION_DISTANCE                     = 6.0;
	public static final                               double       CAMERA_PITCH_DEGREE_LIMIT                    = 89.800;
	public static final                               double       CAMERA_THROUGH_WALL_DETECTION                = 0.180;
	@Deprecated @VersionSensitive public static final List<String> BUILDIN_HOLD_TO_AIM_ITEM_PATTERN_EXPRESSIONS = List.of("crossbow{Charged:1b}", "ender_pearl", "snowball", "egg", "splash_potion", "lingering_potion", "experience_bottle");
	@Deprecated @VersionSensitive public static final List<String> BUILDIN_USE_TO_AIM_ITEM_PATTERN_EXPRESSIONS  = List.of("bow", "trident");
}
