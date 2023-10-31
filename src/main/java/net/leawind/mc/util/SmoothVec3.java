package net.leawind.mc.util;

import net.minecraft.world.phys.Vec3;

/**
 * 平滑（指数函数）
 */

@SuppressWarnings("unused")
public class SmoothVec3 {
	public Vec3 target   = Vec3.ZERO;
	public Vec3 value    = Vec3.ZERO;
	public Vec3 decrease = Vec3.ZERO;

	public SmoothVec3 setTarget(Vec3 target){
		this.target = target;
		return this;
	}

	public SmoothVec3 setValue(Vec3 value){
		this.value = value;
		return this;
	}

	public SmoothVec3 setDecrease(Vec3 decrease){
		this.decrease = decrease;
		return this;
	}

	/**
	 * 每隔 t 秒变为原来的 k 倍
	 */
	public SmoothVec3 setDecrease(Vec3 k, Vec3 t){
		this.decrease = new Vec3(Math.pow(k.x, 1 / t.x), Math.pow(k.y, 1 / t.y), Math.pow(k.z, 1 / t.z));
		return this;
	}

	/**
	 * @param duration 经过的时间（s）
	 */
	public void update(double duration){
		value = Vectors.lerp(value, target, Vectors.pow(decrease, duration).reverse().add(1, 1, 1));
	}
}
