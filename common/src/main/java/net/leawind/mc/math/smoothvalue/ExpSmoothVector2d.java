package net.leawind.mc.math.smoothvalue;


import net.leawind.mc.math.vector.Vector2d;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ExpSmoothVector2d extends ExpSmoothValue<Vector2d> {
	public ExpSmoothVector2d () {
		super(new Vector2d(0), new Vector2d(1), new Vector2d(0), new Vector2d(0), new Vector2d(0));
	}

	public void setTarget (double x, double y) {
		this.target.set(x, y);
	}

	@Override
	public void setEndValue (@NotNull Vector2d v) {
		this.target.set(v);
	}

	public void setValue (double x, double y) {
		this.value.set(x, y);
	}

	@Override
	public void update (double period) {
		super.preUpdate();
		Vector2d t = smoothFactor.copy().pow(smoothFactorWeight.copy().mul(period)).negate().add(1);
		value = value.copy().lerp(target, t);
	}

	@Override
	public void setSmoothFactor (Vector2d s) {
		this.smoothFactor.set(s);
	}

	@Override
	public void set (Vector2d v) {
		value = target = v;
	}

	@Override
	public void setValue (Vector2d v) {
		value = v;
	}

	@Override
	public Vector2d get (double t) {
		return lastValue.copy().lerp(value, t);
	}

	@Override
	public void setSmoothFactor (double smoothFactor) {
		setSmoothFactor(smoothFactor, smoothFactor);
	}

	public void setSmoothFactor (double x, double y) {
		this.smoothFactor.set(x, y);
	}

	@Override
	public void setMT (Vector2d multiplier, Vector2d time) {
		if (multiplier.x < 0 || multiplier.x > 1) {
			throw new IllegalArgumentException("Multiplier.x should in [0,1]: " + multiplier.x);
		} else if (multiplier.y < 0 || multiplier.y > 1) {
			throw new IllegalArgumentException("Multiplier.y should in [0,1]: " + multiplier.y);
		} else if (time.x < 0 || time.y < 0) {
			throw new IllegalArgumentException("Invalid time, non-negative required, but got " + time);
		}
		this.smoothFactor.set(time.x == 0 ? 0: Math.pow(multiplier.x, 1 / time.x),//
							  time.y == 0 ? 0: Math.pow(multiplier.y, 1 / time.y));
	}

	@Override
	public void setHalflife (Vector2d halflife) {
		setMT(new Vector2d(0.5), halflife);
	}

	@Override
	public void setHalflife (double halflife) {
		setMT(new Vector2d(0.5), new Vector2d(halflife));
	}

	public void setSmoothFactorWeight (double w) {
		setSmoothFactorWeight(w, w);
	}

	public void setSmoothFactorWeight (Vector2d w) {
		this.smoothFactorWeight.set(w);
	}

	public void setSmoothFactorWeight (double x, double y) {
		this.smoothFactorWeight.set(x, y);
	}
}
