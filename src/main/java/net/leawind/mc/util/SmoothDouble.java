package net.leawind.mc.util;


import net.minecraft.util.Mth;

/**
 * 平滑（指数函数）
 */
@SuppressWarnings("unused")
public class SmoothDouble {
	public double target   = 0;
	public double value    = 0;
	public double decrease = 0;

	public SmoothDouble setTarget (double target) {
		this.target = target;
		return this;
	}

	public SmoothDouble setValue (double value) {
		this.value = value;
		return this;
	}

	public SmoothDouble setDecrease (double decrease) {
		this.decrease = decrease;
		return this;
	}

	/**
	 * 每隔 t 秒变为原来的 k 倍
	 */
	public SmoothDouble setDecrease (double k, double t) {
		this.decrease = Math.pow(k, 1 / t);
		return this;
	}

	/**
	 * @param duration 经过的时间（s）
	 */
	public void update (double duration) {
		value = Mth.lerp(1 - Math.pow(decrease, duration), value, target);
	}
}
