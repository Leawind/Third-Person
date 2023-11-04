package net.leawind.mc.util.smoothvalue;


import net.minecraft.util.Mth;

public class ExpSmoothDouble extends ExpSmoothValue<Double> {
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
