package net.leawind.mc.util.smoothvalue;


import net.leawind.mc.util.math.Vectors;
import net.minecraft.world.phys.Vec2;

@SuppressWarnings("unused")
public class ExpSmoothVec2f extends ExpSmoothValue<Vec2> {
	public ExpSmoothVec2f () {
		value        = Vec2.ZERO;
		target       = Vec2.ZERO;
		smoothFactor = Vec2.ZERO;
	}

	public ExpSmoothVec2f setTarget (float x, float y) {
		this.target = new Vec2(x, y);
		return this;
	}

	public ExpSmoothVec2f setValue (float x, float y) {
		this.value = new Vec2(x, y);
		return this;
	}

	@Override
	public ExpSmoothVec2f update (double tickTime) {
		super.preUpdate();
		value = Vectors.lerp(value, target, Vectors.pow(smoothFactor, tickTime).negated().add(1f));
		return this;
	}

	@Override
	public Vec2 get (double delta) {
		return Vectors.lerp(lastValue, value, delta);
	}

	@Override
	public ExpSmoothVec2f setSmoothFactor (double smoothFactor) {
		return setSmoothFactor((float)smoothFactor, (float)smoothFactor);
	}

	public ExpSmoothVec2f setSmoothFactor (float x, float y) {
		this.smoothFactor = new Vec2(x, y);
		return this;
	}

	@Override
	public ExpSmoothVec2f setSmoothFactor (Vec2 multiplier, Vec2 deltaTime) {
		this.smoothFactor = new Vec2((float)Math.pow(multiplier.x, 1 / deltaTime.x),
									 (float)Math.pow(multiplier.y, 1 / deltaTime.y));
		return this;
	}
}
