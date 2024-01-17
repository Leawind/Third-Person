package net.leawind.mc.math.smoothvalue;


import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class ExpSmoothValue<T> implements ISmoothValue<T> {
	public @NotNull    T smoothFactor;
	public @NotNull    T smoothFactorWeight;// factor ^ weight
	protected @NotNull T value;
	protected @NotNull T lastValue;
	public @NotNull    T target;

	protected ExpSmoothValue (@NotNull T sf, @NotNull T sw, @NotNull T v, @NotNull T lv, @NotNull T t) {
		smoothFactor       = sf;
		smoothFactorWeight = sw;
		value              = v;
		lastValue          = lv;
		target             = t;
	}

	@Override
	abstract public void setEndValue (@NotNull T endValue);

	/**
	 * @param period 经过的时间（s）
	 */
	@Override
	abstract public void update (double period);

	@Override
	final public @NotNull T get () {
		return value;
	}

	final public T getLast () {
		return lastValue;
	}

	/**
	 * 记录下更新前的值，存储在 lastValue 中。
	 */
	final protected void preUpdate () {
		lastValue = value;
	}

	abstract public void setValue (T value);

	abstract public void set (T value);

	abstract public void setSmoothFactor (T smoothFactor);

	/**
	 * lerp(lastValue, value, t)
	 */
	abstract public T get (double t);

	abstract void setSmoothFactor (double smoothFactor);

	/**
	 * 根据以下规则设置平滑系数：
	 * <p>
	 * 每隔 time 秒，value 变为原来的 multiplier 倍。
	 */
	abstract void setMT (T multiplier, T time);

	/**
	 * 根据半衰期设置平滑系数
	 */
	abstract void setHalflife (T halflife);

	/**
	 * 根据半衰期设置平滑系数
	 */
	abstract void setHalflife (double halflife);

	abstract void setSmoothFactorWeight (double smoothFactorWeight);

	abstract void setSmoothFactorWeight (T smoothFactorWeight);
}
