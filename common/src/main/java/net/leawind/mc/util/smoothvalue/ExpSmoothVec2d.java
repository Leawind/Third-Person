package net.leawind.mc.util.smoothvalue;


import net.leawind.mc.util.math.Vec2d;
import net.leawind.mc.util.math.Vectors;

@SuppressWarnings("unused")
public class ExpSmoothVec2d extends ExpSmoothValue<Vec2d> {
	public ExpSmoothVec2d () {
		value              = Vec2d.ZERO;
		target             = Vec2d.ZERO;
		smoothFactor       = Vec2d.ZERO;
		smoothFactorWeight = Vec2d.ONE;
	}

	public ExpSmoothVec2d setTarget (double x, double y) {
		this.target = new Vec2d(x, y);
		return this;
	}

	public ExpSmoothVec2d setValue (double x, double y) {
		this.value = new Vec2d(x, y);
		return this;
	}

	@Override
	public ExpSmoothVec2d update (double period) {
		super.preUpdate();
		value = Vectors.lerp(value, target, Vectors.pow(smoothFactor, smoothFactorWeight.multiply(period)).negated().add(1));
		return this;
	}

	@Override
	public Vec2d get (double delta) {
		return Vectors.lerp(lastValue, value, delta);
	}

	@Override
	public ExpSmoothVec2d setSmoothFactor (double smoothFactor) {
		return setSmoothFactor(smoothFactor, smoothFactor);
	}

	public ExpSmoothVec2d setSmoothFactor (double x, double y) {
		this.smoothFactor = new Vec2d(x, y);
		return this;
	}

	@Override
	public ExpSmoothVec2d setSmoothFactor (Vec2d multiplier, Vec2d deltaTime) {
		this.smoothFactor = new Vec2d(Math.pow(multiplier.x, 1 / deltaTime.x), Math.pow(multiplier.y, 1 / deltaTime.y));
		return this;
	}

	public ExpSmoothVec2d setSmoothFactorWeight (double weight) {
		return setSmoothFactorWeight(new Vec2d(weight));
	}

	public ExpSmoothVec2d setSmoothFactorWeight (double weightX, double weightY) {
		return setSmoothFactorWeight(new Vec2d(weightX, weightY));
	}

	public ExpSmoothVec2d setSmoothFactorWeight (Vec2d weight) {
		this.smoothFactorWeight = weight;
		return this;
	}
}
