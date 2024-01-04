package net.leawind.mc.util.smoothvalue;


import net.leawind.mc.util.math.Vectors;
import org.joml.Vector3d;

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
	public ExpSmoothVector3d update (double period) {
		super.preUpdate();
		value = Vectors.lerp(value, target, Vectors.pow(smoothFactor, smoothFactorWeight.mul(period)).mul(-1).add(1, 1, 1));
		return this;
	}

	@Override
	public Vector3d get (double delta) {
		return Vectors.lerp(lastValue, value, delta);
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
	public ExpSmoothVector3d setSmoothFactor (Vector3d k, Vector3d t) {
		this.smoothFactor.set(Math.pow(k.x, 1 / t.x), Math.pow(k.y, 1 / t.y), Math.pow(k.z, 1 / t.z));
		return this;
	}

	public ExpSmoothVector3d setSmoothFactorWeight (double w) {
		return setSmoothFactorWeight(w, w, w);
	}

	public ExpSmoothVector3d setSmoothFactorWeight (Vector3d w) {
		return setSmoothFactorWeight(w.x, w.y, w.z);
	}

	public ExpSmoothVector3d setSmoothFactorWeight (double x, double y, double z) {
		this.smoothFactorWeight.set(x, y, z);
		return this;
	}
}
