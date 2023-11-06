package net.leawind.mc.util;


import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("unused")
public class Vectors {
	/**
	 * 弧度制
	 *
	 * @param d 方向
	 * @return [x=俯仰角, y=偏航角]
	 */
	public static Vec2 rotationRadianFromDirection (Vec3 d) {
		d = d.normalize();
		return new Vec2((float)-Math.asin(d.y), (float)Math.atan2(-d.x, d.z));
	}

	/**
	 * 角度制
	 *
	 * @param d 方向
	 * @return [x=俯仰角, y=偏航角]
	 */
	public static Vec2 rotationAngleFromDirection (Vec3 d) {
		d = d.normalize();
		return new Vec2((float)(-Math.asin(d.y) * 180 / Math.PI), (float)(Math.atan2(-d.x, d.z) * 180 / Math.PI));
	}

	/**
	 * 将一个向量旋转到另一个向量的方向上
	 *
	 * @param vec       原向量
	 * @param direction 表示目标方向的向量
	 */
	public static Vec3 rotateTo (Vec3 vec, Vec3 direction) {
		return direction.normalize().scale(vec.length());
	}

	/**
	 * 将一个向量相对原本方向旋转一定弧度
	 *
	 * @param vec 原向量
	 * @param dy  偏航角变化量（弧度制）
	 * @param dx  俯仰角变化量（弧度制）
	 */
	public static Vec3 rotateRadian (Vec3 vec, float dy, float dx) {
		return Vec3.directionFromRotation(rotationRadianFromDirection(vec).add(new Vec2(dx, dy))).scale(vec.length());
	}

	/**
	 * 将一个向量相对原本方向旋转一定弧度
	 *
	 * @param vec      原向量
	 * @param rotation 弧度变化量（弧度制）
	 */
	public static Vec3 rotateRadian (Vec3 vec, Vec2 rotation) {
		return Vec3.directionFromRotation(rotationRadianFromDirection(vec).add(rotation)).scale(vec.length());
	}

	/**
	 * 将一个向量相对原本方向旋转一定角度
	 *
	 * @param vec 原向量
	 * @param dy  偏航角变化量（角度制）
	 * @param dx  俯仰角变化量（角度制）
	 */
	public static Vec3 rotateAngle (Vec3 vec, float dy, float dx) {
		return Vec3.directionFromRotation(rotationAngleFromDirection(vec).add(new Vec2(dx, dy))).scale(vec.length());
	}

	/**
	 * 将一个向量相对原本方向旋转一定角度
	 *
	 * @param vec           原向量
	 * @param rotationAngle 角度变化量（角度制）
	 */
	public static Vec3 rotateAngle (Vec3 vec, Vec2 rotationAngle) {
		return Vec3.directionFromRotation(rotationAngleFromDirection(vec).add(rotationAngle)).scale(vec.length());
	}

	public static double rotationRadianFromDirection (Vec2 d) {
		return -Math.atan2(d.x, d.y);
	}

	public static double rotationAngleFromDirection (Vec2 d) {
		return -Math.atan2(d.x, d.y) * 180 / Math.PI;
	}

	/**
	 * 各分量分别线性插值
	 *
	 * @param src 源向量
	 * @param dst 目标向量
	 * @param k   各分量插值系数
	 */
	public static Vec3 lerp (Vec3 src, Vec3 dst, Vec3 k) {
		return new Vec3(Mth.lerp(k.x, src.x, dst.x), Mth.lerp(k.y, src.y, dst.y), Mth.lerp(k.z, src.z, dst.z));
	}

	/**
	 * 各分量分别线性插值
	 *
	 * @param src 源向量
	 * @param dst 目标向量
	 * @param k   各分量插值系数
	 */
	public static Vec2 lerp (Vec2 src, Vec2 dst, Vec2 k) {
		return new Vec2(Mth.lerp(k.x, src.x, dst.x), Mth.lerp(k.y, src.y, dst.y));
	}

	/**
	 * 各分量分别求幂
	 */
	public static Vec3 pow (Vec3 v, Vec3 p) {
		return new Vec3(Math.pow(v.x, p.x), Math.pow(v.y, p.y), Math.pow(v.z, p.z));
	}

	/**
	 * 各分量分别求幂
	 */
	public static Vec2 pow (Vec2 v, Vec2 p) {
		return new Vec2((float)Math.pow(v.x, p.x), (float)Math.pow(v.y, p.y));
	}

	/**
	 * 各分量分别求幂
	 */
	public static Vec3 pow (Vec3 v, double p) {
		return new Vec3(Math.pow(v.x, p), Math.pow(v.y, p), Math.pow(v.z, p));
	}

	/**
	 * 各分量分别求幂
	 */
	public static Vec2 pow (Vec2 v, double p) {
		return new Vec2((float)Math.pow(v.x, p), (float)Math.pow(v.y, p));
	}

	public static Vec3 sigmoid (Vec3 v) {
		return new Vec3(1 / (1 + Math.exp(-v.x)), 1 / (1 + Math.exp(-v.y)), 1 / (1 + Math.exp(-v.z)));
	}
}
