package net.leawind.mc.thirdperson.impl.config;


import net.leawind.mc.thirdperson.ThirdPersonConstants;
import net.leawind.mc.thirdperson.api.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.thirdperson.api.config.Config;
import net.leawind.mc.util.itempattern.ItemPattern;
import net.leawind.mc.util.math.monolist.MonoList;
import net.leawind.mc.util.math.monolist.StaticMonoList;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ConfigImpl extends Config {
	private final CameraOffsetScheme cameraOffsetScheme = CameraOffsetScheme.of(this);
	private final Set<ItemPattern>   aimItemPatterns    = new HashSet<>();
	private final Set<ItemPattern>   useAimItemPatterns = new HashSet<>();
	private       MonoList           distanceMonoList;

	public ConfigImpl () {
		super();
		update();
	}

	@Override
	public void update () {
		updateDistancesMonoList();
		updateItemSet();
	}

	@Override
	public void updateDistancesMonoList () {
		distanceMonoList = StaticMonoList.of(available_distance_count, camera_distance_min, camera_distance_max, i -> i * i, Math::sqrt);
	}

	@Override
	public void updateItemSet () {
		aimItemPatterns.clear();
		useAimItemPatterns.clear();
		ItemPattern.addToSet(aimItemPatterns, holding_item_aiming_rules);
		ItemPattern.addToSet(useAimItemPatterns, using_item_aiming_rules);
		// 内置物品匹配规则
		if (enable_buildin_item_aiming_rules) {
			ItemPattern.addToSet(aimItemPatterns, ThirdPersonConstants.BUILDIN_HOLDING_ITEM_AIMING_RULES);
			ItemPattern.addToSet(useAimItemPatterns, ThirdPersonConstants.BUILDIN_USING_ITEM_AIMING_RULES);
		}
	}

	@Override
	public @NotNull Set<ItemPattern> getAimItemPatterns () {
		return aimItemPatterns;
	}

	@Override
	public @NotNull Set<ItemPattern> getUseAimItemPatterns () {
		return useAimItemPatterns;
	}

	@Override
	public @NotNull CameraOffsetScheme getCameraOffsetScheme () {
		return cameraOffsetScheme;
	}

	@Override
	public @NotNull MonoList getDistanceMonoList () {
		return distanceMonoList;
	}
}
