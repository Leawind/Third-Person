package net.leawind.mc.util.math.decisionmap.api;


import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

/**
 * 用于标记枚举类型
 * <p>
 * 枚举类型包含各种决策因素
 */
public interface DecisionFactorEnum {
	@NotNull BooleanSupplier getSupplier ();
}
