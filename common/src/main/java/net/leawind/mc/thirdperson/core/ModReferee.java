package net.leawind.mc.thirdperson.core;


import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.event.ModKeys;
import net.leawind.mc.util.vector.Vector3d;
import net.leawind.mc.util.vector.Vectors;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Arrays;

public class ModReferee {
	/**
	 * 是否通过按键切换到了瞄准模式
	 */
	public static boolean isToggleToAiming = false;

	/**
	 * <p>
	 * 是否正在调整摄像机偏移量
	 */
	public static boolean isAdjustingCameraOffset () {
		return isAdjustingCameraDistance() && !CameraAgent.wasAttachedEntityInvisible;
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
		return CameraAgent.isAvailable() && (PlayerAgent.wasAiming ? Config.get().render_crosshair_when_aiming: Config.get().render_crosshair_when_not_aiming);
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
	public static boolean isAttachedEntityInvisible () {
		Minecraft mc = Minecraft.getInstance();
		if (!Config.get().player_fade_out_enabled) {
			return false;
		} else if (mc.cameraEntity == null || CameraAgent.camera == null) {
			return false;
		}
		//		Vec3 eyePosition    = mc.cameraEntity.getEyePosition(PlayerAgent.lastPartialTick);
		Vector3d eyePosition    = CameraAgent.getSmoothEyePositionValue();
		Vector3d cameraPosition = Vectors.toVector3d(CameraAgent.camera.getPosition());
		if (Config.get().cameraOffsetScheme.getMode().getMaxDistance() <= Config.get().distanceMonoList.get(0)) {
			return true;
		} else {
			return eyePosition.distance(cameraPosition) <= Config.get().distanceMonoList.get(0);
		}
	}

	/**
	 * 判断当前是否在瞄准<br/>
	 * <p>
	 * 如果正在使用弓或三叉戟瞄准，返回true
	 * <p>
	 * 如果正在手持上了弦的弩|鸡蛋|末影珍珠|雪球，返回true
	 * <p>
	 * 如果按住了相应按键，返回true
	 * <p>
	 * 如果通过按相应按键切换到了持续瞄准状态，返回true
	 */
	public static boolean isCameraEntityAiming () {
		Minecraft mc = Minecraft.getInstance();
		// 只有 LivingEntity 才有可能手持物品瞄准
		if (mc.cameraEntity instanceof LivingEntity livingEntity) {
			if (livingEntity.isUsingItem()) {
				// 正在使用（瞄准）
				ItemStack itemStack = livingEntity.getUseItem();
				if (itemStack.is(Items.BOW) || itemStack.is(Items.TRIDENT)) {
					return true;// 正在使用弓或三叉戟瞄准
				}
			}
			ItemStack mainHandItem = livingEntity.getMainHandItem();
			ItemStack offhandItem  = livingEntity.getOffhandItem();
			for (ItemStack stack: Arrays.asList(mainHandItem, offhandItem)) {
				if (stack.is(Items.CROSSBOW) && CrossbowItem.isCharged(stack)) {
					return true;    // 上了弦的弩
				} else if (ModConstants.AUTO_AIM_ITEMS.contains(stack.getItem().getDescriptionId())) {
					return true;
				}
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
