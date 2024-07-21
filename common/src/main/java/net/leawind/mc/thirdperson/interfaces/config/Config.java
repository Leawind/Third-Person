package net.leawind.mc.thirdperson.interfaces.config;


import net.leawind.mc.thirdperson.mod.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.thirdperson.mod.config.ConfigImpl;
import net.leawind.mc.thirdperson.resources.ItemPatternManager;
import net.leawind.mc.util.itempattern.ItemPattern;
import net.leawind.mc.util.math.monolist.MonoList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 定义配置项的默认值、额外方法等信息
 */
@SuppressWarnings("unused")
public abstract class Config extends AbstractConfig {
	public static final @NotNull Class<? extends Config> IMPL     = ConfigImpl.class;
	public static final @NotNull Config                  DEFAULTS = new DefaultConfig();

	@Contract(" -> new")
	public static @NotNull Config create () {
		return new ConfigImpl();
	}

	/**
	 * 配置项发生变化时更新
	 */
	abstract public void update ();

	/**
	 * 更新相机到玩家的距离的可调挡位们
	 */
	abstract public void updateDistancesMonoList ();

	/**
	 * 更新配置的自动瞄准物品集合
	 * <p>
	 * aiming_items 是字符串数组，其中的元素是nbt标签表达式
	 * <p>
	 * aiming_item_tags 是解析好的nbt标签集合，用于匹配玩家手持物品
	 *
	 * @see ItemPatternManager#apply
	 */
	abstract public void updateItemPatterns ();

	abstract public @NotNull Set<ItemPattern> getHoldToAimItemPatterns ();

	abstract public @NotNull Set<ItemPattern> getUseToAimItemPatterns ();

	abstract public @NotNull Set<ItemPattern> getUseToFirstPersonItemPatterns ();

	abstract public @NotNull CameraOffsetScheme getCameraOffsetScheme ();

	abstract public @NotNull MonoList getDistanceMonoList ();

	private static final class DefaultConfig extends Config {
		@Override
		public void update () {
			throw illegalAccess();
		}

		@Override
		public void updateDistancesMonoList () {
			throw illegalAccess();
		}

		@Override
		public void updateItemPatterns () {
			throw illegalAccess();
		}

		@Override
		public @NotNull Set<ItemPattern> getHoldToAimItemPatterns () {
			throw illegalAccess();
		}

		@Override
		public @NotNull Set<ItemPattern> getUseToAimItemPatterns () {
			throw illegalAccess();
		}

		@Override
		public @NotNull Set<ItemPattern> getUseToFirstPersonItemPatterns () {
			throw illegalAccess();
		}

		@Override
		public @NotNull CameraOffsetScheme getCameraOffsetScheme () {
			throw illegalAccess();
		}

		@Override
		public @NotNull MonoList getDistanceMonoList () {
			throw illegalAccess();
		}

		@Contract(value=" -> new", pure=true)
		private @NotNull IllegalAccessError illegalAccess () {
			return new IllegalAccessError("This method should not be invoked on default config");
		}
	}
}
