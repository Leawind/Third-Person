package net.leawind.mc.math.smoothvalue;


import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

@SuppressWarnings("unused")
public class FuncSmoothVec3 extends FuncSmoothValue<Vec3> {
	public FuncSmoothVec3 (Function<Double, Double> func) {
		super(func);
		value       = Vec3.ZERO;
		startValue  = Vec3.ZERO;
		targetValue = Vec3.ZERO;
	}

	@Override
	public void update (double now) {
		double k = func.apply((now - startTime) / duration);
		value = new Vec3(k * (targetValue.x - startValue.x) + startValue.x, k * (targetValue.y - startValue.y) + startValue.y, k * (targetValue.z - startValue.z) + startValue.z);
	}
}
