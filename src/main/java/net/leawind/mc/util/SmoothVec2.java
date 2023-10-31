package net.leawind.mc.util;

import net.minecraft.world.phys.Vec2;

/**
 * 平滑（指数函数）
 */

@SuppressWarnings("unused")
public class SmoothVec2 {
	public Vec2 target   = Vec2.ZERO;
	public Vec2 value    = Vec2.ZERO;
	public Vec2 decrease = Vec2.ZERO;

	public SmoothVec2 setTarget(Vec2 target){
		this.target = target;
		return this;
	}

	public SmoothVec2 setValue(Vec2 value){
		this.value = value;
		return this;
	}

	public SmoothVec2 setDecrease(Vec2 decrease){
		this.decrease = decrease;
		return this;
	}

	/**
	 * 每隔 t 秒变为原来的 k 倍
	 */
	public SmoothVec2 setDecrease(Vec2 k, Vec2 t){
		this.decrease = new Vec2((float)Math.pow(k.x, 1 / t.x), (float)Math.pow(k.y, 1 / t.y));
		return this;
	}

	/**
	 * @param duration 经过的时间（s）
	 */
	public void update(double duration){
		value = Vectors.lerp(value, target, Vectors.pow(decrease, duration).negated().add(1f));
	}
}
