package net.leawind.mc.thirdperson.core.cameraoffset;


import net.leawind.mc.util.math.Vec2d;
import net.leawind.mc.util.math.Vec3d;

public class CameraOffsetMode {
	public  CameraOffsetScheme cameraOffsetScheme;
	/**
	 * 眼睛位置的平滑系数
	 */
	private Vec3d              eyeSmoothFactor      = Vec3d.ZERO;
	/**
	 * 距离的平滑系数
	 */
	private double             distanceSmoothFactor = 0;
	/**
	 * 相机偏移量的平滑系数
	 */
	private Vec2d              offsetSmoothFactor   = Vec2d.ZERO;
	/**
	 * 到平滑眼睛的最大距离
	 */
	private double             maxDistance          = 4.0;
	/**
	 * 相机偏移值
	 * <p>
	 * 表示玩家眼睛在屏幕上的位置，x 和 y 的范围都是 [-1, 1]
	 */
	private Vec2d              offsetRatio          = Vec2d.ZERO;
	/**
	 * 当切换到头顶视角时的y偏移量（x偏移固定为0）
	 */
	private double             centerOffsetRatio    = 0.25;

	public CameraOffsetMode (CameraOffsetScheme scheme, double maxDist, double x, double y) {
		this(scheme, maxDist, new Vec2d(x, y));
	}

	public CameraOffsetMode (CameraOffsetScheme scheme, double maxDist, Vec2d offset) {
		this.cameraOffsetScheme = scheme;
		setEyeSmoothFactor(new Vec3d(1e-11, 1e-8, 1e-11));
		setDistanceSmoothFactor(1e-5);
		setOffsetSmoothFactor(new Vec2d(2e-8));
		setMaxDistance(maxDist);
		setOffsetRatio(offset);
		setCenterOffsetRatio(0.6);
	}

	public Vec3d getEyeSmoothFactor () {
		return eyeSmoothFactor;
	}

	public CameraOffsetMode setEyeSmoothFactor (Vec3d smoothFactor) {
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

	public Vec2d getOffsetSmoothFactor () {
		return offsetSmoothFactor;
	}

	public CameraOffsetMode setOffsetSmoothFactor (Vec2d smoothFactor) {
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

	public CameraOffsetMode setSide (boolean isLeft) {
		if (isLeft && offsetRatio.x < 0) {
			offsetRatio = new Vec2d(-offsetRatio.x, offsetRatio.y);
		} else if (!isLeft && offsetRatio.x > 0) {
			offsetRatio = new Vec2d(-offsetRatio.x, offsetRatio.y);
		}
		cameraOffsetScheme.onModify();
		return this;
	}

	public Vec2d getOffsetRatio () {
		return cameraOffsetScheme.isCenter ? new Vec2d(0, getCenterOffsetRatio()): getOffsetValue();
	}

	public CameraOffsetMode setOffsetRatio (Vec2d offset) {
		offsetRatio = offset;
		cameraOffsetScheme.onModify();
		return this;
	}

	public double getCenterOffsetRatio () {
		return centerOffsetRatio;
	}

	public Vec2d getOffsetValue () {
		return offsetRatio;
	}

	public CameraOffsetMode setCenterOffsetRatio (double offset) {
		centerOffsetRatio = offset;
		cameraOffsetScheme.onModify();
		return this;
	}
}
