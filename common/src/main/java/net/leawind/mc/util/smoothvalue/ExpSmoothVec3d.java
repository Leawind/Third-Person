package net.leawind.mc.util.smoothvalue;


import net.leawind.mc.util.math.Vec3d;
import net.leawind.mc.util.math.Vectors;

@SuppressWarnings("unused")
public class ExpSmoothVec3d extends ExpSmoothValue<Vec3d> {
	public ExpSmoothVec3d () {
		value              = Vec3d.ZERO;
		target             = Vec3d.ZERO;
		smoothFactor       = Vec3d.ZERO;
		smoothFactorWeight = Vec3d.ONE;
	}

	public ExpSmoothVec3d setTarget (double x, double y, double z) {
		this.target = new Vec3d(x, y, z);
		return this;
	}

	public ExpSmoothVec3d setValue (double x, double y, double z) {
		this.value = new Vec3d(x, y, z);
		return this;
	}

	@Override
	public ExpSmoothVec3d update (double period) {
		super.preUpdate();
		value = Vectors.lerp(value, target, Vectors.pow(smoothFactor, smoothFactorWeight.multiply(period))
												   .scale(-1)
												   .add(1, 1, 1));
		return this;
	}

	@Override
	public Vec3d get (double delta) {
		return Vectors.lerp(lastValue, value, delta);
	}

	@Override
	public ExpSmoothVec3d setSmoothFactor (double d) {
		return setSmoothFactor(d, d, d);
	}

	private ExpSmoothVec3d setSmoothFactor (double x, double y, double z) {
		this.smoothFactor = new Vec3d(x, y, z);
		return this;
	}

	@Override
	public ExpSmoothVec3d setSmoothFactor (Vec3d k, Vec3d t) {
		this.smoothFactor = new Vec3d(Math.pow(k.x, 1 / t.x), Math.pow(k.y, 1 / t.y), Math.pow(k.z, 1 / t.z));
		return this;
	}

	public ExpSmoothVec3d setSmoothFactorWeight (double weight) {
		return setSmoothFactorWeight(new Vec3d(weight));
	}

	public ExpSmoothVec3d setSmoothFactorWeight (double x, double y, double z) {
		return setSmoothFactorWeight(new Vec3d(x, y, z));
	}

	public ExpSmoothVec3d setSmoothFactorWeight (Vec3d weight) {
		this.smoothFactorWeight = weight;
		return this;
	}
}
