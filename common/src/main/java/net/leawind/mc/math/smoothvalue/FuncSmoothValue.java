package net.leawind.mc.math.smoothvalue;


import com.mojang.blaze3d.Blaze3D;

import java.util.function.Function;

/**
 * 可以自定义平滑函数的平滑值
 *
 * @param <T>
 */
@SuppressWarnings("unused")
public abstract class FuncSmoothValue<T> implements ISmoothValue<T> {
	public static final class Functions {
		public static final Function<Double, Double> linear = x -> x;
		public static final Function<Double, Double> sqrt   = Math::sqrt;
		public static final Function<Double, Double> arc    = x -> Math.sqrt(x * (2 - x));
		public static final Function<Double, Double> sin    = x -> Math.sin(Math.PI * x * 0.5);
		public static final Function<Double, Double> arctan = x -> Math.atan(Math.PI * x * 0.5);
	}

	/**
	 * func 必须在 [0,1] 上有定义
	 * <p>
	 * 建议其满足：
	 * <p>
	 * func(0) = 0
	 * <p>
	 * func(1) = 1
	 * <p>
	 * 单调
	 * <p>
	 * 连续
	 * <p>
	 * 可导
	 */
	protected Function<Double, Double> func;
	protected T                        value;
	protected double                   duration  = 0;
	protected double                   startTime = 0;
	protected T                        startValue;
	protected T                        targetValue;

	public FuncSmoothValue (Function<Double, Double> func) {
		this.func = func;
	}

	public void setFunc (Function<Double, Double> func) {
		this.func = func;
	}

	final public void setStartValue (T startValue) {
		this.startValue = startValue;
	}

	final public void setDuration (double duration) {
		this.duration = duration;
	}

	final public void start (T startValue) {
		start(startValue, Blaze3D.getTime());
	}

	final public void start (T startValue, double startTime) {
		this.startValue = startValue;
		this.startTime  = startTime;
	}

	final public void setTarget (T target) {
		targetValue = target;
	}

	abstract public void update (double now);

	final public T get () {
		return value;
	}

	final public void update () {
		update(Blaze3D.getTime());
	}
}
