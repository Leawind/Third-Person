package net.leawind.mc.util.math.smoothvalue;


import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@SuppressWarnings("unused")
public class FuncSmoothVec3 extends FuncSmoothValue<Vec3> {
	public FuncSmoothVec3 (@NotNull Function<Double, Double> func) {
		super(func, Vec3.ZERO, Vec3.ZERO, Vec3.ZERO);
	}

	@Override
	public void update (double now) {
		double k = func.apply((now - startTime) / duration);
		value = new Vec3(k * (endValue.x - startValue.x) + startValue.x, k * (endValue.y - startValue.y) + startValue.y, k * (endValue.z - startValue.z) + startValue.z);
	}
}
