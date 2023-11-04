package net.leawind.mc.util.smoothvalue;


import net.minecraft.world.phys.Vec3;

public class FuncSmoothVec3 extends FuncSmoothValue<Vec3> {
	@Override
	public FuncSmoothValue<Vec3> update (double now) {
		double k = func.apply((now - startTime) / duration);
		value = new Vec3(k * (targetValue.x - startValue.x) + startValue.x,
						 k * (targetValue.y - startValue.y) + startValue.y,
						 k * (targetValue.z - startValue.z) + startValue.z);
		return this;
	}
}
