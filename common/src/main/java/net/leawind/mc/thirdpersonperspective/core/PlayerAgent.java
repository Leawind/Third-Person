package net.leawind.mc.thirdpersonperspective.core;


import com.mojang.logging.LogUtils;
import net.leawind.mc.thirdpersonperspective.config.Config;
import net.leawind.mc.util.Vectors;
import net.leawind.mc.util.smoothvalue.ExpSmoothVec3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class PlayerAgent {
	public static final Logger        LOGGER            = LogUtils.getLogger();
	public static       LocalPlayer   player;
	public static       ExpSmoothVec3 smoothEyePosition = new ExpSmoothVec3();
	public static       boolean       wasInterecting    = false;

	public static void reset () {
		Minecraft mc = Minecraft.getInstance();
		player = mc.player;
		assert player != null;
		// 将虚拟球心放在实体眼睛处
		smoothEyePosition.setTarget(player.getEyePosition()).setValue(player.getEyePosition());
		LOGGER.info("Reset PlayerAgent");
	}

	public static void updateUserProfile (CameraOffsetProfile profile) {
		smoothEyePosition.setSmoothFactor(profile.getMode().eyeSmoothFactor);
	}

	/**
	 * 插入到 Minecraft.handleKeybinds 方法头部
	 */
	public static void onBeforeHandleKeybinds () {
		if (wasInterecting) {
			if (CameraAgent.isAvailable() && CameraAgent.isThirdPerson) {
				turnToCamera(1);
				Minecraft.getInstance().gameRenderer.pick(1.0f);
			}
		}
	}

	@PerformanceSensitive
	public static void onServerAiStep () {
		if (player.isSwimming()) {
			return;
		}
		float left    = player.xxa;
		float forward = player.isFallFlying() ? 0: player.zza;
		float speed   = (float)Math.sqrt(left * left + forward * forward);// 记录此时的速度
		if (left != 0 || forward != 0) {
			float absoluteRot = (float)(CameraAgent.camera.getYRot() + (-Math.atan2(left, forward) * 180 / Math.PI));
			if (!(CameraAgent.isAiming || wasInterecting)) {
				turnTo(new Vec2(0, absoluteRot), player.isSprinting());
			}
			float relativeRot       = absoluteRot - player.getYRot();
			float relativeRotRadian = (float)(relativeRot * Math.PI / 180);
			player.xxa = (float)-Math.sin(relativeRotRadian) * speed;
			player.zza = (float)Math.cos(relativeRotRadian) * speed;
		}
	}

	@PerformanceSensitive
	public static void onRenderTick (float lerpK, double sinceLastTick) {
		Minecraft mc = Minecraft.getInstance();
		wasInterecting = mc.options.keyUse.isDown() || mc.options.keyAttack.isDown() || mc.options.keyPickItem.isDown();
		// 平滑更新眼睛位置
		smoothEyePosition.setTarget(player.getEyePosition(lerpK)).update(sinceLastTick);
		if (CameraAgent.isAiming || wasInterecting) {
			turnToCamera(lerpK);
		}
	}

	/**
	 * 跟随相机旋转
	 */
	public static void turnToCamera (float lerpK) {
		// 计算相机视线落点
		HitResult hitResult         = Minecraft.getInstance().hitResult;
		Vec3      cameraHitPosition = CameraAgent.getPickPosition();
		System.out.printf("\rCHP: %s", cameraHitPosition);//TODO
		if (cameraHitPosition == null) {
			// 让玩家朝向与相机相同
			turnTo(CameraAgent.relativeRotation.y + 180, -CameraAgent.relativeRotation.x, true);
		} else {
			// 让玩家朝向该坐标
			turnTo(cameraHitPosition, lerpK);
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
	 * 设置玩家朝向
	 *
	 * @param ry          偏航角
	 * @param rx          俯仰角
	 * @param isInstantly 是否瞬间转动
	 */
	public static void turnTo (float ry, float rx, boolean isInstantly) {
		if (isInstantly) {
			player.setYRot(ry);
			player.setXRot(rx);
		} else {
			float playerY = player.getYRot();
			float dy      = ((ry - playerY) % 360 + 360) % 360;
			if (dy > 180) {
				dy -= 360;
			}
			player.turn(dy, rx - player.getXRot());
		}
	}

	/**
	 * 让玩家朝向世界中的特定点
	 *
	 * @param target 目标位置
	 */
	public static void turnTo (@NotNull Vec3 target, float lerpK) {
		Vec3 playerViewVector = player.getEyePosition(lerpK).vectorTo(target);
		Vec2 playerViewRot    = Vectors.rotationAngleFromDirection(playerViewVector);
		turnTo(playerViewRot, true);
	}

	public static boolean isAvailable () {
		if (!Config.is_mod_enable) {
			return false;
		}
		Minecraft   mc     = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		return player != null;
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
		if (player == null) {
			return false;
		}
		if (player.isUsingItem()) {
			ItemStack itemStack = player.getUseItem();
			if (itemStack.is(Items.BOW) || itemStack.is(Items.TRIDENT)) {
				return true;// 正在使用弓或三叉戟瞄准
			}
		}
		ItemStack mainHandItem = player.getMainHandItem();
		if (mainHandItem.is(Items.CROSSBOW) && CrossbowItem.isCharged(mainHandItem)) {
			return true;// 主手拿着上了弦的弩
		}
		ItemStack offhandItem = player.getOffhandItem();
		if (offhandItem.is(Items.CROSSBOW) && CrossbowItem.isCharged(offhandItem)) {
			return true;// 副手拿着上了弦的弩
		}
		return Options.doesPlayerWantToAim();
	}
}
