package net.leawind.mc.thirdperson.core;


import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.util.Vectors;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerAgent {
	public static final Logger        LOGGER            = LoggerFactory.getLogger(ThirdPersonMod.MOD_ID);
	public static       boolean       wasInterecting    = false;
	public static       Vector2f      absoluteImpulse   = new Vector2f(0, 0);
	public static       float         lastPartialTick   = 1F;

	public static void reset () {
		Minecraft mc = Minecraft.getInstance();
		if (mc.cameraEntity != null) {
			// 将虚拟球心放在实体眼睛处
			lastPartialTick = mc.getFrameTime();
			CameraAgent.smoothEyePosition.setTarget(mc.cameraEntity.getEyePosition(lastPartialTick))
										 .setValue(mc.cameraEntity.getEyePosition(lastPartialTick));
		}
	}

	/**
	 * 当玩家与环境交互时，趁交互事件处理前，让玩家看向相机落点
	 */
	public static void onBeforeHandleKeybinds () {
		if (wasInterecting && CameraAgent.isThirdPerson()) {
			turnToCameraHitResult();
			Minecraft.getInstance().gameRenderer.pick(1.0f);
		}
	}

	/**
	 * 让玩家朝向相机的落点
	 */
	public static void turnToCameraHitResult () {
		// 计算相机视线落点
		Vec3 cameraHitPosition = CameraAgent.getPickPosition();
		if (cameraHitPosition == null) {
			turnWithCamera(true);
		} else {
			// 让玩家朝向该坐标
			turnTo(cameraHitPosition);
		}
	}

	/**
	 * 让玩家朝向与相机相同
	 */
	public static void turnWithCamera (boolean isInstantly) {
		turnTo(CameraAgent.relativeRotation.y + 180, -CameraAgent.relativeRotation.x, isInstantly);
	}

	/**
	 * 让玩家朝向世界中的特定点
	 *
	 * @param target 目标位置
	 */
	public static void turnTo (@NotNull Vec3 target) {
		Vec3 playerViewDirection = Minecraft.getInstance().player.getEyePosition(lastPartialTick).vectorTo(target);
		Vec2 playerViewRotation  = Vectors.rotationDegreeFromDirection(playerViewDirection);
		turnTo(playerViewRotation, true);
	}

	/**
	 * 设置玩家朝向
	 *
	 * @param rot         朝向
	 * @param isInstantly 是否瞬间转动
	 */
	public static void turnTo (Vec2 rot, boolean isInstantly) {
		turnTo(rot.y, rot.x, isInstantly);
	}

	/**
	 * 设置玩家朝向
	 *
	 * @param ry          偏航角
	 * @param rx          俯仰角
	 * @param isInstantly 是否瞬间转动
	 */
	public static void turnTo (float ry, float rx, boolean isInstantly) {
		if (CameraAgent.isControlledCamera()) {
			Minecraft mc = Minecraft.getInstance();
			assert mc.player != null;
			if (isInstantly) {
				mc.player.setYRot(ry);
				mc.player.setXRot(rx);
			} else {
				float playerY = mc.player.getViewYRot(lastPartialTick);
				float dy      = ((ry - playerY) % 360 + 360) % 360;
				if (dy > 180) {
					dy -= 360;
				}
				assert mc.player != null;
				mc.player.turn(dy, rx - mc.player.getViewXRot(lastPartialTick));
			}
		}
	}

	/**
	 * 玩家移动时自动转向移动方向
	 */
	@PerformanceSensitive
	public static void onServerAiStep () {
		if (!Minecraft.getInstance().cameraEntity.isSwimming() && absoluteImpulse.length() > 1e-5) {
			float absoluteRotDegree = (float)Vectors.rotationDegreeFromDirection(new Vec2(absoluteImpulse.x,
																						  absoluteImpulse.y));
			if (Config.rotate_to_moving_direction && !(CameraAgent.wasAiming || wasInterecting) &&
				!ModOptions.shouldPlayerRotateWithCamera()) {
				turnTo(absoluteRotDegree, 0, Minecraft.getInstance().options.keySprint.isDown());
			}
		}
	}

	@PerformanceSensitive
	public static void onRenderTick (float partialTick, double sinceLastTick) {
		Minecraft          mc     = Minecraft.getInstance();
		CameraOffsetScheme scheme = Config.cameraOffsetScheme;
		// 更新是否在与方块交互
		wasInterecting = mc.options.keyUse.isDown() || mc.options.keyAttack.isDown() || mc.options.keyPickItem.isDown();
		assert mc.cameraEntity != null;
		if (CameraAgent.wasAiming || wasInterecting) {
			turnToCameraHitResult();
		} else if (ModOptions.shouldPlayerRotateWithCamera()) {
			turnWithCamera(false);
		} else if (Config.player_rotate_with_camera_when_not_aiming) {
			turnWithCamera(true);
		}
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
