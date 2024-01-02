package net.leawind.mc.util.smoothvalue;


import net.minecraft.util.Mth;

@SuppressWarnings("unused")
public class ExpSmoothDouble extends ExpSmoothValue<Double> {
	public ExpSmoothDouble () {
		value        = 0d;
		target       = 0d;
		smoothFactor = 0d;
	}

	public ExpSmoothDouble setValue (double value) {
		this.value = value;
		return this;
	}

	public ExpSmoothDouble setTarget (double target) {
		this.target = target;
		return this;
	}

	public ExpSmoothDouble setSmoothFactor (double k, double t) {
		this.smoothFactor = Math.pow(k, 1 / t);
		return this;
	}

	@Override
	public ExpSmoothDouble update (double tickTime) {
		super.preUpdate();
		value = Mth.lerp(1 - Math.pow(smoothFactor, tickTime), value, target);
		return this;
	}

	@Override
	public Double get (double delta) {
		return value + Mth.lerp(delta, 0, value - lastValue);
	}

	@Override
	public ExpSmoothValue<Double> setSmoothFactor (double smoothFactor) {
		this.smoothFactor = smoothFactor;
		return this;
	}

	@Override
	public ExpSmoothDouble setSmoothFactor (Double k, Double t) {
		this.smoothFactor = Math.pow(k, 1 / t);
		return this;
	}
}
