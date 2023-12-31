package net.leawind.mc.thirdperson.core;


import net.leawind.mc.thirdperson.ModKeys;
import net.leawind.mc.thirdperson.config.Config;
import net.minecraft.world.entity.LivingEntity;

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
		return CameraAgent.isAvailable() && CameraAgent.isThirdPerson() && ModKeys.ADJUST_POSITION.isDown();
	}

	/**
	 * 根据玩家的按键判断玩家是否想瞄准
	 */
	public static boolean doesPlayerWantToAim () {
		return isToggleToAiming || ModKeys.FORCE_AIMING.isDown();
	}

	/**
	 * 当前是否显示准星
	 */
	public static boolean shouldRenderCrosshair () {
		return CameraAgent.isAvailable() &&
			   (CameraAgent.isAiming ? Config.render_crosshair_when_aiming: Config.render_crosshair_when_not_aiming);
	}

	/**
	 * 玩家当前是否跟随相机旋转
	 */
	public static boolean shouldPlayerRotateWithCamera () {
		return CameraAgent.attachedEntity.isSwimming() || (CameraAgent.attachedEntity instanceof LivingEntity &&
														   ((LivingEntity)CameraAgent.attachedEntity).isFallFlying());
	}
}
