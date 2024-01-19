package net.leawind.mc.thirdperson.core;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.event.ThirdPersonKeys;
import net.leawind.mc.thirdperson.impl.config.Config;
import net.leawind.mc.util.api.math.vector.Vector3d;
import net.leawind.mc.util.math.LMath;
import net.minecraft.client.Minecraft;

public final class ModReferee {
	/**
	 * <p>
	 * 是否正在调整摄像机偏移量
	 */
	public static boolean isAdjustingCameraOffset () {
		return isAdjustingCameraDistance() && !CameraAgent.wasCameraCloseToEntity;
	}

	public static boolean isAdjustingCameraDistance () {
		return CameraAgent.isAvailable() && isThirdPerson() && ThirdPersonKeys.ADJUST_POSITION.isDown();
	}

	/**
	 * 根据 mc options 判断当前是否是第三人称
	 */
	public static boolean isThirdPerson () {
		return !Minecraft.getInstance().options.getCameraType().isFirstPerson();
	}

	/**
	 * 当前是否显示准星
	 */
	public static boolean shouldRenderCrosshair () {
		Config config = ThirdPerson.getConfig();
		return CameraAgent.isAvailable() && (ThirdPerson.ENTITY_AGENT.wasAiming() ? config.render_crosshair_when_aiming: config.render_crosshair_when_not_aiming);
	}

	/**
	 * TODO 让玩家实体淡出淡入，而不是瞬间消失出现
	 * <p>
	 * 是否完全隐藏玩家实体
	 * <p>
	 * 当启用假的第一人称或相机距离玩家足够近时隐藏
	 * <p>
	 * 需要借助相机坐标和玩家眼睛坐标来判断
	 */
	public static boolean wasCameraCloseToEntity () {
		Minecraft mc     = Minecraft.getInstance();
		Config    config = ThirdPerson.getConfig();
		if (!config.player_fade_out_enabled) {
			return false;
		} else if (mc.cameraEntity == null || CameraAgent.camera == null) {
			return false;
		}
		//		Vec3 eyePosition    = mc.cameraEntity.getEyePosition(PlayerAgent.lastPartialTick);
		Vector3d eyePosition    = CameraAgent.getSmoothEyePositionValue();
		Vector3d cameraPosition = LMath.toVector3d(CameraAgent.camera.getPosition());
		if (config.cameraOffsetScheme.getMode().getMaxDistance() <= config.distanceMonoList.get(0)) {
			return true;
		} else {
			return eyePosition.distance(cameraPosition) <= config.distanceMonoList.get(0);
		}
	}

	/**
	 * 根据玩家的按键判断玩家是否想瞄准
	 */
	public static boolean doesPlayerWantToAim () {
		return ThirdPerson.isToggleToAiming || ThirdPersonKeys.FORCE_AIMING.isDown();
	}
}
