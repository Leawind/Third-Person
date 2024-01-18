package net.leawind.mc.thirdperson.core;


import net.leawind.mc.math.LMath;
import net.leawind.mc.math.vector.Vector3d;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.event.ModKeys;
import net.leawind.mc.thirdperson.impl.config.Config;
import net.leawind.mc.util.api.ItemPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class ModReferee {
	/**
	 * 是否通过按键切换到了瞄准模式
	 */
	public static boolean isToggleToAiming = false;

	/**
	 * <p>
	 * 是否正在调整摄像机偏移量
	 */
	public static boolean isAdjustingCameraOffset () {
		return isAdjustingCameraDistance() && !CameraAgent.wasCameraCloseToEntity;
	}

	public static boolean isAdjustingCameraDistance () {
		return CameraAgent.isAvailable() && isThirdPerson() && ModKeys.ADJUST_POSITION.isDown();
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
		Config config = ThirdPersonMod.getConfig();
		return CameraAgent.isAvailable() && (PlayerAgent.wasAiming ? config.render_crosshair_when_aiming: config.render_crosshair_when_not_aiming);
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
		Config    config = ThirdPersonMod.getConfig();
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
	 * 判断相机实体当前是否在瞄准<br/>
	 * <p>
	 * 根据实体手持物品判断
	 */
	public static boolean isCameraEntityAiming () {
		Entity cameraEntity = Minecraft.getInstance().cameraEntity;
		Config config       = ThirdPersonMod.getConfig();
		if (cameraEntity instanceof LivingEntity livingEntity) {
			if (livingEntity.isUsingItem() && ItemPattern.anyMatch(config.use_aim_item_patterns, livingEntity.getUseItem())) {
				return true;
			} else if (ItemPattern.anyMatch(config.aim_item_patterns, livingEntity.getMainHandItem())) {
				return true;
			} else if (ItemPattern.anyMatch(config.aim_item_patterns, livingEntity.getOffhandItem())) {
				return true;
			}
		}
		return doesPlayerWantToAim();
	}

	/**
	 * 判断是否在飞行
	 */
	public static boolean isAttachedEntityFallFlying () {
		Minecraft mc = Minecraft.getInstance();
		if (mc.cameraEntity instanceof LivingEntity livingEntity) {
			return livingEntity.isFallFlying();
		}
		return false;
	}

	/**
	 * 当前是否是第三人称
	 */
	public static boolean isThirdPerson () {
		return !Minecraft.getInstance().options.getCameraType().isFirstPerson();
	}
}
