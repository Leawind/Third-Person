package net.leawind.mc.thirdperson.config;


import net.leawind.mc.math.monolist.StaticMonoList;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.thirdperson.util.ModConstants;
import net.leawind.mc.util.ItemPattern;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 模组配置
 */
public class Config extends AbstractConfig {
	// ============================================================ //
	public StaticMonoList     distanceMonoList;
	public Set<ItemPattern>   aim_item_patterns;
	public Set<ItemPattern>   use_aim_item_patterns;
	// ============================================================ //
	public CameraOffsetScheme cameraOffsetScheme = new CameraOffsetScheme(this);

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
		aim_item_patterns     = new HashSet<>();
		use_aim_item_patterns = new HashSet<>();
		ItemPattern.mergeToSet(aim_item_patterns, List.of(aim_item_list));
		ItemPattern.mergeToSet(use_aim_item_patterns, List.of(use_aim_item_list));
		// 内置物品模式
		if (ThirdPersonMod.getConfig().enable_buildin_aim_item_patterns) {
			ItemPattern.mergeToSet(aim_item_patterns, ModConstants.BUILDIN_AIM_ITEMS);
			ItemPattern.mergeToSet(use_aim_item_patterns, ModConstants.BUILDIN_USE_AIM_ITEMS);
		}
	}
}
