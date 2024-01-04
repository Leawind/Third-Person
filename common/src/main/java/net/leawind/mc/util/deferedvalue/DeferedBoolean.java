package net.leawind.mc.util.deferedvalue;


/**
 * 成功 (T->F) 后一段时间内不能再次 (F->T)
 * <p>
 * 成功 (F->T) 后一段时间内不能再次 (T->F)
 */
public class DeferedBoolean {
	public  boolean value;
	public  boolean target;
	public  double  posDelay     = 0;
	public  double  negDelay     = 0;
	private double  posTimestamp = Double.NEGATIVE_INFINITY;
	private double  negTimestamp = Double.NEGATIVE_INFINITY;

	public DeferedBoolean (boolean initValue) {
		value = target = initValue;
	}

	public DeferedBoolean setDelay (double delay) {
		setDelay(delay, delay);
		return this;
	}

	public DeferedBoolean setDelay (double posDelay, double negDelay) {
		this.posDelay = posDelay;
		this.negDelay = negDelay;
		return this;
	}

	public DeferedBoolean set (Boolean targetValue) {
		target = targetValue;
		return this;
	}

	public void update (double t) {
		if (value == target) {
			return;
		}
		boolean newValue;
		if (target) {
			double period = t - posTimestamp;
			newValue = !(period < posDelay);
		} else {
			double period = t - negTimestamp;
			newValue = period < negDelay;
		}
		if (value != newValue) {
			if (newValue) {
				negTimestamp = t;
			} else {
				posTimestamp = t;
			}
		}
		value = newValue;
	}

	public boolean get (double t) {
		update(t);
		return value;
	}
}
