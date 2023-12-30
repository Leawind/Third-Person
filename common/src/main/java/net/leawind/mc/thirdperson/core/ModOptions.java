package net.leawind.mc.thirdperson.core;


import net.leawind.mc.thirdperson.ModKeys;

import java.util.HashMap;
import java.util.Map;

public class ModOptions {
	@SuppressWarnings("unused")
	public static Map<Object, Object> debug_map        = new HashMap<>();
	/**
	 * 是否通过按键切换到了瞄准模式
	 */
	public static boolean             isToggleToAiming = false;

	/**
	 * 是否正在调整摄像机偏移量
	 */
	public static boolean isAdjustingCameraOffset () {
		return CameraAgent.isAvailable() && CameraAgent.isThirdPerson && ModKeys.ADJUST_POSITION.isDown();
	}

	/**
	 * 根据玩家的按键判断玩家是否想瞄准
	 */
	public static boolean doesPlayerWantToAim () {
		return isToggleToAiming || ModKeys.FORCE_AIMING.isDown();
	}
}
