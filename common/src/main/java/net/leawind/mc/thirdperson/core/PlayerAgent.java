package net.leawind.mc.thirdperson.core;


import net.leawind.mc.math.LMath;
import net.leawind.mc.math.smoothvalue.ExpRotSmoothDouble;
import net.leawind.mc.math.smoothvalue.SmoothDouble;
import net.leawind.mc.math.vector.Vector2d;
import net.leawind.mc.math.vector.Vector3d;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.util.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.jetbrains.annotations.NotNull;

public class PlayerAgent {
	public static final Vector2d           impulseHorizon = new Vector2d(0);
	public static final Vector3d           impulse        = new Vector3d(0);
	public static final SmoothDouble       smoothXRot     = new SmoothDouble(d -> d);
	public static final ExpRotSmoothDouble smoothYRot     = ExpRotSmoothDouble.createWithHalflife(360, ModConstants.PLAYER_ROTATION_HALFLIFE);
	public static       boolean            wasInterecting = false;
	public static       boolean            wasAiming      = false;

	public static void resetSmoothRotations () {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null) {
			smoothYRot.set((double)mc.player.getYRot());
			smoothXRot.set(mc.player.getXRot());
		}
	}

	public static void updateSmoothRotations (double period) {
		smoothYRot.update(period);
	}

	public static void applySmoothRotations () {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			player.setYRot(player.yRotO = smoothYRot.get(ThirdPersonMod.lastPartialTick).floatValue());
			player.setXRot(player.xRotO = (float)smoothXRot.get(ThirdPersonMod.lastPartialTick));
		}
	}

	public static void reset () {
		Minecraft mc = Minecraft.getInstance();
		ThirdPersonMod.lastPartialTick = mc.getFrameTime();
		if (mc.cameraEntity != null) {
			// 将虚拟球心放在实体眼睛处
			CameraAgent.smoothEyePosition.set(LMath.toVector3d(mc.cameraEntity.getEyePosition(ThirdPersonMod.lastPartialTick)));
			if (mc.player != null) {
				resetSmoothRotations();
			}
		}
	}

	/**
	 * 玩家移动时自动转向移动方向
	 */
	@PerformanceSensitive
	public static void onServerAiStep () {
		Minecraft mc     = Minecraft.getInstance();
		Config    config = ThirdPersonMod.getConfig();
		assert mc.cameraEntity != null;
		if (!config.rotate_to_moving_direction) {
			return;
		} else if (wasInterecting) {
			return;
		} else if (wasAiming) {
			return;
		} else if (impulseHorizon.length() <= 1e-5) {
			return;
		} else if (mc.cameraEntity.isSwimming()) {
			turnToDirection(impulse, true);
		} else if (CameraAgent.wasCameraCloseToEntity) {
			return;
		} else if (ModReferee.isAttachedEntityFallFlying()) {
			return;
		} else {
			// 键盘控制的移动方向
			double absoluteRotDegree = LMath.rotationDegreeFromDirection(impulseHorizon);
			turnToRotation(absoluteRotDegree, 0, mc.options.keySprint.isDown());
		}
	}

	public static void onCameraSetup (double period) {
		Minecraft mc     = Minecraft.getInstance();
		Config    config = ThirdPersonMod.getConfig();
		if (!CameraAgent.isControlledCamera()) {
			return;
		} else if (wasAiming) {
			turnToCameraHitResult(true);
			// 侧身拉弓
			if (config.auto_turn_body_drawing_a_bow && CameraAgent.isControlledCamera()) {
				assert mc.player != null;
				if (mc.player.isUsingItem() && mc.player.getUseItem().is(Items.BOW)) {
					double k = mc.player.getUsedItemHand() == InteractionHand.MAIN_HAND ? 1: -1;
					if (mc.options.mainHand().get() == HumanoidArm.LEFT) {
						k = -k;
					}
					mc.player.yBodyRot = (float)(k * 45 + mc.player.getYRot());
				}
			}
		} else if (mc.player != null && mc.player.isFallFlying()) {
			turnToCameraRotation(true);
			//			} else if (CameraAgent.wasAttachedEntityInvisible) {
			//				turnToCameraRotation(true);
		} else if (config.player_rotate_with_camera_when_not_aiming) {
			turnToCameraRotation(true);
		} else if (wasInterecting && config.auto_rotate_interacting) {
			if (config.rotate_interacting_type) {
				turnToCameraHitResult(true);
			} else {
				turnToCameraRotation(true);
			}
		}
		applySmoothRotations();
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
				smoothYRot.set(y);
				smoothXRot.update(x);
			} else {
				smoothYRot.setTarget(y);
				smoothXRot.update(x);
			}
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
	 * @param pos 目标位置
	 */
	public static void turnToPosition (@NotNull Vector3d pos, boolean isInstantly) {
		assert Minecraft.getInstance().player != null;
		Vector3d eyePosition      = LMath.toVector3d(Minecraft.getInstance().player.getEyePosition(ThirdPersonMod.lastPartialTick));
		Vector3d playerViewVector = pos.copy().sub(eyePosition);
		turnToDirection(playerViewVector, isInstantly);
	}

	public static void turnToDirection (Vector3d v, boolean isInstantly) {
		Vector2d rotation = LMath.rotationDegreeFromDirection(v);
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
	 * 玩家是否在交互
	 * <p>
	 * 即是否按下了 使用|攻击|选取 键
	 */
	public static boolean isInterecting () {
		Options mcOptions = Minecraft.getInstance().options;
		return mcOptions.keyUse.isDown() || mcOptions.keyAttack.isDown() || mcOptions.keyPickItem.isDown();
	}
}
