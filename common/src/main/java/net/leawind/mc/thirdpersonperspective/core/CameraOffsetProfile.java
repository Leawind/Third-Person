package net.leawind.mc.thirdpersonperspective.core;


import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * 第三人称相机的偏移模式
 * <p>
 * 第三人称下，相机会根据其当前所处的模式来确定相机的行为。例如如何跟随玩家、如何旋转、与玩家的相对位置如何确定等。
 */
public class CameraOffsetProfile {
	// TODO
	public static final CameraOffsetProfile DEFAULT_CLOSER  = new CameraOffsetProfile(new OffsetModeNormal(1.6,
																										   new Vec2(-0.372f,
																													0.2f)),
																					  new OffsetModeAiming(0.8,
																										   new Vec2(-0.7f,
																													0f)));
	public static final CameraOffsetProfile DEFAULT_FARTHER = new CameraOffsetProfile(new OffsetModeNormal(3.6,
																										   new Vec2(-0.372f,
																													0.2f)),
																					  new OffsetModeAiming(1.8,
																										   new Vec2(-0.7f,
																													0f)));
	public              OffsetModeAiming    offsetAiming;
	public              OffsetModeNormal    offsetNormal;

	public CameraOffsetProfile (OffsetModeNormal offsetNormal, OffsetModeAiming offsetAiming) {
		this.offsetNormal = offsetNormal;
		this.offsetAiming = offsetAiming;
	}

	public static class OffsetModeNormal extends OffsetMode {
		public Vec3   eyeSmoothFactor      = new Vec3(8e-4, 1e-2, 8e-4);
		public double distanceSmoothFactor = 0.5;
		public Vec2   offsetSmoothFactor   = new Vec2(2e-3f, 2e-3f);

		public OffsetModeNormal (double maxDist, Vec2 offset) {
			super();
			setMaxDistance(maxDist).setOffsetValue(offset);
		}

		@Override
		public Vec2 getOffsetRatio (double distance) {
			return offsetValue;
		}
	}

	public static class OffsetModeAiming extends OffsetMode {
		public Vec3   eyeSmoothFactor      = new Vec3(8e-7, 1e-5, 8e-7);
		public double distanceSmoothFactor = 5e-2;
		public Vec2   offsetSmoothFactor   = new Vec2(2e-6f, 2e-6f);

		public OffsetModeAiming (double maxDist, Vec2 offset) {
			setMaxDistance(maxDist).setOffsetValue(offset);
		}

		@Override
		public Vec2 getOffsetRatio (double distance) {
			//TODO
			return new Vec2((float)-Math.atan2(offsetValue.x, distance), (float)-Math.atan2(offsetValue.y, distance));
		}
	}

	public abstract static class OffsetMode {
		/**
		 * 眼睛位置的平滑系数
		 */
		public Vec3   eyeSmoothFactor      = Vec3.ZERO;
		/**
		 * 距离的平滑系数
		 */
		public double distanceSmoothFactor = 0;
		/**
		 * 相机偏移量的平滑系数
		 */
		public Vec2   offsetSmoothFactor   = Vec2.ZERO;
		/**
		 * 到平滑眼睛的最大距离
		 */
		public double maxDistance          = 4.0;
		/**
		 * 实相机偏移值，对于不同子类，可能表示不同含义
		 */
		public Vec2   offsetValue          = Vec2.ZERO;

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

		/**
		 * 根据距离计算实相机偏移量
		 */
		abstract public Vec2 getOffsetRatio (double distance);
	}
}

