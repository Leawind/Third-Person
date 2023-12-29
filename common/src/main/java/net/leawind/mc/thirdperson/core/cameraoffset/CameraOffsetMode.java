package net.leawind.mc.thirdperson.core.cameraoffset;


import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CameraOffsetMode {
	public  CameraOffsetScheme cameraOffsetScheme;
	/**
	 * 眼睛位置的平滑系数
	 */
	private Vec3               eyeSmoothFactor      = Vec3.ZERO;
	/**
	 * 距离的平滑系数
	 */
	private double             distanceSmoothFactor = 0;
	/**
	 * 相机偏移量的平滑系数
	 */
	private Vec2               offsetSmoothFactor   = Vec2.ZERO;
	/**
	 * 到平滑眼睛的最大距离
	 */
	private double             maxDistance          = 4.0;
	/**
	 * 相机偏移值
	 * <p>
	 * 表示玩家眼睛在屏幕上的位置，x 和 y 的范围都是 [-1, 1]
	 */
	private Vec2               offsetRatio          = Vec2.ZERO;
	/**
	 * 当切换到头顶视角时的y偏移量（x偏移固定为0）
	 */
	private double             centerOffsetRatio    = 0.25;

	public CameraOffsetMode (CameraOffsetScheme scheme) {
		this.cameraOffsetScheme = scheme;
	}

	public Vec3 getEyeSmoothFactor () {
		return eyeSmoothFactor;
	}

	public CameraOffsetMode setEyeSmoothFactor (Vec3 smoothFactor) {
		eyeSmoothFactor = smoothFactor;
		cameraOffsetScheme.onModify();
		return this;
	}

	public double getDistanceSmoothFactor () {
		return distanceSmoothFactor;
	}

	public CameraOffsetMode setDistanceSmoothFactor (double smoothFactor) {
		distanceSmoothFactor = smoothFactor;
		cameraOffsetScheme.onModify();
		return this;
	}

	public Vec2 getOffsetSmoothFactor () {
		return offsetSmoothFactor;
	}

	public CameraOffsetMode setOffsetSmoothFactor (Vec2 smoothFactor) {
		offsetSmoothFactor = smoothFactor;
		cameraOffsetScheme.onModify();
		return this;
	}

	public double getMaxDistance () {
		return maxDistance;
	}

	public CameraOffsetMode setMaxDistance (double distance) {
		maxDistance = distance;
		cameraOffsetScheme.onModify();
		return this;
	}

	public Vec2 getOffsetValue () {
		return offsetRatio;
	}

	public CameraOffsetMode setOffsetRatio (Vec2 offset) {
		offsetRatio = offset;
		cameraOffsetScheme.onModify();
		return this;
	}

	public double getCenterOffsetRatio () {
		return centerOffsetRatio;
	}

	public CameraOffsetMode setCenterOffsetRatio (double offset) {
		centerOffsetRatio = offset;
		cameraOffsetScheme.onModify();
		return this;
	}

	public CameraOffsetMode setSide (boolean isLeft) {
		if (isLeft && offsetRatio.x < 0) {
			offsetRatio = new Vec2(-offsetRatio.x, offsetRatio.y);
		} else if (!isLeft && offsetRatio.x > 0) {
			offsetRatio = new Vec2(-offsetRatio.x, offsetRatio.y);
		}
		cameraOffsetScheme.onModify();
		return this;
	}

	/**
	 * 根据距离计算实相机偏移量
	 */
	public Vec2 getOffsetRatio () {
		return cameraOffsetScheme.isCenter ? new Vec2(0, (float)getCenterOffsetRatio()): getOffsetValue();
	}
}
