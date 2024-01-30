package net.leawind.mc.thirdperson.api.config;


import net.leawind.mc.thirdperson.api.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.util.itempattern.ItemPattern;
import net.leawind.mc.util.math.monolist.MonoList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 定义配置项的默认值、额外方法等信息
 */
public abstract class Config extends AbstractConfig {
	public static final Config DEFAULTS = new Config() {
	};

	/**
	 * 配置项发生变化时更新
	 */
	public void update () {
		throw new IllegalAccessError("This method should not be invoked on default config");
	}

	/**
	 * 更新相机到玩家的距离的可调挡位们
	 */
	public void updateDistancesMonoList () {
		throw new IllegalAccessError("This method should not be invoked on default config");
	}

	/**
	 * 更新自动瞄准物品集合
	 * <p>
	 * aiming_items 是字符串数组，其中的元素是nbt标签表达式
	 * <p>
	 * aiming_item_tags 是解析好的nbt标签集合，用于匹配玩家手持物品
	 */
	public void updateItemSet () {
		throw new IllegalAccessError("This method should not be invoked on default config");
	}

	public @NotNull Set<ItemPattern> getAimItemPatterns () {
		throw new IllegalAccessError("This method should not be invoked on default config");
	}

	public @NotNull Set<ItemPattern> getUseAimItemPatterns () {
		throw new IllegalAccessError("This method should not be invoked on default config");
	}

	public @NotNull CameraOffsetScheme getCameraOffsetScheme () {
		throw new IllegalAccessError("This method should not be invoked on default config");
	}

	public @NotNull MonoList getDistanceMonoList () {
		throw new IllegalAccessError("This method should not be invoked on default config");
	}
}
