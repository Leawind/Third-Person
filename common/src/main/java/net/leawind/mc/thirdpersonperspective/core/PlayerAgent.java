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
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class PlayerAgent {
	public static final Logger        LOGGER            = LogUtils.getLogger();
	public static       LocalPlayer   player;
	public static       ExpSmoothVec3 smoothEyePosition = new ExpSmoothVec3();

	public static void reset () {
		Minecraft mc = Minecraft.getInstance();
		player = mc.player;
		assert player != null;
		smoothEyePosition.setTarget(player.getEyePosition()).setValue(player.getEyePosition());
		LOGGER.info("Reset PlayerAgent");
	}

	public static void updateUserProfile () {
		// TODO
		// smoothEyePosition.setSmoothFactor()
		LOGGER.info("PlayerAgent: updateUserProfile");
	}

	@PerformanceSensitive
	public static void onServerAiStep () {
		if (player.isSwimming()) {
			return;
		}
	}

	@PerformanceSensitive
	public static void onRenderTick (float lerpK) {
		//TODO
	}

	/**
	 * 设置玩家朝向
	 *
	 * @param rot         朝向
	 * @param isInstantly 是否瞬间转动
	 */
	public static void turnTo (Vec2 rot, boolean isInstantly) {
		if (isInstantly) {
			player.setYRot(rot.y);
			player.setXRot(rot.x);
		} else {
			float playerY = player.getYRot();
			float dy      = ((rot.y - playerY) % 360 + 360) % 360;
			if (dy > 180) {
				dy -= 360;
			}
			player.turn(dy, rot.x - player.getXRot());
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
