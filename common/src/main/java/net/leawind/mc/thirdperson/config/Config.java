package net.leawind.mc.thirdperson.config;


import net.leawind.mc.math.monolist.StaticMonoList;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.thirdperson.util.ModConstants;
import net.leawind.mc.util.ItemPattern;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * 模组配置
 */
public class Config extends AbstractConfig {
	// ============================================================ //
	public @NotNull StaticMonoList     distanceMonoList;
	public @NotNull Set<ItemPattern>   aim_item_patterns;
	public @NotNull Set<ItemPattern>   use_aim_item_patterns;
	// ============================================================ //
	public @NotNull CameraOffsetScheme cameraOffsetScheme = new CameraOffsetScheme(this);

	public Config () {
		distanceMonoList      = StaticMonoList.of(available_distance_count, camera_distance_min, camera_distance_max, i -> i * i, Math::sqrt);
		aim_item_patterns     = new HashSet<>();
		use_aim_item_patterns = new HashSet<>();
	}

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
		ItemPattern.mergeToSet(aim_item_patterns, aim_item_rules);
		ItemPattern.mergeToSet(use_aim_item_patterns, use_aim_item_rules);
		// 内置物品匹配规则
		if (enable_buildin_aim_item_rules) {
			ItemPattern.mergeToSet(aim_item_patterns, ModConstants.BUILDIN_AIM_ITEM_RULES);
			ItemPattern.mergeToSet(use_aim_item_patterns, ModConstants.BUILDIN_USE_AIM_ITEM_RULES);
		}
	}
}
