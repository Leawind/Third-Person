package net.leawind.mc.util.math.smoothvalue;


import com.mojang.blaze3d.Blaze3D;
import org.jetbrains.annotations.NotNull;

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
	protected @NotNull Function<Double, Double> func;
	protected @NotNull T                        value;
	protected          double                   duration  = 0;
	protected          double                   startTime = 0;
	protected @NotNull T                        startValue;
	protected @NotNull T                        endValue;

	public FuncSmoothValue (@NotNull Function<Double, Double> func, @NotNull T value, @NotNull T startValue, @NotNull T endValue) {
		this.func       = func;
		this.value      = value;
		this.startValue = startValue;
		this.endValue   = endValue;
	}

	public void setFunc (@NotNull Function<Double, Double> func) {
		this.func = func;
	}

	final public void setStartValue (@NotNull T startValue) {
		this.startValue = startValue;
	}

	final public void setDuration (double duration) {
		this.duration = duration;
	}

	final public void start (@NotNull T startValue) {
		start(startValue, Blaze3D.getTime());
	}

	final public void start (@NotNull T startValue, double startTime) {
		this.startValue = startValue;
		this.startTime  = startTime;
	}

	final public void setEndValue (@NotNull T endValue) {
		this.endValue = endValue;
	}

	abstract public void update (double now);

	final public @NotNull T get () {
		return value;
	}

	final public void update () {
		update(Blaze3D.getTime());
	}
}
