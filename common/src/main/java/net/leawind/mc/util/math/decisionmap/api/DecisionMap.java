package net.leawind.mc.util.math.decisionmap.api;


import net.leawind.mc.util.math.decisionmap.impl.DecisionMapImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 决策表
 */
@SuppressWarnings("unused")
public interface DecisionMap<T> {
	int MAX_FACTOR_COUNT = 32;

	/**
	 * 空的决策表
	 */
	static <T> DecisionMap<T> of () {
		return new DecisionMapImpl<>();
	}

	/**
	 * 根据枚举类型注册决策因素
	 *
	 * @param factorsClazz 枚举类型
	 * @param <T>          做出决策时的返回值类型
	 */
	static <T> DecisionMap<T> of (Class<? extends DecisionFactorEnum> factorsClazz) {
		DecisionMap<T> decisionMap = new DecisionMapImpl<>();
		if (!factorsClazz.isEnum()) {
			throw new IllegalArgumentException(String.format("%s is not Enum", factorsClazz.getName()));
		}
		for (Field field: factorsClazz.getFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				try {
					if (field.canAccess(null)) {
						String          factorName     = field.getName();
						BooleanSupplier factorSupplier = ((DecisionFactorEnum)field.get(null)).getSupplier();
						decisionMap.addFactor(new DecisionFactor(factorName, factorSupplier));
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return decisionMap;
	}

	/**
	 * 添加决策因素
	 * <p>
	 * 自动递增id
	 */
	void addFactor (DecisionFactor decisionFactor);

	/**
	 * 做出决策
	 */
	T make ();

	/**
	 * 获取因素数量
	 */
	int getFactorCount ();

	/**
	 * 2^因素数量
	 * <p>
	 * 每个因素有2中可能性，返回所有可能性总数
	 */
	int getMapSize ();

	/**
	 * 构建
	 */
	DecisionMap<T> build ();

	/**
	 * 是否已经构建
	 */
	boolean isBuilt ();

	/**
	 * 添加规则
	 *
	 * @param getter 决策方法
	 * @return 原对象
	 */
	DecisionMap<T> addRule (int id, Supplier<T> getter);

	/**
	 * 添加规则
	 *
	 * @param id     因素列表
	 * @param mask   掩码
	 * @param getter 决策方法
	 * @return 原对象
	 */
	DecisionMap<T> addRule (int id, int mask, Supplier<T> getter);

	/**
	 * 添加规则
	 * <p>
	 * 此方法会立即使之前在此对象上调用的所有{@link DecisionMap#addRule}方法失效
	 *
	 * @param func 根据输入的各个因素返回相应的决策函数
	 * @return 原对象
	 */
	DecisionMap<T> addRule (Function<boolean[], Supplier<T>> func);

	/**
	 * 决策因素
	 */
	record DecisionFactor(String name, BooleanSupplier supplier) {
		public boolean get () {
			return supplier.getAsBoolean();
		}
	}
}
