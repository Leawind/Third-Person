package net.leawind.mc.thirdpersonperspective.core;


import com.mojang.logging.LogUtils;
import net.leawind.mc.thirdpersonperspective.config.Config;
import net.leawind.mc.util.Vectors;
import net.leawind.mc.util.smoothvalue.ExpSmoothVec3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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
}
