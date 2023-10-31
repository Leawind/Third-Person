package net.leawind.mc.thirdpersonperspective.core;


import com.mojang.logging.LogUtils;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.io.Serializable;

/**
 * 第三人称相机的偏移模式
 * <p>
 * 第三人称下，相机会根据其当前所处的模式来确定相机的行为。例如如何跟随玩家、如何旋转、与玩家的相对位置如何确定等。
 * <p>
 * 默认有两种模式，按F5在第一人称和两种模式间切换
 */
public class CameraOffsetProfile implements Cloneable, Serializable {
	public static final Logger              LOGGER          = LogUtils.getLogger();
	public static final CameraOffsetProfile DEFAULT_CLOSER  = CameraOffsetProfile.create(1.6, -0.372f, 0.2f, 0.8, -0.5f, 0f);
	public static final CameraOffsetProfile DEFAULT_FARTHER = CameraOffsetProfile.create(3.6, -0.3f, 0.2f, 1.5, -0.5f, 0f);
	public              OffsetModeAiming    aimingMode;
	public              OffsetModeNormal    normalMode;
	public              boolean             isTop           = false;
	public transient    boolean             isAiming        = false;

	private CameraOffsetProfile () {
	}

	public static CameraOffsetProfile create (double normalMaxDist,
											  double normalOffsetX,
											  double normalOffsetY,
											  double aimingMaxDist,
											  double aimingOffsetX,
											  double aimingOffsetY) {
		final CameraOffsetProfile profile = new CameraOffsetProfile();
		profile.normalMode = new OffsetModeNormal(profile, normalMaxDist, (float)normalOffsetX, (float)normalOffsetY);
		profile.aimingMode = new OffsetModeAiming(profile, aimingMaxDist, (float)aimingOffsetX, (float)aimingOffsetY);
		return profile;
	}

	/**
	 * 获取当前模式
	 */
	public OffsetMode getMode () {
		return isAiming ? aimingMode: normalMode;
	}

	/**
	 * 获取当前未启用的模式
	 */
	public OffsetMode getAnotherMode () {
		return isAiming ? normalMode: aimingMode;
	}

	/**
	 * 设置相机相对于玩家的方向
	 * <p>
	 *
	 * @param isLeft true 表示相机在玩家左侧
	 */
	public void setSide (boolean isLeft) {
		aimingMode.setSide(isLeft);
		normalMode.setSide(isLeft);
	}

	public void setSide (double side) {
		setSide(side > 0);
	}

	public void nextSide () {
		if (isTop) {
			isTop = false;
		} else {
			aimingMode.offsetValue = new Vec2(-aimingMode.offsetValue.x, aimingMode.offsetValue.y);
			normalMode.offsetValue = new Vec2(-normalMode.offsetValue.x, normalMode.offsetValue.y);
		}
	}

	public void setToTop () {
		isTop = true;
	}

	public void setAiming (boolean isAiming) {
		this.isAiming = isAiming;
	}

	@Override
	public CameraOffsetProfile clone () {
		try {
			CameraOffsetProfile clone = (CameraOffsetProfile)super.clone();
			clone.aimingMode                     = (OffsetModeAiming)this.aimingMode.clone();
			clone.normalMode                     = (OffsetModeNormal)this.normalMode.clone();
			clone.aimingMode.cameraOffsetProfile = clone;
			clone.normalMode.cameraOffsetProfile = clone;
			LOGGER.info("Cloning CameraOffsetProfile {} --> {}", this, clone);
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError("CameraOffsetProfile Clone error");
		}
	}

	public static class OffsetModeNormal extends OffsetMode {
		public OffsetModeNormal (CameraOffsetProfile profile, double maxDist, float x, float y) {
			super(profile);
			setEyeSmoothFactor(new Vec3(8e-4, 1e-2, 8e-4));
			setDistanceSmoothFactor(1e-3);
			setOffsetSmoothFactor(new Vec2(4e-5f, 4e-5f));
			setMaxDistance(maxDist);
			setOffsetValue(new Vec2(x, y));
			setTopOffsetValue(0.2);
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
			setEyeSmoothFactor(new Vec3(1e-11, 1e-8, 1e-11));
			setDistanceSmoothFactor(1e-5);
			setOffsetSmoothFactor(new Vec2(2e-8f, 2e-8f));
			setMaxDistance(maxDist);
			setOffsetValue(offset);
			setTopOffsetValue(0.6);
		}

		@Override
		public Vec2 getOffsetRatio (double distance) {
			return cameraOffsetProfile.isTop
				   ? new Vec2(0, (float)Math.atan2(topOffsetValue, distance))
				   : new Vec2((float)Math.atan2(offsetValue.x, distance), (float)Math.atan2(offsetValue.y, distance));
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

		public OffsetMode setSide (boolean isLeft) {
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
		public OffsetMode clone () {
			try {
				return (OffsetMode)super.clone();
			} catch (CloneNotSupportedException e) {
				throw new AssertionError();
			}
		}
	}
}
