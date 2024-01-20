package net.leawind.mc.util.math.smoothvalue;


import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class ExpSmoothValue<T> implements ISmoothValue<T> {
	/**
	 * 平滑系数，越大越平滑
	 */
	public @NotNull    T smoothFactor;
	/**
	 * 平滑系数乘数，默认应为1
	 */
	public @NotNull    T smoothFactorWeight;// factor ^ weight
	/**
	 * 目标值
	 */
	public @NotNull    T target;
	/**
	 * 当前平滑的值
	 */
	protected @NotNull T value;
	/**
	 * 上次更新时的目标值
	 */
	protected @NotNull T lastValue;

	protected ExpSmoothValue (@NotNull T sf, @NotNull T sw, @NotNull T v, @NotNull T lv, @NotNull T t) {
		smoothFactor       = sf;
		smoothFactorWeight = sw;
		value              = v;
		lastValue          = lv;
		target             = t;
	}

	@Override
	final public @NotNull T get () {
		return value;
	}

	@Override
	abstract public T get (double t);

	@Override
	final public T getLast () {
		return lastValue;
	}

	final public void update (double period) {
		saveLastValue();
		udpateWithOutSavingLastValue(period);
	}

	/**
	 * 记录下更新前的平滑值（旧值），存储在 lastValue 中。
	 * <p>
	 * 应当在 update 方法中写入新值前调用
	 */
	final protected void saveLastValue () {
		lastValue = value;
	}

	abstract protected void udpateWithOutSavingLastValue (double period);

	abstract public void setValue (T value);

	abstract public void set (T value);

	abstract public void setSmoothFactor (T smoothFactor);

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
