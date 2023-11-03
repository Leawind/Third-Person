package net.leawind.mc.thirdpersonperspective.core;


public class Options {
	/**
	 * 是否通过按键切换到了瞄准模式
	 */
	public static boolean isToggleToAiming        = false;
	/**
	 * 是否按住了强制瞄准键
	 */
	public static boolean isForceKeepingAiming    = false;
	/**
	 * 是否正在调整摄像机偏移量
	 */
	public static boolean isAdjustingCameraOffset = false;

	/**
	 * 根据玩家的按键判断玩家是否想瞄准
	 */
	public static boolean doesPlayerWantToAim () {
		return isToggleToAiming || isForceKeepingAiming;
	}
}
