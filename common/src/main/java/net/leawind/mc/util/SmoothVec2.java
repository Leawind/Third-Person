package net.leawind.mc.util;


import net.minecraft.world.phys.Vec2;

/**
 * 平滑（指数函数）
 */
@SuppressWarnings("unused")
public class SmoothVec2 {
	public Vec2 target        = Vec2.ZERO;
	public Vec2 value         = Vec2.ZERO;
	public Vec2 decreaseSpeed = Vec2.ZERO;

	public SmoothVec2 setTarget (Vec2 target) {
		this.target = target;
		return this;
	}

	public SmoothVec2 setTarget (float x, float y) {
		this.target = new Vec2(x, y);
		return this;
	}

	public SmoothVec2 setValue (Vec2 value) {
		this.value = value;
		return this;
	}

	public SmoothVec2 setValue (float x, float y) {
		this.value = new Vec2(x, y);
		return this;
	}

	public SmoothVec2 setDecreaseSpeed (Vec2 decreaseSpeed) {
		this.decreaseSpeed = decreaseSpeed;
		return this;
	}

	public SmoothVec2 setDecreaseSpeed (float x, float y) {
		this.decreaseSpeed = new Vec2(x, y);
		return this;
	}

	/**
	 * 每隔 deltaTime 秒变为原来的 multiplier 倍
	 */
	public SmoothVec2 setDecreaseSpeed (Vec2 multiplier, Vec2 deltaTime) {
		this.decreaseSpeed = new Vec2((float)Math.pow(multiplier.x, 1 / deltaTime.x),
									  (float)Math.pow(multiplier.y, 1 / deltaTime.y));
		return this;
	}

	/**
	 * @param deltaTime 经过的时间（s）
	 */
	public SmoothVec2 update (double deltaTime) {
		value = Vectors.lerp(value, target, Vectors.pow(decreaseSpeed, deltaTime).negated().add(1f));
		return this;
	}
}
