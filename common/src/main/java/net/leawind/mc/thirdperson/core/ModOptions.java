package net.leawind.mc.thirdperson.core;


import net.leawind.mc.thirdperson.ModKeys;
import net.leawind.mc.thirdperson.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

public class ModOptions {
	/**
	 * 是否通过按键切换到了瞄准模式
	 */
	public static boolean isToggleToAiming = false;

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
			   (CameraAgent.wasAiming ? Config.render_crosshair_when_aiming: Config.render_crosshair_when_not_aiming);
	}

	/**
	 * 玩家当前是否跟随相机旋转
	 */
	public static boolean shouldPlayerRotateWithCamera () {
		Minecraft mc = Minecraft.getInstance();
		if (CameraAgent.wasAttachedEntityInvisible) {
			return true;
		}
		return mc.cameraEntity != null && mc.cameraEntity.isSwimming() ||
			   (mc.cameraEntity instanceof LivingEntity && ((LivingEntity)mc.cameraEntity).isFallFlying());
	}

	/**
	 * TODO 让玩家实体淡出淡入，而不是瞬间消失出现
	 * <p>
	 * 是否完全隐藏玩家实体
	 * <p>
	 * 当启用假的第一人称或相机距离玩家足够近时隐藏
	 */
	public static boolean isAttachedEntityInvisible () {
		Minecraft mc = Minecraft.getInstance();
		if (Config.player_fade_out_enabled && mc.cameraEntity != null) {
			return Math.min((CameraAgent.wasAiming
							 ? Config.cameraOffsetScheme.aimingMode
							 : Config.cameraOffsetScheme.normalMode).getMaxDistance(),
							mc.cameraEntity.getEyePosition(Minecraft.getInstance().getFrameTime())
										   .distanceTo(CameraAgent.camera.getPosition())) <= Config.distanceMonoList.get(0);
		} else {
			return false;
		}
	}
}
