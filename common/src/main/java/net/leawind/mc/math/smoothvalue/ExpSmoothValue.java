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
	 * 每隔 deltaTime 秒，value 变为原来的 multiplier 倍
	 */
	abstract ExpSmoothValue<T> setSmoothFactor (T multiplier, T deltaTime);

	abstract ExpSmoothValue<T> setSmoothFactorWeight (double smoothFactorWeight);

	abstract ExpSmoothValue<T> setSmoothFactorWeight (T smoothFactorWeight);
}
