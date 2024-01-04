package net.leawind.mc.thirdperson.core;


import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.util.math.Vectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerAgent {
	@SuppressWarnings("unused")
	public static final Logger   LOGGER                   = LoggerFactory.getLogger(ThirdPersonMod.MOD_ID);
	public static final Vector2f horizonalAbsoluteImpulse = new Vector2f(0);
	public static       boolean  wasInterecting           = false;
	public static       float    lastPartialTick          = 1F;

	public static void reset () {
		Minecraft mc = Minecraft.getInstance();
		lastPartialTick = mc.getFrameTime();
		if (mc.cameraEntity != null) {
			// 将虚拟球心放在实体眼睛处
			CameraAgent.smoothEyePosition.set(Vectors.toVector3d(mc.cameraEntity.getEyePosition(lastPartialTick)));
		}
	}

	/**
	 * 让玩家朝向相机的落点
	 */
	public static void turnToCameraHitResult (boolean isInstantly) {
		// 计算相机视线落点
		Vector3d cameraHitPosition = CameraAgent.getPickPosition();
		if (cameraHitPosition == null) {
			turnToCameraRotation(isInstantly);
		} else {
			turnToPosition(cameraHitPosition, isInstantly);
		}
	}

	/**
	 * 让玩家朝向与相机相同
	 */
	public static void turnToCameraRotation (boolean isInstantly) {
		turnToRotation(CameraAgent.relativeRotation.y + 180, -CameraAgent.relativeRotation.x, isInstantly);
	}

	/**
	 * 让玩家朝向世界中的特定点
	 *
	 * @param target 目标位置
	 */
	public static void turnToPosition (@NotNull Vector3d target, boolean isInstantly) {
		assert Minecraft.getInstance().player != null;
		Vector3d playerViewVector = target.sub(Vectors.toVector3d(Minecraft.getInstance().player.getEyePosition(lastPartialTick)));
		turnToDirection(playerViewVector, isInstantly);
	}

	public static void turnToDirection (Vector3d v, boolean isInstantly) {
		Vector2d rotation = Vectors.rotationDegreeFromDirection(v);
		turnToRotation(rotation, isInstantly);
	}

	/**
	 * 设置玩家朝向
	 *
	 * @param rot         朝向
	 * @param isInstantly 是否瞬间转动
	 */
	public static void turnToRotation (Vector2d rot, boolean isInstantly) {
		turnToRotation(rot.y, rot.x, isInstantly);
	}

	/**
	 * 设置玩家朝向
	 *
	 * @param y           偏航角
	 * @param x           俯仰角
	 * @param isInstantly 是否瞬间转动
	 */
	public static void turnToRotation (double y, double x, boolean isInstantly) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null && CameraAgent.isControlledCamera()) {
			if (isInstantly) {
				mc.player.setYRot((float)y);
				mc.player.setXRot((float)x);
			} else {
				double previousY = mc.player.getViewYRot(lastPartialTick);
				double dy        = ((y - previousY) % 360 + 360) % 360;
				if (dy > 180) {
					dy -= 360;
				}
				double previousX = mc.player.getViewXRot(lastPartialTick);
				double dx        = x - previousX;
				mc.player.turn(dy, dx);
			}
		}
	}

	/**
	 * 玩家移动时自动转向移动方向
	 */
	@PerformanceSensitive
	public static void onServerAiStep () {
		Minecraft mc = Minecraft.getInstance();
		assert mc.cameraEntity != null;
		if (!Config.rotate_to_moving_direction) {
			return;
		} else if (wasInterecting) {
			return;
		} else if (CameraAgent.wasAiming) {
			return;
		} else if (CameraAgent.wasAttachedEntityInvisible) {
			return;
		} else if (mc.cameraEntity.isUnderWater()) {
			return;
		} else if (horizonalAbsoluteImpulse.length() <= 1e-5) {
			return;
		} else if ((mc.cameraEntity instanceof LivingEntity && ((LivingEntity)mc.cameraEntity).isFallFlying())) {
			return;
		} else {
			// 键盘控制的移动方向
			double absoluteRotDegree = Vectors.rotationDegreeFromDirection(new Vector2d(horizonalAbsoluteImpulse));
			turnToRotation(absoluteRotDegree, 0, Minecraft.getInstance().options.keySprint.isDown());
		}
	}

	public static void onRenderTick () {
		Minecraft mc = Minecraft.getInstance();
		if (CameraAgent.isControlledCamera()) {
			if (CameraAgent.wasAiming) {
				PlayerAgent.turnToCameraHitResult(true);
			} else if (mc.player != null && mc.player.isUnderWater()) {
				PlayerAgent.turnToCameraRotation(true);
			} else if (mc.player != null && mc.player.isFallFlying()) {
				PlayerAgent.turnToCameraRotation(true);
			} else if (CameraAgent.wasAttachedEntityInvisible) {
				PlayerAgent.turnToCameraRotation(true);
			} else if (Config.player_rotate_with_camera_when_not_aiming) {
				PlayerAgent.turnToCameraRotation(true);
			} else if (PlayerAgent.wasInterecting) {
				if (Config.auto_rotate_interacting) {
					if (Config.rotate_interacting_type) {
						turnToCameraHitResult(true);
					} else {
						turnToCameraRotation(true);
					}
				}
			}
		}
	}

	/**
	 * 玩家是否在交互
	 * <p>
	 * 即是否按下了 使用|攻击|选取 键
	 */
	public static boolean isInterecting () {
		Options mcOptions = Minecraft.getInstance().options;
		return mcOptions.keyUse.isDown() || mcOptions.keyAttack.isDown() || mcOptions.keyPickItem.isDown();
	}

	public static boolean isAvailable () {
		return CameraAgent.isAvailable();
	}

	/**
	 * 判断当前是否在瞄准<br/>
	 * <p>
	 * 如果正在使用弓或三叉戟瞄准，返回true
	 * <p>
	 * 如果正在手持上了弦的弩，返回true
	 * <p>
	 * 如果按住了相应按键，返回true
	 * <p>
	 * 如果通过按相应按键切换到了持续瞄准状态，返回true
	 */
	public static boolean isAiming () {
		Minecraft mc = Minecraft.getInstance();
		if (mc.cameraEntity == null) {
			return false;
		}
		// 只有 LivingEntity 才有可能手持物品瞄准
		if (mc.cameraEntity instanceof LivingEntity livingEntity) {
			if (livingEntity.isUsingItem()) {
				ItemStack itemStack = livingEntity.getUseItem();
				if (itemStack.is(Items.BOW) || itemStack.is(Items.TRIDENT)) {
					return true;// 正在使用弓或三叉戟瞄准
				}
			}
			ItemStack mainHandItem = livingEntity.getMainHandItem();
			if (mainHandItem.is(Items.CROSSBOW) && CrossbowItem.isCharged(mainHandItem)) {
				return true;// 主手拿着上了弦的弩
			}
			ItemStack offhandItem = livingEntity.getOffhandItem();
			if (offhandItem.is(Items.CROSSBOW) && CrossbowItem.isCharged(offhandItem)) {
				return true;// 副手拿着上了弦的弩
			}
		}
		return ModOptions.doesPlayerWantToAim();
	}
}
