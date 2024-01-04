package net.leawind.mc.thirdperson.core.cameraoffset;


import org.joml.Vector2d;
import org.joml.Vector3d;

public class CameraOffsetMode {
	public  CameraOffsetScheme cameraOffsetScheme;
	/**
	 * 眼睛位置的平滑系数
	 */
	private Vector3d           eyeSmoothFactor      = new Vector3d(0);
	/**
	 * 距离的平滑系数
	 */
	private double             distanceSmoothFactor = 0;
	/**
	 * 相机偏移量的平滑系数
	 */
	private Vector2d           offsetSmoothFactor   = new Vector2d(0);
	/**
	 * 到平滑眼睛的最大距离
	 */
	private double             maxDistance          = 4.0;
	/**
	 * 相机偏移值
	 * <p>
	 * 表示玩家眼睛在屏幕上的位置，x 和 y 的范围都是 [-1, 1]
	 */
	private Vector2d           offsetRatio          = new Vector2d(0);
	/**
	 * 当切换到头顶视角时的y偏移量（x偏移固定为0）
	 */
	private double             centerOffsetRatio    = 0.25;

	public CameraOffsetMode (CameraOffsetScheme scheme, double maxDist, double x, double y) {
		this(scheme, maxDist, new Vector2d(x, y));
	}

	public CameraOffsetMode (CameraOffsetScheme scheme, double maxDist, Vector2d offset) {
		this.cameraOffsetScheme = scheme;
		setMaxDistance(maxDist);
		setOffsetRatio(offset);
		setCenterOffsetRatio(0.6);
	}

	public Vector3d getEyeSmoothFactor () {
		return eyeSmoothFactor;
	}

	public CameraOffsetMode setEyeSmoothFactor (Vector3d smoothFactor) {
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

	public Vector2d getOffsetSmoothFactor () {
		return offsetSmoothFactor;
	}

	public CameraOffsetMode setOffsetSmoothFactor (Vector2d smoothFactor) {
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
			offsetRatio.set(-offsetRatio.x, offsetRatio.y);
		} else if (!isLeft && offsetRatio.x > 0) {
			offsetRatio.set(-offsetRatio.x, offsetRatio.y);
		}
		cameraOffsetScheme.onModify();
		return this;
	}

	public Vector2d getOffsetRatio () {
		return cameraOffsetScheme.isCenter ? new Vector2d(0, getCenterOffsetRatio()): getOffsetValue();
	}

	public CameraOffsetMode setOffsetRatio (Vector2d offset) {
		offsetRatio = offset;
		cameraOffsetScheme.onModify();
		return this;
	}

	public double getCenterOffsetRatio () {
		return centerOffsetRatio;
	}

	public Vector2d getOffsetValue () {
		return offsetRatio;
	}

	public CameraOffsetMode setCenterOffsetRatio (double offset) {
		centerOffsetRatio = offset;
		cameraOffsetScheme.onModify();
		return this;
	}
}
