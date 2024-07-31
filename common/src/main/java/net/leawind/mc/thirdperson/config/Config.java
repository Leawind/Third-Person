package net.leawind.mc.thirdperson.config;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.thirdperson.resources.ItemPatternManager;
import net.leawind.mc.util.itempattern.ItemPattern;
import net.leawind.mc.util.math.monolist.MonoList;
import net.leawind.mc.util.math.monolist.StaticMonoList;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * AbstractConfig 中包含了用户可以直接修改的配置项及默认值。
 * <p>
 * 但要在模组中使用这些配置项，还需要进行进一步的处理。
 */
public class Config extends AbstractConfig {
	public static final @NotNull Config             DEFAULTS                     = new Config();
	private final @NotNull       CameraOffsetScheme cameraOffsetScheme           = new CameraOffsetScheme(this);
	private final @NotNull       Set<ItemPattern>   holdToAimItemPatterns        = new HashSet<>();
	private final @NotNull       Set<ItemPattern>   useToAimItemPatterns         = new HashSet<>();
	public final @NotNull        Set<ItemPattern>   useToFirstPersonItemPatterns = new HashSet<>();
	private                      MonoList           distanceMonoList;

	public Config () {
		update();
	}

	/**
	 * 在配置项发生变化时更新
	 */
	public void update () {
		updateDistancesMonoList();
		updateItemPatterns();
	}

	/**
	 * 更新相机到玩家的距离的可调挡位们
	 */
	public void updateDistancesMonoList () {
		ThirdPerson.LOGGER.debug("Updating distances mono list");
		distanceMonoList = StaticMonoList.of(available_distance_count, camera_distance_min, camera_distance_max, i -> i * i, Math::sqrt);
	}

	/**
	 * 更新配置的自动瞄准物品集合
	 * <p>
	 * aiming_items 是字符串数组，其中的元素是nbt标签表达式
	 * <p>
	 * aiming_item_tags 是解析好的nbt标签集合，用于匹配玩家手持物品
	 *
	 * @see ItemPatternManager#apply
	 */
	public void updateItemPatterns () {
		int count;
		holdToAimItemPatterns.clear();
		count = ItemPattern.addToSet("minecraft", holdToAimItemPatterns, hold_to_aim_item_pattern_expressions);
		ThirdPerson.LOGGER.info("Loaded {} hold_to_aim item patterns from configuration", count);
		useToAimItemPatterns.clear();
		count = ItemPattern.addToSet("minecraft", useToAimItemPatterns, use_to_aim_item_pattern_expressions);
		ThirdPerson.LOGGER.info("Loaded {}  use_to_aim item patterns from configuration", count);
		useToFirstPersonItemPatterns.clear();
		count = ItemPattern.addToSet("minecraft", useToFirstPersonItemPatterns, use_to_first_person_pattern_expressions);
		ThirdPerson.LOGGER.info("Loaded {} use_to_first_person item patterns from configuration", count);
	}

	public @NotNull Set<ItemPattern> getHoldToAimItemPatterns () {
		return holdToAimItemPatterns;
	}

	public @NotNull Set<ItemPattern> getUseToAimItemPatterns () {
		return useToAimItemPatterns;
	}

	public @NotNull Set<ItemPattern> getUseToFirstPersonItemPatterns () {
		return useToFirstPersonItemPatterns;
	}

	public @NotNull CameraOffsetScheme getCameraOffsetScheme () {
		return cameraOffsetScheme;
	}

	public @NotNull MonoList getDistanceMonoList () {
		return distanceMonoList;
	}
}
