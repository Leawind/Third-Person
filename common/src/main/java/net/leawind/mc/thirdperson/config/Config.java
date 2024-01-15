package net.leawind.mc.thirdperson.config;


import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.leawind.mc.math.monolist.StaticMonoList;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetScheme;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

import java.util.HashSet;

/**
 * 模组配置
 */
public class Config extends AbstractConfig {
	// ============================================================ //
	public StaticMonoList       distanceMonoList;
	public HashSet<CompoundTag> aim_item_tags;
	public HashSet<CompoundTag> use_aim_item_tags;
	// ============================================================ //
	public CameraOffsetScheme   cameraOffsetScheme = new CameraOffsetScheme(this);

	/**
	 * 配置项发生变化时更新
	 */
	public void update () {
		updateDistancesMonoList();
		updateItemSet();
	}

	/**
	 * 更新相机到玩家的距离的可调挡位们
	 */
	public void updateDistancesMonoList () {
		distanceMonoList = StaticMonoList.of(available_distance_count, camera_distance_min, camera_distance_max, i -> i * i, Math::sqrt);
	}

	/**
	 * 更新自动瞄准物品集合
	 * <p>
	 * aiming_items 是字符串数组，其中的元素是nbt标签表达式
	 * <p>
	 * aiming_item_tags 是解析好的nbt标签集合，用于匹配玩家手持物品
	 */
	public void updateItemSet () {
		aim_item_tags     = new HashSet<>();
		use_aim_item_tags = new HashSet<>();
		for (String nbtSrc: aim_items) {
			try {
				aim_item_tags.add(TagParser.parseTag(nbtSrc));
			} catch (CommandSyntaxException e) {
				ThirdPersonMod.LOGGER.error("Skip invalid NBT compound: {}", nbtSrc);
			}
		}
		for (String nbtSrc: use_aim_items) {
			try {
				use_aim_item_tags.add(TagParser.parseTag(nbtSrc));
			} catch (CommandSyntaxException e) {
				ThirdPersonMod.LOGGER.error("Skip invalid NBT compound: {}", nbtSrc);
			}
		}
	}
}
