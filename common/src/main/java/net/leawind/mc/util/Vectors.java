package net.leawind.mc.util;


import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2d;
import org.joml.Vector3d;

@SuppressWarnings("unused")
public class Vectors {
	/**
	 * 将一个向量旋转到另一个向量的方向上
	 *
	 * @param vec       原向量
	 * @param direction 表示目标方向的向量
	 */
	public static Vector3d rotateTo (Vector3d vec, Vector3d direction) {
		return direction.normalize(vec.length());
	}

	/**
	 * 将一个向量相对原本方向旋转一定弧度
	 *
	 * @param vec 原向量
	 * @param dy  偏航角变化量（弧度制）
	 * @param dx  俯仰角变化量（弧度制）
	 */
	public static Vector3d rotateRadian (Vector3d vec, float dy, float dx) {
		return directionFromRotationDegree(rotationRadianFromDirection(vec).add(new Vector2d(dx, dy))).mul(vec.length());
	}

	/**
	 * 弧度制
	 *
	 * @param d 方向
	 * @return [x=俯仰角, y=偏航角]
	 */
	public static Vector2d rotationRadianFromDirection (Vector3d d) {
		d = d.normalize();
		return new Vector2d(-Math.asin(d.y), Math.atan2(-d.x, d.z));
	}

	/**
	 * 将一个向量相对原本方向旋转一定弧度
	 *
	 * @param vec      原向量
	 * @param rotation 弧度变化量（弧度制）
	 */
	public static Vector3d rotateRadian (Vector3d vec, Vector2d rotation) {
		return directionFromRotationDegree(rotationRadianFromDirection(vec).add(rotation)).mul(vec.length());
	}

	/**
	 * 将一个向量相对原本方向旋转一定角度
	 *
	 * @param vec 原向量
	 * @param dy  偏航角变化量（角度制）
	 * @param dx  俯仰角变化量（角度制）
	 */
	public static Vector3d rotateDegree (Vector3d vec, double dy, double dx) {
		return directionFromRotationDegree(rotationDegreeFromDirection(vec).add(new Vector2d(dx, dy))).mul(vec.length());
	}

	/**
	 * 角度制
	 *
	 * @param d 方向
	 * @return [x=俯仰角, y=偏航角]
	 */
	public static Vector2d rotationDegreeFromDirection (Vector3d d) {
		d = d.normalize();
		return new Vector2d((-Math.toDegrees(Math.asin(d.y))), Math.toDegrees(Math.atan2(-d.x, d.z)));
	}

	/**
	 * 将一个向量相对原本方向旋转一定角度
	 *
	 * @param vec           原向量
	 * @param rotationAngle 角度变化量（角度制）
	 */
	public static Vector3d rotateDegree (Vector3d vec, Vector2d rotationAngle) {
		return directionFromRotationDegree(rotationDegreeFromDirection(vec).add(rotationAngle)).mul(vec.length());
	}

	public static double rotationDegreeFromDirection (Vector2d d) {
		return -Math.toDegrees(Math.atan2(d.x, d.y));
	}

	public static Vector3d directionFromRotationDegree (Vector2d r) {
		return directionFromRotationDegree(r.x, r.y);
	}

	public static Vector3d directionFromRotationDegree (double x, double y) {
		double h = Math.cos(-y * 0.017453292519943295 - Math.PI);
		double i = Math.sin(-y * 0.017453292519943295 - Math.PI);
		double j = -Math.cos(-x * 0.017453292519943295);
		double k = Math.sin(-x * 0.017453292519943295);
		return new Vector3d(i * j, k, h * j);
	}

	/**
	 * 各分量分别线性插值
	 *
	 * @param src 源向量
	 * @param dst 目标向量
	 * @param k   各分量插值系数
	 */
	public static Vector3d lerp (Vector3d src, Vector3d dst, Vector3d k) {
		return new Vector3d(Mth.lerp(k.x, src.x, dst.x), Mth.lerp(k.y, src.y, dst.y), Mth.lerp(k.z, src.z, dst.z));
	}

	public static Vector2d lerp (Vector2d src, Vector2d dst, Vector2d k) {
		return new Vector2d(Mth.lerp(k.x, src.x, dst.x), Mth.lerp(k.y, src.y, dst.y));
	}

	public static Vector2d lerp (Vector2d src, Vector2d dst, double k) {
		return new Vector2d(Mth.lerp(k, src.x, dst.x), Mth.lerp(k, src.y, dst.y));
	}

	public static Vector3d lerp (Vector3d src, Vector3d dst, double k) {
		return new Vector3d(Mth.lerp(k, src.x, dst.x), Mth.lerp(k, src.y, dst.y), Mth.lerp(k, src.z, dst.z));
	}

	/**
	 * 各分量分别求幂
	 */
	public static Vector3d pow (Vector3d v, Vector3d p) {
		return new Vector3d(Math.pow(v.x, p.x), Math.pow(v.y, p.y), Math.pow(v.z, p.z));
	}

	public static Vector2d pow (Vector2d v, Vector2d p) {
		return new Vector2d(Math.pow(v.x, p.x), Math.pow(v.y, p.y));
	}

	/**
	 * 各分量分别求幂
	 */
	public static Vector3d pow (Vector3d v, double p) {
		return new Vector3d(Math.pow(v.x, p), Math.pow(v.y, p), Math.pow(v.z, p));
	}

	public static Vector2d pow (Vector2d v, double p) {
		return new Vector2d(Math.pow(v.x, p), Math.pow(v.y, p));
	}

	public static Vector3d sigmoid (Vector3d v) {
		return new Vector3d(1 / (1 + Math.exp(-v.x)), 1 / (1 + Math.exp(-v.y)), 1 / (1 + Math.exp(-v.z)));
	}

	public static Vector3d toVector3d (Vec3 v) {
		return new Vector3d(v.x, v.y, v.z);
	}

	public static Vec3 toVec3 (Vector3d v) {
		return new Vec3(v.x, v.y, v.z);
	}

	public static double clamp (double d, double min, double max) {
		return d < min ? min: Math.min(d, max);
	}

	public static void clamp (Vector2d v, double min, double max) {
		v.set(clamp(v.x, min, max), clamp(v.y, min, max));
	}
}
