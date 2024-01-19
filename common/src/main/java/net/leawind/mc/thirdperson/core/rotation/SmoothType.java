package net.leawind.mc.thirdperson.core.rotation;


/**
 *
 */
public enum SmoothType {
	/**
	 * 在 render tick 中更新并立即应用新值
	 * <p>
	 * 不用担心抽搐问题，但完全没有平滑效果
	 */
	CRISP,
	/**
	 * 在 client tick 中更新并记录旧值
	 * <p>
	 * 在 render tick 中根据 partialTicks 应用新值与旧值的线性插值
	 */
	LINEAR_SMOOTH,
	/**
	 * 可以在 client tick 中或 render tick 中更新
	 * <p>
	 * 需要指定平滑系数（半衰期）
	 * <p>
	 * 无需记录旧值
	 * <p>
	 * 在 render tick 中需要更新平滑值
	 */
	EXP_SMOOTH,
}
