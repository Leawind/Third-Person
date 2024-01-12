package net.leawind.mc.thirdperson.core;


import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.util.vector.Vector2d;
import net.leawind.mc.util.vector.Vector3d;
import net.leawind.mc.util.vector.Vectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.jetbrains.annotations.NotNull;

public class PlayerAgent {
	public static final Vector2d impulseHorizon  = new Vector2d(0);
	public static final Vector3d impulse         = new Vector3d(0);
	public static       boolean  wasInterecting  = false;
	public static       float    lastPartialTick = 1F;
	public static       boolean  wasAiming       = false;

	/**
	 * 玩家移动时自动转向移动方向
	 */
	@PerformanceSensitive
	public static void onServerAiStep () {
		Minecraft mc = Minecraft.getInstance();
		assert mc.cameraEntity != null;
		if (!Config.get().rotate_to_moving_direction) {
			return;
		} else if (wasInterecting) {
			return;
		} else if (wasAiming) {
			return;
		} else if (impulseHorizon.length() <= 1e-5) {
			return;
		} else if (mc.cameraEntity.isSwimming()) {
			turnToDirection(impulse, false);
		} else if (CameraAgent.wasAttachedEntityInvisible) {
			return;
		} else if (ModReferee.isAttachedEntityFallFlying()) {
			return;
		} else {
			// 键盘控制的移动方向
			double absoluteRotDegree = Vectors.rotationDegreeFromDirection(impulseHorizon);
			turnToRotation(absoluteRotDegree, 0, mc.options.keySprint.isDown());
		}
	}

	public static void onRenderTick () {
		Minecraft mc = Minecraft.getInstance();
		if (!CameraAgent.isControlledCamera()) {
			return;
			//		} else if (mc.cameraEntity != null && mc.cameraEntity.isSwimming()) {
			//			turnToDirection(impulse, false);
		} else if (wasAiming) {
			turnToCameraHitResult(true);
		} else if (mc.player != null && mc.player.isFallFlying()) {
			turnToCameraRotation(true);
			//			} else if (CameraAgent.wasAttachedEntityInvisible) {
			//				turnToCameraRotation(true);
		} else if (Config.get().player_rotate_with_camera_when_not_aiming) {
			turnToCameraRotation(true);
		} else if (wasInterecting && Config.get().auto_rotate_interacting) {
			if (Config.get().rotate_interacting_type) {
				turnToCameraHitResult(true);
			} else {
				turnToCameraRotation(true);
			}
		}
	}

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
}
