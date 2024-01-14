package net.leawind.mc.math.smoothvalue;


@SuppressWarnings("unused")
public abstract class ExpSmoothValue<T> implements ISmoothValue<T> {
	public    T smoothFactor;
	public    T smoothFactorWeight;// factor ^ weight
	protected T value;
	protected T lastValue;
	public    T target;

	@Override
	abstract public ExpSmoothValue<T> setTarget (T target);

	/**
	 * @param period 经过的时间（s）
	 */
	@Override
	abstract public ExpSmoothValue<T> update (double period);

	@Override
	final public T get () {
		return value;
	}

	final public T getLast () {
		return lastValue;
	}

	final protected void preUpdate () {
		lastValue = value;
	}

	abstract public ExpSmoothValue<T> setValue (T value);

	abstract public ExpSmoothValue<T> set (T value);

	abstract public ExpSmoothValue<T> setSmoothFactor (T smoothFactor);

	abstract public T get (double delta);

	abstract ExpSmoothValue<T> setSmoothFactor (double smoothFactor);

	/**
	 * 根据以下规则设置平滑系数：
	 * <p>
	 * 每隔 time 秒，value 变为原来的 multiplier 倍。
	 */
	abstract ExpSmoothValue<T> setMT (T multiplier, T time);

	/**
	 * 根据半衰期设置平滑系数
	 */
	abstract ExpSmoothValue<T> setHalflife (T halflife);

	/**
	 * 根据半衰期设置平滑系数
	 */
	abstract ExpSmoothValue<T> setHalflife (double halflife);

	abstract ExpSmoothValue<T> setSmoothFactorWeight (double smoothFactorWeight);

	abstract ExpSmoothValue<T> setSmoothFactorWeight (T smoothFactorWeight);
}
