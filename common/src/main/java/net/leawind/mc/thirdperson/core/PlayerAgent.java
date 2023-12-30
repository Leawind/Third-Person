package net.leawind.mc.thirdperson.core;


import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.util.Vectors;
import net.leawind.mc.util.smoothvalue.ExpSmoothVec3;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerAgent {
	public static final Logger        LOGGER            = LoggerFactory.getLogger(ThirdPersonMod.MOD_ID);
	public static       ExpSmoothVec3 smoothEyePosition = new ExpSmoothVec3();
	public static       boolean       wasInterecting    = false;
	public static       Vector2f      absoluteImpulse   = new Vector2f(0, 0);

	public static void reset () {
		Minecraft mc = Minecraft.getInstance();
		// 将虚拟球心放在实体眼睛处
		smoothEyePosition.setTarget(CameraAgent.attachedEntity.getEyePosition())
						 .setValue(CameraAgent.attachedEntity.getEyePosition());
		LOGGER.info("Reset PlayerAgent");
	}

	/**
	 * 当玩家与环境交互时，趁交互事件处理前，让玩家看向相机落点
	 */
	public static void onBeforeHandleKeybinds () {
		if (wasInterecting) {
			if (CameraAgent.isThirdPerson) {
				turnToCameraHitResult(1);
				Minecraft.getInstance().gameRenderer.pick(1.0f);
			}
		}
	}

	/**
	 * 让玩家朝向相机的落点
	 */
	public static void turnToCameraHitResult (float partialTick) {
		// 计算相机视线落点
		HitResult hitResult         = Minecraft.getInstance().hitResult;
		Vec3      cameraHitPosition = CameraAgent.getPickPosition();
		if (cameraHitPosition == null) {
			turnWithCamera(true);
		} else {
			// 让玩家朝向该坐标
			turnTo(cameraHitPosition, partialTick);
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
	public static void turnTo (@NotNull Vec3 target, float partialTick) {
		Vec3 playerViewVector = CameraAgent.playerEntity.getEyePosition(partialTick).vectorTo(target);
		Vec2 playerViewRot    = Vectors.rotationDegreeFromDirection(playerViewVector);
		turnTo(playerViewRot, true);
	}

	/**
	 * 设置玩家朝向
	 *
	 * @param ry          偏航角
	 * @param rx          俯仰角
	 * @param isInstantly 是否瞬间转动
	 */
	public static void turnTo (float ry, float rx, boolean isInstantly) {
		if (isInstantly) {
			CameraAgent.playerEntity.setYRot(ry);
			CameraAgent.playerEntity.setXRot(rx);
		} else {
			float playerY = CameraAgent.playerEntity.getYRot();
			float dy      = ((ry - playerY) % 360 + 360) % 360;
			if (dy > 180) {
				dy -= 360;
			}
			CameraAgent.playerEntity.turn(dy, rx - CameraAgent.playerEntity.getXRot());
		}
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
	 * 玩家移动时自动转向移动方向
	 */
	@PerformanceSensitive
	public static void onServerAiStep () {
		if (CameraAgent.attachedEntity == CameraAgent.playerEntity && !CameraAgent.attachedEntity.isSwimming() &&
			absoluteImpulse.length() > 1e-5) {
			float absoluteRotDegree = (float)Vectors.rotationDegreeFromDirection(new Vec2(absoluteImpulse.x,
																						  absoluteImpulse.y));
			if (Config.rotate_to_moving_direction && !(CameraAgent.isAiming || wasInterecting)) {
				turnTo(absoluteRotDegree, 0, CameraAgent.playerEntity.isSprinting());
			}
		}
	}

	@PerformanceSensitive
	public static void onRenderTick (float partialTick, double sinceLastTick) {
		Minecraft          mc     = Minecraft.getInstance();
		CameraOffsetScheme scheme = Config.cameraOffsetScheme;
		// 更新是否在与方块交互
		wasInterecting = mc.options.keyUse.isDown() || mc.options.keyAttack.isDown() || mc.options.keyPickItem.isDown();
		// 平滑更新眼睛位置，飞行时使用专用的平滑系数
		if (CameraAgent.playerEntity.isFallFlying()) {
			smoothEyePosition.setSmoothFactor(Config.flying_smooth_factor);
		} else {
			smoothEyePosition.setSmoothFactor(scheme.getMode().getEyeSmoothFactor());
		}
		smoothEyePosition.setTarget(CameraAgent.attachedEntity.getEyePosition(partialTick)).update(sinceLastTick);
		if (CameraAgent.isAiming || wasInterecting) {
			turnToCameraHitResult(partialTick);
		} else if (CameraAgent.attachedEntity.isSwimming() || (CameraAgent.attachedEntity instanceof LivingEntity &&
															   ((LivingEntity)CameraAgent.attachedEntity).isFallFlying())) {
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
		if (CameraAgent.attachedEntity == null) {
			return false;
		}
		// 只有 LivingEntity 才有可能手持物品瞄准
		if (CameraAgent.attachedEntity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)CameraAgent.attachedEntity;
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
		return Options.doesPlayerWantToAim();
	}
}
