package net.leawind.mc.thirdperson.core.cameraoffset;


import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public abstract class CameraOffsetMode {
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
	 * 对于 OffsetModeNormal，这个值表示玩家眼睛在屏幕上的位置
	 * <p>
	 * 对于 OffsetModeAiming，这个值表示相机视线到玩家眼睛的距离
	 */
	private Vec2               offsetValue          = Vec2.ZERO;
	/**
	 * 当切换到头顶视角时的y偏移量（x偏移固定为0）
	 */
	private double             middleOffsetValue    = 0.25;

	public CameraOffsetMode (CameraOffsetScheme scheme) {
		this.cameraOffsetScheme = scheme;
	}

	public Vec3 getEyeSmoothFactor () {
		return eyeSmoothFactor;
	}

	public double getDistanceSmoothFactor () {
		return distanceSmoothFactor;
	}

	public Vec2 getOffsetSmoothFactor () {
		return offsetSmoothFactor;
	}

	public double getMaxDistance () {
		return maxDistance;
	}

	public Vec2 getOffsetValue () {
		return offsetValue;
	}

	public double getMiddleOffsetValue () {
		return middleOffsetValue;
	}

	public CameraOffsetMode setEyeSmoothFactor (Vec3 smoothFactor) {
		eyeSmoothFactor = smoothFactor;
		cameraOffsetScheme.onModify();
		return this;
	}

	public CameraOffsetMode setDistanceSmoothFactor (double smoothFactor) {
		distanceSmoothFactor = smoothFactor;
		cameraOffsetScheme.onModify();
		return this;
	}

	public CameraOffsetMode setOffsetSmoothFactor (Vec2 smoothFactor) {
		offsetSmoothFactor = smoothFactor;
		cameraOffsetScheme.onModify();
		return this;
	}

	public CameraOffsetMode setMaxDistance (double distance) {
		maxDistance = distance;
		cameraOffsetScheme.onModify();
		return this;
	}

	public CameraOffsetMode setOffsetValue (Vec2 offset) {
		offsetValue = offset;
		cameraOffsetScheme.onModify();
		return this;
	}

	public CameraOffsetMode setMiddleOffsetValue (double offset) {
		middleOffsetValue = offset;
		cameraOffsetScheme.onModify();
		return this;
	}

	public CameraOffsetMode setSide (boolean isLeft) {
		if (isLeft && offsetValue.x < 0) {
			offsetValue = new Vec2(-offsetValue.x, offsetValue.y);
		} else if (!isLeft && offsetValue.x > 0) {
			offsetValue = new Vec2(-offsetValue.x, offsetValue.y);
		}
		cameraOffsetScheme.onModify();
		return this;
	}

	/**
	 * 根据距离计算实相机偏移量
	 */
	abstract public Vec2 getOffsetRatio (double distance);
}
