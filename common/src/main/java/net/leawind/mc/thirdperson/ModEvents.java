package net.leawind.mc.thirdperson;


import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.Options;
import net.leawind.mc.thirdperson.core.PlayerAgent;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetScheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

public class ModEvents {
	public static void register () {
		ClientLifecycleEvent.CLIENT_STARTED.register(ModEvents::onClientStarted);
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(ModEvents::onClientPlayerRespawn);
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(ModEvents::onClientPlayerJoin);
		ClientTickEvent.CLIENT_POST.register(ModKeys::handleThrowExpey);
		ClientRawInputEvent.MOUSE_SCROLLED.register(ModEvents::onMouseScrolled);
	}

	public static void onClientStarted (Minecraft minecraft) {
	}

	/**
	 * 当玩家死亡后重生或加入新的维度时触发
	 */
	public static void onClientPlayerRespawn (LocalPlayer oldPlayer, LocalPlayer newPlayer) {
		onPlayerReset(newPlayer);
		ThirdPersonMod.LOGGER.info("on Client player respawn");
	}

	public static void onClientPlayerJoin (LocalPlayer player) {
		onPlayerReset(player);
		ThirdPersonMod.LOGGER.info("on Client player join");
	}

	/**
	 * 使用滚轮调整距离
	 *
	 * @param minecraft mc
	 * @param amount    向前滚是+1，向后滚是-1
	 */
	private static EventResult onMouseScrolled (Minecraft minecraft, double amount) {
		if (Options.isAdjustingCameraOffset()) {
			double dist = Config.cameraOffsetScheme.getMode().getMaxDistance();
			dist = Config.distanceMonoList.offset(dist, (int)-Math.signum(amount));
			Config.cameraOffsetScheme.getMode().setMaxDistance(dist);
			return EventResult.interruptFalse();
		} else {
			return EventResult.pass();
		}
	}

	public static void onPlayerReset (LocalPlayer player) {
		CameraAgent.reset();
		PlayerAgent.reset();
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
			double sensitivity = mc.options.sensitivity().get() * 0.6 + 0.2;
			double dy          = yMove * sensitivity * 0.15;
			// 相机在头顶，只能上下调整
			double topOffset = scheme.getMode().getCenterOffsetRatio();
			topOffset += -yMove / mc.getWindow().getScreenHeight();
			topOffset = Mth.clamp(topOffset, -1, 1);
			scheme.getMode().setCenterOffsetRatio(topOffset);
		} else {
			// 相机没固定在头顶，可以上下左右调整
			double offsetX = scheme.getMode().getOffsetValue().x;
			double offsetY = scheme.getMode().getOffsetValue().y;
			offsetX += -xMove / mc.getWindow().getScreenWidth();
			offsetY += -yMove / mc.getWindow().getScreenHeight();
			offsetX = Mth.clamp(offsetX, -1, 1);
			offsetY = Mth.clamp(offsetY, -1, 1);
			double newXsgn = Math.signum(offsetX);
			scheme.setSide(newXsgn);
			scheme.getMode().setOffsetRatio(new Vec2((float)offsetX, (float)offsetY));
		}
	}
}
