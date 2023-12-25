package net.leawind.mc.thirdpersonperspective.core.cameraoffset;


import com.mojang.logging.LogUtils;
import net.minecraft.world.phys.Vec2;
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
	public CameraOffsetMode getMode () {
		return isAiming ? aimingMode: normalMode;
	}

	/**
	 * 获取当前未启用的模式
	 */
	public CameraOffsetMode getAnotherMode () {
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
}
