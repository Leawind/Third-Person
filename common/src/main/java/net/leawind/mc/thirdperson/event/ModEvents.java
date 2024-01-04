package net.leawind.mc.thirdperson.event;


import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.ModOptions;
import net.leawind.mc.thirdperson.core.PlayerAgent;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.util.Vectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ModEvents {
	public static void register () {
		ClientTickEvent.CLIENT_PRE.register(ModEvents::onClientTickPre);
		ClientLifecycleEvent.CLIENT_STARTED.register(ModEvents::onClientStarted);
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(ModEvents::onClientPlayerRespawn);
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(ModEvents::onClientPlayerJoin);
		ClientRawInputEvent.MOUSE_SCROLLED.register(ModEvents::onMouseScrolled);
	}

	private static void onClientTickPre (Minecraft mc) {
		if (mc.isPaused()) {
			return;
		}
		CameraAgent.updateSmoothEyePosition(0.05);
		PlayerAgent.wasInterecting = PlayerAgent.isInterecting();
		PlayerAgent.wasAiming      = PlayerAgent.isAiming();
		Config.cameraOffsetScheme.setAiming(PlayerAgent.wasAiming);
	}

	private static void onClientStarted (Minecraft minecraft) {
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
		if (ModOptions.isAdjustingCameraDistance()) {
			double dist = Config.cameraOffsetScheme.getMode().getMaxDistance();
			dist = Config.distanceMonoList.offset(dist, (int)-Math.signum(amount));
			Config.cameraOffsetScheme.getMode().setMaxDistance(dist);
			return EventResult.interruptFalse();
		} else {
			return EventResult.pass();
		}
	}

	public static void onStartAdjustingCameraOffset () {
	}

	public static void onStopAdjustingCameraOffset () {
		Config.loadFromCameraOffsetScheme();
		Config.save();
	}

	/**
	 * 移动鼠标调整相机偏移
	 *
	 * @param xMove 水平移动的像素
	 * @param yMove 垂直移动的像素
	 */
	public static void onAdjustingCameraOffset (double xMove, double yMove) {
		if (xMove == 0 && yMove == 0) {
			return;
		}
		Minecraft          mc     = Minecraft.getInstance();
		CameraOffsetScheme scheme = Config.cameraOffsetScheme;
		if (scheme.isCenter()) {
			// 相机在头顶，只能上下调整
			double topOffset = scheme.getMode().getCenterOffsetRatio();
			topOffset += -yMove / mc.getWindow().getScreenHeight();
			topOffset = Vectors.clamp(topOffset, -1, 1);
			scheme.getMode().setCenterOffsetRatio(topOffset);
		} else {
			// 相机没固定在头顶，可以上下左右调整
			double offsetX = scheme.getMode().getSideOffsetRatio().x;
			double offsetY = scheme.getMode().getSideOffsetRatio().y;
			offsetX += -xMove / mc.getWindow().getScreenWidth();
			offsetY += -yMove / mc.getWindow().getScreenHeight();
			offsetX = Vectors.clamp(offsetX, -1, 1);
			offsetY = Vectors.clamp(offsetY, -1, 1);
			scheme.setSide(Math.signum(offsetX));
			scheme.getMode().setSideOffsetRatio(offsetX, offsetY);
		}
	}

	/**
	 * 当玩家与环境交互时，趁交互事件处理前，让玩家看向相机落点
	 */
	public static void onBeforeHandleKeybinds () {
		PlayerAgent.wasInterecting = PlayerAgent.isInterecting();
		if (PlayerAgent.wasInterecting) {
			Minecraft.getInstance().gameRenderer.pick(1.0f);
		}
	}
}
