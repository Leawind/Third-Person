package net.leawind.mc.util.vector;


import com.mojang.math.Vector3f;
import net.minecraft.world.phys.Vec3;

public class Vectors {
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

	public static Vector2d directionFromRotationDegree (double yRot) {
		double x = Math.sin(yRot * 0.017453292519943295 + Math.PI);
		double z = -Math.cos(yRot * 0.017453292519943295 + Math.PI);
		return new Vector2d(x, z);
	}

	public static Vector3d toVector3d (Vec3 v) {
		return new Vector3d(v.x, v.y, v.z);
	}

	public static Vector3d toVector3d (Vector3f v) {
		return new Vector3d(v.x(), v.y(), v.z());
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
