package net.leawind.mc.util.smoothvalue;


import net.leawind.mc.util.Vectors;
import net.minecraft.world.phys.Vec3;

public class ExpSmoothVec3f extends ExpSmoothValue<Vec3> {
	public ExpSmoothVec3f () {
		value        = Vec3.ZERO;
		target       = Vec3.ZERO;
		smoothFactor = Vec3.ZERO;
	}

	public ExpSmoothVec3f setTarget (float x, float y, float z) {
		this.target = new Vec3(x, y, z);
		return this;
	}

	public ExpSmoothVec3f setValue (float x, float y, float z) {
		this.value = new Vec3(x, y, z);
		return this;
	}

	@Override
	public ExpSmoothVec3f update (double tickTime) {
		super.preUpdate();
		value = Vectors.lerp(value, target, Vectors.pow(smoothFactor, tickTime).reverse().add(1, 1, 1));
		return this;
	}

	@Override
	public Vec3 get (float delta) {
		return Vectors.lerp(lastValue, value, delta);
	}

	@Override
	public ExpSmoothVec3f setSmoothFactor (double smoothFactor) {
		return setSmoothFactor(smoothFactor, smoothFactor, smoothFactor);
	}

	private ExpSmoothVec3f setSmoothFactor (double x, double y, double z) {
		this.smoothFactor = new Vec3(x, y, z);
		return this;
	}

	@Override
	public ExpSmoothVec3f setSmoothFactor (Vec3 k, Vec3 t) {
		this.smoothFactor = new Vec3(Math.pow(k.x, 1 / t.x), Math.pow(k.y, 1 / t.y), Math.pow(k.z, 1 / t.z));
		return this;
	}
}
