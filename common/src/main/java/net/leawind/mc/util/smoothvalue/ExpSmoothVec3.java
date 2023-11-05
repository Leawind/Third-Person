package net.leawind.mc.util.smoothvalue;


import net.leawind.mc.util.Vectors;
import net.minecraft.world.phys.Vec3;

public class ExpSmoothVec3 extends ExpSmoothValue<Vec3> {
	private ExpSmoothVec3 setSmoothFactor (double x, double y, double z) {
		this.smoothFactor = new Vec3(x, y, z);
		return this;
	}

	@Override
	public ExpSmoothVec3 setSmoothFactor (double decrease) {
		return setSmoothFactor(decrease, decrease, decrease);
	}

	@Override
	public ExpSmoothVec3 setSmoothFactor (Vec3 k, Vec3 t) {
		this.smoothFactor = new Vec3(Math.pow(k.x, 1 / t.x), Math.pow(k.y, 1 / t.y), Math.pow(k.z, 1 / t.z));
		return this;
	}

	@Override
	public ExpSmoothVec3 update (double tickTime) {
		value = Vectors.lerp(value, target, Vectors.pow(smoothFactor, tickTime).reverse().add(1, 1, 1));
		return this;
	}
}
