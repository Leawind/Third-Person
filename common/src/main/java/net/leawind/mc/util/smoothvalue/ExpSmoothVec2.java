package net.leawind.mc.util.smoothvalue;


import net.leawind.mc.util.Vectors;
import net.minecraft.world.phys.Vec2;

public class ExpSmoothVec2 extends ExpSmoothValue<Vec2> {
	public ExpSmoothVec2 setTarget (float x, float y) {
		this.target = new Vec2(x, y);
		return this;
	}

	public ExpSmoothVec2 setValue (float x, float y) {
		this.value = new Vec2(x, y);
		return this;
	}

	public ExpSmoothVec2 setDecayRatio (float x, float y) {
		this.decayRatio = new Vec2(x, y);
		return this;
	}

	@Override
	public ExpSmoothVec2 setDecayRatio (double decreaseSpeed) {
		return setDecayRatio((float)decreaseSpeed, (float)decreaseSpeed);
	}

	@Override
	public ExpSmoothVec2 setDecayRatio (Vec2 multiplier, Vec2 deltaTime) {
		this.decayRatio = new Vec2((float)Math.pow(multiplier.x, 1 / deltaTime.x),
								   (float)Math.pow(multiplier.y, 1 / deltaTime.y));
		return this;
	}

	@Override
	public ExpSmoothVec2 update (double tickTime) {
		value = Vectors.lerp(value, target, Vectors.pow(decayRatio, tickTime).negated().add(1f));
		return this;
	}
}
