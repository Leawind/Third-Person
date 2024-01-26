package net.leawind.mc.util.math.decisionmap.api.anno;


import net.leawind.mc.util.math.decisionmap.api.DecisionMap;

import java.lang.annotation.*;

/**
 * 决策因素
 * <p>
 * 只有拥有此注解的字段会被视为决策因素
 * <p>
 * value表示此字段的索引。若不同字段指定了相同索引，将会抛出异常。
 * <p>
 * 如果value为-1，将根据mask的值计算索引。
 * <p>
 * 如果mask为-1，则自动递增索引
 * <p>
 * 计算出的索引值不能重复，不能超过{@link DecisionMap#MAX_FACTOR_COUNT}
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ADecisionFactor {
	/**
	 * 期望索引
	 */
	int value () default -1;

	/**
	 * 期望掩码
	 */
	int mask () default -1;
}
