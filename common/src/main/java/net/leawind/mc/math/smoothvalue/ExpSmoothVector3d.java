package net.leawind.mc.math.smoothvalue;


import net.leawind.mc.math.vector.Vector3d;

@SuppressWarnings("unused")
public class ExpSmoothVector3d extends ExpSmoothValue<Vector3d> {
	public ExpSmoothVector3d () {
		value              = new Vector3d(0);
		target             = new Vector3d(0);
		smoothFactor       = new Vector3d(0);
		smoothFactorWeight = new Vector3d(1);
	}

	public ExpSmoothVector3d setTarget (double x, double y, double z) {
		this.target.set(x, y, z);
		return this;
	}

	public ExpSmoothVector3d setValue (double x, double y, double z) {
		this.value.set(x, y, z);
		return this;
	}

	@Override
	public ExpSmoothVector3d setTarget (Vector3d s) {
		this.target.set(s);
		return this;
	}

	@Override
	public ExpSmoothVector3d update (double period) {
		super.preUpdate();
		Vector3d t = smoothFactor.copy().pow(smoothFactorWeight.copy().mul(period)).negate().add(1);
		value = value.copy().lerp(target, t);//TODO ?
		return this;
	}

	@Override
	public ExpSmoothVector3d setSmoothFactor (Vector3d smoothFactor) {
		this.smoothFactor.set(smoothFactor);
		return this;
	}

	@Override
	public Vector3d get (double delta) {
		return lastValue.copy().lerp(value, delta);
	}

	@Override
	public ExpSmoothVector3d set (Vector3d v) {
		value = target = v;
		return this;
	}

	@Override
	public ExpSmoothVector3d setValue (Vector3d v) {
		value = v;
		return this;
	}

	@Override
	public ExpSmoothVector3d setSmoothFactor (double d) {
		return setSmoothFactor(d, d, d);
	}

	private ExpSmoothVector3d setSmoothFactor (double x, double y, double z) {
		this.smoothFactor.set(x, y, z);
		return this;
	}

	@Override
	public ExpSmoothVector3d setMT (Vector3d multiplier, Vector3d time) {
		this.smoothFactor.set(Math.pow(multiplier.x, 1 / time.x), Math.pow(multiplier.y, 1 / time.y), Math.pow(multiplier.z, 1 / time.z));
		return this;
	}

	@Override
	public ExpSmoothVector3d setHalflife (Vector3d halflife) {
		return setMT(new Vector3d(0.5), halflife);
	}

	@Override
	public ExpSmoothVector3d setHalflife (double halflife) {
		return setMT(new Vector3d(0.5), new Vector3d(halflife));
	}

	public ExpSmoothVector3d setSmoothFactorWeight (double w) {
		return setSmoothFactorWeight(w, w, w);
	}

	public ExpSmoothVector3d setSmoothFactorWeight (Vector3d w) {
		this.smoothFactorWeight.set(w);
		return this;
	}

	public ExpSmoothVector3d setSmoothFactorWeight (double x, double y, double z) {
		this.smoothFactorWeight.set(x, y, z);
		return this;
	}
}
