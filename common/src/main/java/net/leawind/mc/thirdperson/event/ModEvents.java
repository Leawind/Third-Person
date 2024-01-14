package net.leawind.mc.thirdperson.event;


import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.Window;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.util.ModConstants;
import net.leawind.mc.thirdperson.core.ModReferee;
import net.leawind.mc.thirdperson.core.PlayerAgent;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetMode;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.math.vector.Vector2d;
import net.leawind.mc.math.vector.Vectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

public class ModEvents {
	public static void register () {
		ClientTickEvent.CLIENT_PRE.register(ModEvents::onClientTickPre);
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(ModEvents::onClientPlayerRespawn);
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(ModEvents::onClientPlayerJoin);
		ClientRawInputEvent.MOUSE_SCROLLED.register(ModEvents::onMouseScrolled);
	}

	private static void onClientTickPre (Minecraft mc) {
		if (mc.isPaused()) {
			return;
		}
		Config config = ThirdPersonMod.getConfig();
		CameraAgent.updateSmoothEyePosition(0.05);
		PlayerAgent.wasInterecting         = PlayerAgent.isInterecting();
		PlayerAgent.wasAiming              = ModReferee.isCameraEntityAiming();
		config.cameraOffsetScheme.isAiming = PlayerAgent.wasAiming;
	}

	/**
	 * 当玩家死亡后重生或加入新的维度时触发
	 */
	private static void onClientPlayerRespawn (LocalPlayer oldPlayer, LocalPlayer newPlayer) {
		onPlayerReset();
		ThirdPersonMod.LOGGER.info("on Client player respawn");
	}

	private static void onClientPlayerJoin (LocalPlayer player) {
		onPlayerReset();
		ThirdPersonMod.LOGGER.info("on Client player join");
	}

	private static void onPlayerReset () {
		CameraAgent.reset();
		PlayerAgent.reset();
	}

	/**
	 * 使用滚轮调整距离
	 *
	 * @param minecraft mc
	 * @param amount    向前滚是+1，向后滚是-1
	 */
	private static EventResult onMouseScrolled (Minecraft minecraft, double amount) {
		Config config = ThirdPersonMod.getConfig();
		if (ModReferee.isAdjustingCameraDistance()) {
			double dist = config.cameraOffsetScheme.getMode().getMaxDistance();
			dist = config.distanceMonoList.offset(dist, (int)-Math.signum(amount));
			config.cameraOffsetScheme.getMode().setMaxDistance(dist);
			return EventResult.interruptFalse();
		} else {
			return EventResult.pass();
		}
	}

	public static void onStartAdjustingCameraOffset () {
	}

	public static void onStopAdjustingCameraOffset () {
		ThirdPersonMod.getConfigManager().trySave();
	}

	/**
	 * 移动鼠标调整相机偏移
	 *
	 * @param xMove 水平移动的像素
	 * @param yMove 垂直移动的像素
	 */
	public static void onAdjustingCameraOffset (Vector2d movement) {
		if (movement.lengthSquared() == 0) {
			return;
		}
		Config             config     = ThirdPersonMod.getConfig();
		Window             window     = Minecraft.getInstance().getWindow();
		Vector2d           screenSize = new Vector2d(window.getScreenWidth(), window.getScreenHeight());
		CameraOffsetScheme scheme     = config.cameraOffsetScheme;
		CameraOffsetMode   mode       = scheme.getMode();
		if (mode.isCentered()) {
			// 相机在头顶，只能上下调整
			double topOffset = mode.getCenterOffsetRatio();
			topOffset += -movement.y / screenSize.y;
			topOffset = Vectors.clamp(topOffset, -1, 1);
			mode.setCenterOffsetRatio(topOffset);
		} else {
			// 相机没固定在头顶，可以上下左右调整
			Vector2d offset = mode.getSideOffsetRatio();
			offset.sub(movement.div(screenSize));
			offset.clamp(-1, 1);
			scheme.setSide(Math.signum(offset.x));
			mode.setSideOffsetRatio(offset);
		}
	}

	/**
	 * 当玩家与环境交互时，趁交互事件处理前，让玩家看向相机落点
	 */
	public static void onBeforeHandleKeybinds () {
		PlayerAgent.wasInterecting = PlayerAgent.isInterecting();
		if (PlayerAgent.wasInterecting) {
			// 该方法中使用了mixin，修改了 viewVector
			Minecraft.getInstance().gameRenderer.pick(1.0f);
		}
	}

	/**
	 * 退出第三人称视角
	 */
	public static void onLeaveThirdPerson () {
		if (ThirdPersonMod.getConfig().turn_with_camera_when_enter_first_person) {
			PlayerAgent.turnToCameraRotation(true);
		}
	}

	/**
	 * 进入第三人称视角时触发
	 */
	public static void onEnterThirdPerson () {
		CameraAgent.reset();
		PlayerAgent.reset();
		PlayerAgent.wasAiming               = false;
		ModReferee.isToggleToAiming         = false;
		CameraAgent.lastRenderTickTimeStamp = Blaze3D.getTime();
	}

	/**
	 * 鼠标移动导致的相机旋转
	 *
	 * @param y 偏航角变化量
	 * @param x 俯仰角变化量
	 */
	public static void onCameraTurn (double y, double x) {
		Config config = ThirdPersonMod.getConfig();
		if (config.is_mod_enable && !ModReferee.isAdjustingCameraOffset()) {
			y *= 0.15;
			x *= config.lock_camera_pitch_angle ? 0: -0.15;
			if (y != 0 || x != 0) {
				CameraAgent.lastCameraTurnTimeStamp = Blaze3D.getTime();
				CameraAgent.relativeRotation.set(Mth.clamp(CameraAgent.relativeRotation.x + x, -ModConstants.CAMERA_PITCH_DEGREE_LIMIT, ModConstants.CAMERA_PITCH_DEGREE_LIMIT), (CameraAgent.relativeRotation.y + y) % 360f);
			}
		}
	}
}
