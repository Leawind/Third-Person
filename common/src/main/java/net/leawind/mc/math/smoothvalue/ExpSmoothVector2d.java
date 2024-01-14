package net.leawind.mc.math.smoothvalue;


import net.leawind.mc.math.vector.Vector2d;

@SuppressWarnings("unused")
public class ExpSmoothVector2d extends ExpSmoothValue<Vector2d> {
	public ExpSmoothVector2d () {
		value              = new Vector2d(0);
		target             = new Vector2d(0);
		smoothFactor       = new Vector2d(0);
		smoothFactorWeight = new Vector2d(1);
	}

	public ExpSmoothVector2d setTarget (double x, double y) {
		this.target.set(x, y);
		return this;
	}

	@Override
	public ExpSmoothVector2d setTarget (Vector2d v) {
		this.target.set(v);
		return this;
	}

	public ExpSmoothVector2d setValue (double x, double y) {
		this.value.set(x, y);
		return this;
	}

	@Override
	public ExpSmoothVector2d update (double period) {
		super.preUpdate();
		Vector2d t = smoothFactor.copy().pow(smoothFactorWeight.copy().mul(period)).negate().add(1);
		value = value.copy().lerp(target, t);
		return this;
	}

	@Override
	public ExpSmoothVector2d setSmoothFactor (Vector2d s) {
		this.smoothFactor.set(s);
		return this;
	}

	@Override
	public ExpSmoothVector2d set (Vector2d v) {
		value = target = v;
		return this;
	}

	@Override
	public ExpSmoothVector2d setValue (Vector2d v) {
		value = v;
		return this;
	}

	@Override
	public Vector2d get (double delta) {
		return lastValue.copy().lerp(value, delta);
	}

	@Override
	public ExpSmoothVector2d setSmoothFactor (double smoothFactor) {
		return setSmoothFactor(smoothFactor, smoothFactor);
	}

	public ExpSmoothVector2d setSmoothFactor (double x, double y) {
		this.smoothFactor.set(x, y);
		return this;
	}

	@Override
	public ExpSmoothVector2d setSmoothFactor (Vector2d multiplier, Vector2d deltaTime) {
		this.smoothFactor.set(Math.pow(multiplier.x, 1 / deltaTime.x), Math.pow(multiplier.y, 1 / deltaTime.y));
		return this;
	}

	public ExpSmoothVector2d setSmoothFactorWeight (double w) {
		return setSmoothFactorWeight(w, w);
	}

	public ExpSmoothVector2d setSmoothFactorWeight (Vector2d w) {
		this.smoothFactorWeight.set(w);
		return this;
	}

	public ExpSmoothVector2d setSmoothFactorWeight (double x, double y) {
		this.smoothFactorWeight.set(x, y);
		return this;
	}
}
