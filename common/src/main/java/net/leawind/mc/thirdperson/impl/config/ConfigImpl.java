package net.leawind.mc.thirdperson.impl.config;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.api.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.thirdperson.api.config.Config;
import net.leawind.mc.util.itempattern.ItemPattern;
import net.leawind.mc.util.math.monolist.MonoList;
import net.leawind.mc.util.math.monolist.StaticMonoList;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ConfigImpl extends Config {
	private final CameraOffsetScheme cameraOffsetScheme    = CameraOffsetScheme.of(this);
	private final Set<ItemPattern>   holdToAimItemPatterns = new HashSet<>();
	private final Set<ItemPattern>   useToAimItemPatterns  = new HashSet<>();
	private       MonoList           distanceMonoList;

	public ConfigImpl () {
		super();
		update();
	}

	@Override
	public void update () {
		updateDistancesMonoList();
		updateItemPatterns();
	}

	@Override
	public void updateDistancesMonoList () {
		distanceMonoList = StaticMonoList.of(available_distance_count, camera_distance_min, camera_distance_max, i -> i * i, Math::sqrt);
	}

	@Override
	public void updateItemPatterns () {
		holdToAimItemPatterns.clear();
		int countHold = ItemPattern.addToSet(holdToAimItemPatterns, hold_to_aim_item_pattern_expressions);
		ThirdPerson.LOGGER.info("Loaded {} hold_to_aim item patterns from configuration", countHold);
		useToAimItemPatterns.clear();
		int countUse = ItemPattern.addToSet(useToAimItemPatterns, use_to_aim_item_pattern_expressions);
		ThirdPerson.LOGGER.info("Loaded {}  use_to_aim item patterns from configuration", countUse);
	}

	@Override
	public @NotNull Set<ItemPattern> getHoldToAimItemPatterns () {
		return holdToAimItemPatterns;
	}

	@Override
	public @NotNull Set<ItemPattern> getUseToAimItemPatterns () {
		return useToAimItemPatterns;
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
