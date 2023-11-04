package net.leawind.mc.util.smoothvalue;


@SuppressWarnings("unused")
public abstract class ExpSmoothValue<T> implements ISmoothValue<T> {
	public    T value;
	protected T target;
	public    T decayRatio;

	final public T getValue () {
		return value;
	}

	final public ExpSmoothValue<T> setTarget (T target) {
		this.target = target;
		return this;
	}

	final public ExpSmoothValue<T> setValue (T value) {
		this.value = value;
		return this;
	}

	final public ExpSmoothValue<T> setDecayRatio (T decreaseSpeed) {
		this.decayRatio = decreaseSpeed;
		return this;
	}

	abstract ExpSmoothValue<T> setDecayRatio (double decreaseSpeed);

	/**
	 * 每隔 deltaTime 秒，value 变为原来的 multiplier 倍
	 */
	abstract ExpSmoothValue<T> setDecayRatio (T multiplier, T deltaTime);

	/**
	 * @param tickTime 经过的时间（s）
	 */
	abstract public ExpSmoothValue<T> update (double tickTime);
}
