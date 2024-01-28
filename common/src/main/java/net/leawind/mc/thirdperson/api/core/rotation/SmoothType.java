package net.leawind.mc.thirdperson.api.core.rotation;


/**
 * 平滑类型
 * <p>
 * 实现平滑效果可以有多种方式，区别在于 render tick 和 client tick 中的处理方式不同
 * <p>
 * 注意此处的 client tick 可以是mc的 client tick，也可以是自定义的固定间隔的tick，但是需要在 render tick 中计算 partialTick。
 * <p>
 * partialTick 是一个浮点数，表示自上一次 client tick 以来经过的时间占 client tick 间隔的比例，通常用于线性插值
 */
public enum SmoothType {
	/**
	 * <h1>梆硬</h1>
	 * 不用担心抽搐问题，但完全没有平滑效果
	 * <h2>render tick</h2>
	 * 更新目标值并立即应用。 既然不需要平滑效果，自然不需要更新平滑值了
	 * <h2>client tick</h2>
	 */
	HARD,
	/**
	 * <h1>线性插值</h1>
	 * 相当于半衰期为0的 {@link SmoothType#EXP_LINEAR}
	 * <h2>render tick</h2>
	 * 根据 partialTick 获取新值与旧值的线性插值
	 * <h2>client tick</h2>
	 * 更新平滑值并记录旧值
	 */
	LINEAR,
	/**
	 * <h1>指数衰减</h1>
	 * 需要指定平滑系数（半衰期），无需记录旧值即可实现平滑效果。可以在任意时刻更新目标值
	 * <p>
	 * 缺点：对时间精度要求高。当目标值变化速度较快时，平滑值的变化速度可能不平滑
	 * <h2>render tick</h2>
	 * 取值前需要更新平滑值，然后直接取平滑值。
	 * <h2>client tick</h2>
	 */
	EXP,
	/**
	 * <h1>指数衰减+线性插值</h1>
	 * 解决了 {@link SmoothType#EXP} 的不平滑问题。但是响应速度可能略迟钝。
	 * <h2>render tick</h2>
	 * 取值时不直接取平滑值，而是取线性插值
	 * <h2>client tick</h2>
	 * 更新目标值， 更新平滑值并记录旧值
	 */
	EXP_LINEAR,
}
