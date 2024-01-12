package net.leawind.mc.thirdperson.core.cameraoffset;


import net.leawind.mc.thirdperson.config.Config;
import org.jetbrains.annotations.Nullable;

/**
 * 第三人称相机的偏移方案
 * <p>
 * 第三人称下，相机会根据其当前所处的模式来确定相机的行为。例如如何跟随玩家、如何旋转、与玩家的相对位置如何确定等。
 * <p>
 * 默认有两种模式，按F5在第一人称和两种模式间切换
 *
 * TODO 直接从 Config 读取偏移量，而非存储在此对象中
 */
public class CameraOffsetScheme {
	public static final CameraOffsetScheme DEFAULT  = CameraOffsetScheme.create(1.6, -0.372f, 0.2f, 0.8, -0.5f, 0f);
	public              CameraOffsetMode   aimingMode;
	public              CameraOffsetMode   normalMode;
	public              boolean            isAiming = false;
	protected           boolean            isCenter = false;
	@Nullable private   Config             config;

	private CameraOffsetScheme () {
	}

	public CameraOffsetScheme bindConfig (Config config) {
		this.config = config;
		return this;
	}

	public static CameraOffsetScheme create (double normalMaxDist, double normalOffsetX, double normalOffsetY, double aimingMaxDist, double aimingOffsetX, double aimingOffsetY) {
		final CameraOffsetScheme scheme = new CameraOffsetScheme();
		scheme.normalMode = new CameraOffsetMode(scheme, normalMaxDist, normalOffsetX, normalOffsetY);
		scheme.aimingMode = new CameraOffsetMode(scheme, aimingMaxDist, aimingOffsetX, aimingOffsetY);
		return scheme;
	}

	/**
	 * 获取当前模式
	 */
	public CameraOffsetMode getMode () {
		return isAiming ? aimingMode: normalMode;
	}

	/**
	 * 获取当前未启用的模式
	 */
	@SuppressWarnings("unused")
	public CameraOffsetMode getAnotherMode () {
		return isAiming ? normalMode: aimingMode;
	}

	public void setSide (double side) {
		setSide(side > 0);
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
		onModify();
	}

	public void onModify () {
		if (config != null && config.cameraOffsetScheme == this) {
			config.updateFromCameraOffsetScheme();
		}
	}

	/**
	 * 切换到另一边
	 */
	public void nextSide () {
		if (isCenter) {
			isCenter = false;
		} else {
			aimingMode.nextSide();
			normalMode.nextSide();
		}
		onModify();
	}

	public boolean isCenter () {
		return isCenter;
	}

	/**
	 * 切换到中间
	 */
	public void setToCenter () {
		isCenter = true;
		onModify();
	}

	public void setAiming (boolean isAiming) {
		this.isAiming = isAiming;
	}
}
