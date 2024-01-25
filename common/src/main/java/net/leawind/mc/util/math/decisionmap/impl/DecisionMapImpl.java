package net.leawind.mc.util.math.decisionmap.impl;


import net.leawind.mc.util.math.decisionmap.api.DecisionMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class DecisionMapImpl<T> implements DecisionMap<T> {
	/**
	 * 规则构建器们
	 */
	private final          List<Runnable>       ruleBuilders = new LinkedList<>();
	/**
	 * 因素列表
	 */
	private final @NotNull List<DecisionFactor> factors      = new ArrayList<>();
	/**
	 * 列出所有可能的情况的列表
	 */
	private                List<Supplier<T>>    map;
	/**
	 * 此对象是否已经构建
	 */
	private                boolean              isBuilt      = false;

	public DecisionMapImpl () {
	}

	@Override
	public void addFactor (DecisionFactor factor) {
		assert !isBuilt;
		if (getFactorCount() >= MAX_FACTOR_COUNT) {
			throw new IndexOutOfBoundsException(String.format("Factors count reaches the upper limit %d", getFactorCount()));
		}
		factors.add(factor);
	}

	@Override
	public T make () {
		assert isBuilt;
		int id = 0;
		for (int i = 0; i < factors.size(); i++) {
			id |= (factors.get(i).get() ? 1: 0) << i;
		}
		Supplier<T> getter = map.get(id);
		if (getter != null) {
			return getter.get();
		}
		return null;
	}

	/**
	 * 因素数量
	 */
	@Override
	public int getFactorCount () {
		return factors.size();
	}

	/**
	 * 2^因素数量
	 */
	@Override
	public int getMapSize () {
		return (int)Math.pow(2, getFactorCount());
	}

	@Override
	public DecisionMap<T> build () {
		assert !isBuilt;
		map = new ArrayList<>(getMapSize());
		for (Runnable ruleBuilder: ruleBuilders) {
			ruleBuilder.run();
		}
		isBuilt = true;
		return this;
	}

	@Override
	public boolean isBuilt () {
		return isBuilt;
	}

	@Override
	public DecisionMap<T> addRule (int id, Supplier<T> getter) {
		ruleBuilders.add(() -> map.set(id, getter));
		return this;
	}

	@Override
	public DecisionMap<T> addRule (int id, int mask, Supplier<T> getter) {
		ruleBuilders.add(() -> {
			int mapSize = map.size();
			int im      = id & mask;
			for (int i = 0; i < mapSize; i++) {
				if ((i & mask) == im) {
					map.set(i, getter);
				}
			}
		});
		return this;
	}

	@Override
	public DecisionMap<T> addRule (Function<boolean[], Supplier<T>> func) {
		ruleBuilders.clear();
		ruleBuilders.add(() -> {
			int       mapSize     = map.size();
			int       factorCount = (int)(Math.log(mapSize) / Math.log(2));
			boolean[] params      = new boolean[factorCount];
			for (int id = 0; id < mapSize; id++) {
				for (int i = 0; i < factorCount; i++) {
					params[i] = (id & (1 << i)) > 0;
				}
				map.set(id, func.apply(params));
			}
		});
		return this;
	}
}
