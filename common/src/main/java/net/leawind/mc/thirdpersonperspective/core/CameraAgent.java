package net.leawind.mc.thirdpersonperspective.core;


import com.mojang.blaze3d.Blaze3D;
import com.mojang.logging.LogUtils;
import net.leawind.mc.thirdpersonperspective.config.Config;
import net.leawind.mc.thirdpersonperspective.userprofile.UserProfile;
import net.leawind.mc.util.smoothvalue.ExpSmoothDouble;
import net.leawind.mc.util.smoothvalue.ExpSmoothVec2;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec2;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.slf4j.Logger;

public class CameraAgent {
	public static final Logger          LOGGER               = LogUtils.getLogger();
	public static       BlockGetter     level;
	public static       Camera          camera;
	public static       LocalPlayer     player;
	public static       boolean         isThirdPersonEnabled = false;
	public static       ExpSmoothVec2   smoothOffset         = new ExpSmoothVec2().setValue(0, 0);
	public static       double          lastTickTime         = 0;
	public static       boolean         isAiming             = false;
	// 上次转动视角的时间
	public static       double          lastTurnTime         = 0;
	/**
	 * 虚相机到平滑眼睛的距离
	 */
	public static       ExpSmoothDouble smoothDistance       = new ExpSmoothDouble().setValue(0).setTarget(0);
	public static       Vec2            relativeRotation     = Vec2.ZERO;

	public static boolean isAvailable () {
		if (!Config.is_mod_enable) {
			return false;
		}
		Minecraft mc     = Minecraft.getInstance();
		Camera    camera = mc.gameRenderer.getMainCamera();
		if (!camera.isInitialized()) {
			return false;
		}
		LocalPlayer player = mc.player;
		return player != null;
	}

	public static void reset () {//TODO
		Minecraft mc = Minecraft.getInstance();
		player = mc.player;
		assert player != null;
		camera = mc.gameRenderer.getMainCamera();
		// 将虚拟球心放在实体眼睛处
		smoothOffset.setValue(0, 0);
		smoothDistance.setValue(0);
		LOGGER.info("Reset CameraAgent");
	}

	public static void updateUserProfile () {
		//TODO
		// smoothOffset.setSmoothFactor()
		// smoothDistance.setSmoothFactor()
		LOGGER.info("CameraAgent: updateUserProfile");
	}

	/**
	 * 鼠标移动导致的相机旋转
	 *
	 * @param turnY 水平角变化量
	 * @param turnX 俯仰角变化量
	 */
	public static void onCameraTurn (double turnY, double turnX) {
		System.out.printf("\r TurnPlayer: y=%.4f, x=%.4f", turnY, turnX);
		lastTurnTime = Blaze3D.getTime();
	}

	/**
	 * 进入第三人称视角时触发
	 */
	public static void onEnterThirdPerson (float lerpK) {
		reset();
		isThirdPersonEnabled     = true;
		isAiming                 = false;
		Options.isToggleToAiming = false;
		lastTickTime             = Blaze3D.getTime();
		LOGGER.info("Enter third person, lerpK={}", lerpK);
	}

	/**
	 * 计算并更新相机的朝向和坐标
	 *
	 * @param level  维度
	 * @param entity 实体
	 */
	@PerformanceSensitive
	public static void onRenderTick (BlockGetter level, Entity entity, boolean isMirrored, float lerpK) {
		CameraAgent.level = level;
		player            = (LocalPlayer)entity;
		// 时间
		double now           = Blaze3D.getTime();
		double sinceLastTurn = now - lastTurnTime;
		double sinceLastTick = now - lastTickTime;
		lastTickTime = now;
		PlayerAgent.onRenderTick(lerpK);
		CameraOffsetProfile profile = UserProfile.getCameraOffsetProfile();
		//TODO
	}
}
