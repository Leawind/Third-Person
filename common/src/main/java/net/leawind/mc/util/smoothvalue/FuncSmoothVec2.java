package net.leawind.mc.util.smoothvalue;


import net.minecraft.world.phys.Vec2;

public class FuncSmoothVec2 extends FuncSmoothValue<Vec2> {
	public FuncSmoothVec2 () {
		value       = Vec2.ZERO;
		startValue  = Vec2.ZERO;
		targetValue = Vec2.ZERO;
	}

	@Override
	public FuncSmoothValue<Vec2> update (double now) {
		float k = func.apply((now - startTime) / duration).floatValue();
		value = new Vec2(k * (targetValue.x - startValue.x) + startValue.x, k * (targetValue.y - startValue.y) + startValue.y);
		return this;
	}
}
