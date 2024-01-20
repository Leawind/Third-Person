package net.leawind.mc.thirdperson.api.core.rotation;


/**
 * 平滑类型
 * <p>
 * 有多种方式实现平滑效果
 */
public enum SmoothType {
	/**
	 * 在 render tick 中更新并立即应用新值
	 * <p>
	 * 不用担心抽搐问题，但完全没有平滑效果
	 */
	HARD,
	/**
	 * 在 client tick 中更新并记录旧值
	 * <p>
	 * 在 render tick 中根据 partialTick 获取新值与旧值的线性插值
	 */
	LINEAR,
	/**
	 * 可以在 client tick 中或 render tick 中更新
	 * <p>
	 * 需要指定平滑系数（半衰期）
	 * <p>
	 * 无需记录旧值
	 * <p>
	 * 在 render tick 中需要更新平滑值
	 */
	EXP,
	/**
	 * 在 client tick 中更新并记录旧值，在 render tick 中获取线性插值
	 */
	EXP_LINEAR,
}
