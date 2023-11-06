package net.leawind.mc.thirdpersonperspective.core;


import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.io.Serializable;

/**
 * 第三人称相机的偏移模式
 * <p>
 * 第三人称下，相机会根据其当前所处的模式来确定相机的行为。例如如何跟随玩家、如何旋转、与玩家的相对位置如何确定等。
 * <p>
 * 默认有两种模式，按F5在第一人称和两种模式间切换
 */
public class CameraOffsetProfile implements Cloneable, Serializable {
	public static final CameraOffsetProfile DEFAULT_CLOSER  = CameraOffsetProfile.create(1.6, -0.372f, 0.25f, 0.8, -0.7f, 0f);
	public static final CameraOffsetProfile DEFAULT_FARTHER = CameraOffsetProfile.create(3.6, -0.372f, 0.25f, 1.8, -0.7f, 0f);
	public              OffsetModeAiming    offsetAiming;
	public              OffsetModeNormal    offsetNormal;
	public              boolean             isTop           = false;

	private CameraOffsetProfile () {
	}

	public static CameraOffsetProfile create (double normalMaxDist,
											  double normalOffsetX,
											  double normalOffsetY,
											  double aimingMaxDist,
											  double aimingOffsetX,
											  double aimingOffsetY) {
		final CameraOffsetProfile profile = new CameraOffsetProfile();
		profile.offsetNormal = new OffsetModeNormal(profile, normalMaxDist, (float)normalOffsetX, (float)normalOffsetY);
		profile.offsetAiming = new OffsetModeAiming(profile, aimingMaxDist, (float)aimingOffsetX, (float)aimingOffsetY);
		return profile;
	}

	public Vec2 getOffsetRatio (boolean isAiming, double distance) {
		if (isAiming) {
			return this.offsetAiming.getOffsetRatio(distance);
		} else {
			return this.offsetNormal.getOffsetRatio();
		}
	}

	@Override
	public CameraOffsetProfile clone () {
		try {
			CameraOffsetProfile clone = (CameraOffsetProfile)super.clone();
			clone.offsetAiming = (OffsetModeAiming)this.offsetAiming.clone();
			clone.offsetNormal = (OffsetModeNormal)this.offsetNormal.clone();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError("CameraOffsetProfile Clone error");
		}
	}

	public static class OffsetModeNormal extends OffsetMode {
		public OffsetModeNormal (CameraOffsetProfile profile, double maxDist, float x, float y) {
			super(profile);
			setEyeSmoothFactor(new Vec3(8e-4, 1e-2, 8e-4));
			setDistanceSmoothFactor(0.5);
			setOffsetSmoothFactor(new Vec2(2e-3f, 2e-3f));
			setMaxDistance(maxDist).setOffsetValue(new Vec2(x, y));
		}

		public Vec2 getOffsetRatio () {
			return cameraOffsetProfile.isTop ? new Vec2(0, (float)topOffsetValue): offsetValue;
		}

		@Override
		public Vec2 getOffsetRatio (double distance) {
			return getOffsetRatio();
		}
	}

	public static class OffsetModeAiming extends OffsetMode {
		public OffsetModeAiming (CameraOffsetProfile profile, double maxDist, float x, float y) {
			this(profile, maxDist, new Vec2(x, y));
		}

		public OffsetModeAiming (CameraOffsetProfile profile, double maxDist, Vec2 offset) {
			super(profile);
			setEyeSmoothFactor(new Vec3(8e-7, 1e-5, 8e-7));
			setDistanceSmoothFactor(5e-2);
			setOffsetSmoothFactor(new Vec2(2e-6f, 2e-6f));
			setMaxDistance(maxDist).setOffsetValue(offset);
		}

		@Override
		public Vec2 getOffsetRatio (double distance) {
			//TODO
			return cameraOffsetProfile.isTop
				   ? new Vec2(0, (float)Math.atan2(topOffsetValue, distance))
				   : new Vec2((float)-Math.atan2(offsetValue.x, distance), (float)-Math.atan2(offsetValue.y, distance));
		}
	}

	public abstract static class OffsetMode implements Cloneable, Serializable {
		/**
		 * 眼睛位置的平滑系数
		 */
		public    Vec3                eyeSmoothFactor      = Vec3.ZERO;
		/**
		 * 距离的平滑系数
		 */
		public    double              distanceSmoothFactor = 0;
		/**
		 * 相机偏移量的平滑系数
		 */
		public    Vec2                offsetSmoothFactor   = Vec2.ZERO;
		/**
		 * 到平滑眼睛的最大距离
		 */
		public    double              maxDistance          = 4.0;
		/**
		 * 相机偏移值
		 * <p>
		 * 对于 OffsetModeNormal，这个值表示玩家眼睛在屏幕上的位置
		 * <p>
		 * 对于 OffsetModeAiming，这个值表示相机视线到玩家眼睛的距离
		 */
		public    Vec2                offsetValue          = Vec2.ZERO;
		/**
		 * 当切换到头顶视角时的y偏移量（x偏移固定为0）
		 */
		public    double              topOffsetValue       = 0.25;
		protected CameraOffsetProfile cameraOffsetProfile;

		public OffsetMode (CameraOffsetProfile profile) {
			this.cameraOffsetProfile = profile;
		}

		public OffsetMode setEyeSmoothFactor (Vec3 smoothFactor) {
			eyeSmoothFactor = smoothFactor;
			return this;
		}

		public OffsetMode setDistanceSmoothFactor (double smoothFactor) {
			distanceSmoothFactor = smoothFactor;
			return this;
		}

		public OffsetMode setOffsetSmoothFactor (Vec2 smoothFactor) {
			offsetSmoothFactor = smoothFactor;
			return this;
		}

		public OffsetMode setMaxDistance (double distance) {
			maxDistance = distance;
			return this;
		}

		public OffsetMode setOffsetValue (Vec2 offset) {
			offsetValue = offset;
			return this;
		}

		public OffsetMode setTopOffsetValue (double offset) {
			topOffsetValue = offset;
			return this;
		}

		/**
		 * 根据距离计算实相机偏移量
		 */
		abstract public Vec2 getOffsetRatio (double distance);

		@Override
		public OffsetMode clone () {
			try {
				return (OffsetMode)super.clone();
			} catch (CloneNotSupportedException e) {
				throw new AssertionError();
			}
		}
	}
}

