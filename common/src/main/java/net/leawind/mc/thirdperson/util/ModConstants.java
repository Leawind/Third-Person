package net.leawind.mc.thirdperson.util;


import net.minecraft.client.Minecraft;

import java.io.File;
import java.util.List;

public class ModConstants {
	public static final String       MOD_ID                        = "leawind_third_person";
	public static final String       KEY_CATEGORY                  = "key.categories." + MOD_ID;
	public static final File         CONFIG_FILE                   = Minecraft.getInstance().gameDirectory.toPath().resolve("config/" + MOD_ID + ".json").toFile();
	public static final List<String> USE_AIM_ITEM_LIST             = List.of("minecraft:bow", "minecraft:trident");
	/**
	 * 当玩家手持以下物品时会自动进入瞄准模式
	 * <p>
	 * TODO 作为可配置项
	 */
	public static final List<String> AIM_ITEM_LIST                 = List.of("minecraft:ender_pearl", "minecraft:snowball", "minecraft:egg", "minecraft:splash_potion", "minecraft:lingering_potion", "minecraft:experience_bottle");
	public static final double       EYE_POSITIOIN_SMOOTH_WEIGHT   = 8;
	public static final double       DISTANCE_TO_EYE_SMOOTH_WEIGHT = 4;
	public static final double       OFFSET_RATIO_SMOOTH_WEIGHT    = 12;
	/**
	 * 成像平面到相机的距离，这是一个固定值，硬编码在Minecraft源码中。
	 * <p>
	 * 取自 {@link net.minecraft.client.Camera#getNearPlane()}
	 */
	public static final double       NEAR_PLANE_DISTANCE           = 0.050;
	public static final double       CAMERA_PITCH_DEGREE_LIMIT     = 89.800;
	public static final double       CAMERA_THROUGH_WALL_DETECTION = 0.180;
	// Player rotation
	public static final double       PLAYER_ROTATION_HALFLIFE      = 0.06;
}
