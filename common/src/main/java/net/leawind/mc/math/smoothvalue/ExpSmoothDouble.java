package net.leawind.mc.math.smoothvalue;


import net.leawind.mc.math.LMath;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ExpSmoothDouble extends ExpSmoothValue<Double> {
	public static ExpSmoothDouble createWithHalflife (double halflife) {
		ExpSmoothDouble v = new ExpSmoothDouble();
		v.setHalflife(halflife);
		return v;
	}

	public ExpSmoothDouble () {
		super(0D, 1D, 0D, 0D, 0D);
	}

	public void setTarget (double target) {
		this.target = target;
	}

	public void setSmoothFactor (double k, double t) {
		this.smoothFactor = Math.pow(k, 1 / t);
	}

	public void setSmoothFactorWeight (double weight) {
		this.smoothFactorWeight = weight;
	}

	public void setSmoothFactorWeight (Double weight) {
		this.smoothFactorWeight = weight;
	}

	@Override
	public void setEndValue (@NotNull Double endValue) {
		this.target = endValue;
	}

	@Override
	public void update (double period) {
		super.preUpdate();
		value = LMath.lerp(value, target, 1 - Math.pow(smoothFactor, smoothFactorWeight * period));
	}

	@Override
	public void set (Double d) {
		value = target = d;
	}

	public void setValue (double d) {
		value = d;
	}

	@Override
	public void setValue (Double d) {
		value = d;
	}

	@Override
	public void setSmoothFactor (Double smoothFactor) {
		this.smoothFactor = smoothFactor;
	}

	@Override
	public Double get (double t) {
		return Mth.lerp(t, lastValue, value);
	}

	@Override
	public void setSmoothFactor (double smoothFactor) {
		this.smoothFactor = smoothFactor;
	}

	@Override
	public void setMT (Double multiplier, Double time) {
		if (multiplier < 0 || multiplier > 1) {
			throw new IllegalArgumentException("Multiplier should in [0,1]: " + multiplier);
		} else if (time < 0) {
			throw new IllegalArgumentException("Invalid time, non-negative required, but got " + time);
		}
		setSmoothFactor(time == 0 ? 0: Math.pow(multiplier, 1 / time));
	}

	@Override
	public void setHalflife (Double halflife) {
		setMT(0.5, halflife);
	}

	@Override
	public void setHalflife (double halflife) {
		setMT(0.5, halflife);
	}
}
