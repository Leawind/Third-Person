package net.leawind.mc.thirdperson.core.cameraoffset;


import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.config.Config;
import net.minecraft.world.phys.Vec2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 第三人称相机的偏移方案
 * <p>
 * 第三人称下，相机会根据其当前所处的模式来确定相机的行为。例如如何跟随玩家、如何旋转、与玩家的相对位置如何确定等。
 * <p>
 * 默认有两种模式，按F5在第一人称和两种模式间切换
 */
public class CameraOffsetScheme {
	public static final Logger             LOGGER   = LoggerFactory.getLogger(ThirdPersonMod.MOD_ID);
	public static final CameraOffsetScheme DEFAULT  = CameraOffsetScheme.create(1.6, -0.372f, 0.2f, 0.8, -0.5f, 0f);
	public              CameraOffsetMode   aimingMode;
	public              CameraOffsetMode   normalMode;
	public transient    boolean            isAiming = false;
	protected           boolean            isCenter = false;

	private CameraOffsetScheme () {
	}

	public static CameraOffsetScheme create (double normalMaxDist,
											 double normalOffsetX,
											 double normalOffsetY,
											 double aimingMaxDist,
											 double aimingOffsetX,
											 double aimingOffsetY) {
		final CameraOffsetScheme scheme = new CameraOffsetScheme();
		scheme.normalMode = new CameraOffsetMode(scheme, normalMaxDist, (float)normalOffsetX, (float)normalOffsetY);
		scheme.aimingMode = new CameraOffsetMode(scheme, aimingMaxDist, (float)aimingOffsetX, (float)aimingOffsetY);
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
		if (Config.cameraOffsetScheme == this) {
			Config.loadFromCameraOffsetScheme();
		}
	}

	/**
	 * 切换到另一边
	 */
	public void nextSide () {
		if (isCenter) {
			isCenter = false;
		} else {
			aimingMode.setOffsetRatio(new Vec2(-aimingMode.getOffsetValue().x, aimingMode.getOffsetValue().y));
			normalMode.setOffsetRatio(new Vec2(-normalMode.getOffsetValue().x, normalMode.getOffsetValue().y));
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
