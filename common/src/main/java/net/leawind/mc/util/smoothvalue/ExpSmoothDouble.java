package net.leawind.mc.util.smoothvalue;


import net.minecraft.util.Mth;

public class ExpSmoothDouble extends ExpSmoothValue<Double> {
	public ExpSmoothDouble setValue (double value) {
		this.value = value;
		return this;
	}

	public ExpSmoothDouble setTarget (double target) {
		this.target = target;
		return this;
	}

	public ExpSmoothDouble setDecayRatio (double k, double t) {
		this.decayRatio = Math.pow(k, 1 / t);
		return this;
	}

	@Override
	public ExpSmoothValue<Double> setDecayRatio (double decreaseSpeed) {
		this.decayRatio = decreaseSpeed;
		return this;
	}

	@Override
	public ExpSmoothDouble setDecayRatio (Double k, Double t) {
		this.decayRatio = Math.pow(k, 1 / t);
		return this;
	}

	@Override
	public ExpSmoothDouble update (double tickTime) {
		value = Mth.lerp(1 - Math.pow(decayRatio, tickTime), value, target);
		return this;
	}
}
