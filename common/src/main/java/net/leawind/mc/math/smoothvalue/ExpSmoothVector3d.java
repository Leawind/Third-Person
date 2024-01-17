package net.leawind.mc.math.smoothvalue;


import net.leawind.mc.math.vector.Vector3d;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ExpSmoothVector3d extends ExpSmoothValue<Vector3d> {
	public ExpSmoothVector3d () {
		super(new Vector3d(0), new Vector3d(1), new Vector3d(0), new Vector3d(0), new Vector3d(0));
	}

	public void setTarget (double x, double y, double z) {
		this.target.set(x, y, z);
	}

	public void setValue (double x, double y, double z) {
		this.value.set(x, y, z);
	}

	@Override
	public void setEndValue (@NotNull Vector3d s) {
		this.target.set(s);
	}

	@Override
	public void update (double period) {
		super.preUpdate();
		Vector3d t = smoothFactor.copy().pow(smoothFactorWeight.copy().mul(period)).negate().add(1);
		value = value.copy().lerp(target, t);//TODO ?
	}

	@Override
	public void setSmoothFactor (Vector3d smoothFactor) {
		this.smoothFactor.set(smoothFactor);
	}

	@Override
	public Vector3d get (double t) {
		return lastValue.copy().lerp(value, t);
	}

	@Override
	public void set (Vector3d v) {
		value = target = v;
	}

	@Override
	public void setValue (Vector3d v) {
		value = v;
	}

	@Override
	public void setSmoothFactor (double d) {
		setSmoothFactor(d, d, d);
	}

	private void setSmoothFactor (double x, double y, double z) {
		this.smoothFactor.set(x, y, z);
	}

	@Override
	public void setMT (Vector3d multiplier, Vector3d time) {
		if (multiplier.x < 0 || multiplier.x > 1) {
			throw new IllegalArgumentException("Multiplier.x should in [0,1]: " + multiplier.x);
		} else if (multiplier.y < 0 || multiplier.y > 1) {
			throw new IllegalArgumentException("Multiplier.y should in [0,1]: " + multiplier.y);
		} else if (multiplier.z < 0 || multiplier.z > 1) {
			throw new IllegalArgumentException("Multiplier.z should in [0,1]: " + multiplier.z);
		} else if (time.x < 0 || time.y < 0 || time.z < 0) {
			throw new IllegalArgumentException("Invalid time, non-negative required, but got " + time);
		}
		this.smoothFactor.set(time.x == 0 ? 0: Math.pow(multiplier.x, 1 / time.x),//
							  time.y == 0 ? 0: Math.pow(multiplier.y, 1 / time.y),//
							  time.z == 0 ? 0: Math.pow(multiplier.z, 1 / time.z));
	}

	@Override
	public void setHalflife (Vector3d halflife) {
		setMT(new Vector3d(0.5), halflife);
	}

	@Override
	public void setHalflife (double halflife) {
		setMT(new Vector3d(0.5), new Vector3d(halflife));
	}

	public void setSmoothFactorWeight (double w) {
		setSmoothFactorWeight(w, w, w);
	}

	public void setSmoothFactorWeight (Vector3d w) {
		this.smoothFactorWeight.set(w);
	}

	public void setSmoothFactorWeight (double x, double y, double z) {
		this.smoothFactorWeight.set(x, y, z);
	}
}
