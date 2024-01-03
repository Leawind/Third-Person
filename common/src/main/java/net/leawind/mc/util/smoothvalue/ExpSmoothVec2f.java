package net.leawind.mc.util.smoothvalue;


import net.leawind.mc.util.math.Vec2f;
import net.leawind.mc.util.math.Vectors;

@SuppressWarnings("unused")
public class ExpSmoothVec2f extends ExpSmoothValue<Vec2f> {
	public ExpSmoothVec2f () {
		value              = Vec2f.ZERO;
		target             = Vec2f.ZERO;
		smoothFactor       = Vec2f.ZERO;
		smoothFactorWeight = Vec2f.ONE;
	}

	public ExpSmoothVec2f setTarget (float x, float y) {
		this.target = new Vec2f(x, y);
		return this;
	}

	public ExpSmoothVec2f setValue (float x, float y) {
		this.value = new Vec2f(x, y);
		return this;
	}

	@Override
	public ExpSmoothVec2f update (double period) {
		super.preUpdate();
		value = Vectors.lerp(value, target, Vectors.pow(smoothFactor, smoothFactorWeight.multiply((float)period))
												   .negated()
												   .add(1f));
		return this;
	}

	@Override
	public Vec2f get (double delta) {
		return Vectors.lerp(lastValue, value, delta);
	}

	@Override
	public ExpSmoothVec2f setSmoothFactor (double smoothFactor) {
		return setSmoothFactor((float)smoothFactor, (float)smoothFactor);
	}

	public ExpSmoothVec2f setSmoothFactor (float x, float y) {
		this.smoothFactor = new Vec2f(x, y);
		return this;
	}

	@Override
	public ExpSmoothVec2f setSmoothFactor (Vec2f multiplier, Vec2f deltaTime) {
		this.smoothFactor = new Vec2f((float)Math.pow(multiplier.x, 1 / deltaTime.x),
									  (float)Math.pow(multiplier.y, 1 / deltaTime.y));
		return this;
	}

	public ExpSmoothVec2f setSmoothFactorWeight (double weight) {
		return setSmoothFactorWeight(new Vec2f(weight));
	}

	public ExpSmoothVec2f setSmoothFactorWeight (float weightX, float weightY) {
		return setSmoothFactorWeight(new Vec2f(weightX, weightY));
	}

	public ExpSmoothVec2f setSmoothFactorWeight (Vec2f weight) {
		this.smoothFactorWeight = weight;
		return this;
	}
}
