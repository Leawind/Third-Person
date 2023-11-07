package net.leawind.mc.thirdpersonperspective.core;


import net.leawind.mc.thirdpersonperspective.ModKeys;
import net.leawind.mc.thirdpersonperspective.config.Config;

public class Options {
	/**
	 * 是否通过按键切换到了瞄准模式
	 */
	public static boolean isToggleToAiming = false;

	/**
	 * 是否正在调整摄像机偏移量
	 */
	public static boolean isAdjustingCameraOffset () {
		return Config.is_mod_enable && CameraAgent.isThirdPerson && ModKeys.ADJUST_POSITION.isDown() &&
			   CameraAgent.isAvailable();
	}

	/**
	 * 根据玩家的按键判断玩家是否想瞄准
	 */
	public static boolean doesPlayerWantToAim () {
		return isToggleToAiming || ModKeys.FORCE_AIMING.isDown();
	}
}
