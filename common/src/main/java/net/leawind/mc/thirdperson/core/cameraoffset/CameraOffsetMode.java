package net.leawind.mc.thirdperson.core.cameraoffset;


import org.joml.Vector2d;
import org.joml.Vector3d;

/**
 * 相机偏移模式
 */
public class CameraOffsetMode {
	public        CameraOffsetScheme cameraOffsetScheme;
	/**
	 * 眼睛位置的平滑系数
	 */
	private final Vector3d           eyeSmoothFactor      = new Vector3d(0);
	/**
	 * 距离的平滑系数
	 */
	private       double             distanceSmoothFactor = 0;
	/**
	 * 相机偏移量的平滑系数
	 */
	private final Vector2d           offsetSmoothFactor   = new Vector2d(0);
	/**
	 * 到平滑眼睛的最大距离
	 */
	private       double             maxDistance          = 4.0;
	/**
	 * 相机偏移值
	 * <p>
	 * 表示玩家眼睛在屏幕上的位置，x 和 y 的范围都是 [-1, 1]
	 */
	private final Vector2d           sideOffsetRatio      = new Vector2d(0);
	/**
	 * 当切换到头顶视角时的y偏移量（x偏移固定为0）
	 */
	private       double             centerOffsetRatio    = 0.25;

	public CameraOffsetMode (CameraOffsetScheme scheme, double maxDist, double x, double y) {
		this.cameraOffsetScheme = scheme;
		setMaxDistance(maxDist);
		setSideOffsetRatio(x, y);
		setCenterOffsetRatio(0.6);
	}

	public Vector3d getEyeSmoothFactor () {
		return new Vector3d(eyeSmoothFactor);
	}

	public CameraOffsetMode setEyeSmoothFactor (double horizon, double vertical) {
		eyeSmoothFactor.set(horizon, vertical, horizon);
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
		return new Vector2d(offsetSmoothFactor);
	}

	public CameraOffsetMode setOffsetSmoothFactor (double d) {
		offsetSmoothFactor.set(d, d);
		cameraOffsetScheme.onModify();
		return this;
	}

	public double getMaxDistance () {
		return maxDistance;
	}

	public void setMaxDistance (double distance) {
		maxDistance = distance;
		cameraOffsetScheme.onModify();
	}

	public void setSide (boolean isLeft) {
		if (isLeft ^ (sideOffsetRatio.x > 0)) {
			sideOffsetRatio.set(-sideOffsetRatio.x, sideOffsetRatio.y);
		}
		cameraOffsetScheme.onModify();
	}

	public void nextSide () {
		sideOffsetRatio.x = -sideOffsetRatio.x;
	}

	public Vector2d getOffsetRatio () {
		return cameraOffsetScheme.isCenter ? new Vector2d(0, getCenterOffsetRatio()): getSideOffsetRatio();
	}

	public void setSideOffsetRatio (double x, double y) {
		sideOffsetRatio.set(x, y);
		cameraOffsetScheme.onModify();
	}

	public double getCenterOffsetRatio () {
		return centerOffsetRatio;
	}

	public Vector2d getSideOffsetRatio () {
		return new Vector2d(sideOffsetRatio);
	}

	public void setCenterOffsetRatio (double offset) {
		centerOffsetRatio = offset;
		cameraOffsetScheme.onModify();
	}
}
