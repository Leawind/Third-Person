package net.leawind.mc.thirdperson.core.cameraoffset;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.io.Serializable;

@Environment(EnvType.CLIENT)
public abstract class CameraOffsetMode implements Cloneable, Serializable {
	/**
	 * 眼睛位置的平滑系数
	 */
	public Vec3                eyeSmoothFactor      = Vec3.ZERO;
	/**
	 * 距离的平滑系数
	 */
	public double              distanceSmoothFactor = 0;
	/**
	 * 相机偏移量的平滑系数
	 */
	public Vec2                offsetSmoothFactor   = Vec2.ZERO;
	/**
	 * 到平滑眼睛的最大距离
	 */
	public double              maxDistance          = 4.0;
	/**
	 * 相机偏移值
	 * <p>
	 * 对于 OffsetModeNormal，这个值表示玩家眼睛在屏幕上的位置
	 * <p>
	 * 对于 OffsetModeAiming，这个值表示相机视线到玩家眼睛的距离
	 */
	public Vec2                offsetValue          = Vec2.ZERO;
	/**
	 * 当切换到头顶视角时的y偏移量（x偏移固定为0）
	 */
	public double              topOffsetValue       = 0.25;
	public CameraOffsetProfile cameraOffsetProfile;

	public CameraOffsetMode (CameraOffsetProfile profile) {
		this.cameraOffsetProfile = profile;
	}

	public CameraOffsetMode setEyeSmoothFactor (Vec3 smoothFactor) {
		eyeSmoothFactor = smoothFactor;
		return this;
	}

	public CameraOffsetMode setDistanceSmoothFactor (double smoothFactor) {
		distanceSmoothFactor = smoothFactor;
		return this;
	}

	public CameraOffsetMode setOffsetSmoothFactor (Vec2 smoothFactor) {
		offsetSmoothFactor = smoothFactor;
		return this;
	}

	public CameraOffsetMode setMaxDistance (double distance) {
		maxDistance = distance;
		return this;
	}

	public CameraOffsetMode setOffsetValue (Vec2 offset) {
		offsetValue = offset;
		return this;
	}

	public CameraOffsetMode setTopOffsetValue (double offset) {
		topOffsetValue = offset;
		return this;
	}

	public CameraOffsetMode setSide (boolean isLeft) {
		if (isLeft && offsetValue.x < 0) {
			offsetValue = new Vec2(-offsetValue.x, offsetValue.y);
		} else if (!isLeft && offsetValue.x > 0) {
			offsetValue = new Vec2(-offsetValue.x, offsetValue.y);
		}
		return this;
	}

	/**
	 * 根据距离计算实相机偏移量
	 */
	abstract public Vec2 getOffsetRatio (double distance);

	@Override
	public CameraOffsetMode clone () {
		try {
			return (CameraOffsetMode)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
