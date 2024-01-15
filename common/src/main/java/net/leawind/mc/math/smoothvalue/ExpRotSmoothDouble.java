package net.leawind.mc.math.smoothvalue;


import net.leawind.mc.math.LMath;

public class ExpRotSmoothDouble extends ExpSmoothDouble {
	public static ExpRotSmoothDouble createWithHalflife (double cycle, double halflife) {
		ExpRotSmoothDouble v = new ExpRotSmoothDouble(cycle);
		v.setHalflife(halflife);
		return v;
	}

	private double cycle;

	/**
	 * @param cycle 周期
	 */
	public ExpRotSmoothDouble (double cycle) {
		super();
		setCycle(cycle);
	}

	public double getCycle () {
		return cycle;
	}

	public void setCycle (double cycle) {
		this.cycle = cycle;
	}

	@Override
	public void setValue (double d) {
		d = LMath.floorMod(d, cycle);
		super.setValue(d);
	}

	@Override
	public void setTarget (double d) {
		d = LMath.floorMod(d, cycle);
		super.setTarget(d);
	}

	@Override
	public void set (Double d) {
		d = LMath.floorMod(d, cycle);
		super.set(d);
	}

	@Override
	public Double get (double t) {
		lastValue = LMath.floorMod(lastValue, cycle);
		value     = LMath.floorMod(value, cycle);
		double delta = LMath.floorMod(value - lastValue, cycle);
		if (delta > cycle / 2) {
			delta -= cycle;
		}
		value = lastValue + delta;
		return LMath.lerp(lastValue, value, t);
	}

	@Override
	public void update (double period) {
		super.preUpdate();
		value  = LMath.floorMod(value, cycle);
		target = LMath.floorMod(target, cycle);
		double delta = LMath.floorMod(target - value, cycle);
		if (delta > cycle / 2) {
			delta -= cycle;
		}
		target = value + delta;
		value  = LMath.lerp(value, target, 1 - Math.pow(smoothFactor, smoothFactorWeight * period));
	}

	@Override
	public void setHalflife (double halflife) {
		super.setHalflife(halflife);
	}
}
