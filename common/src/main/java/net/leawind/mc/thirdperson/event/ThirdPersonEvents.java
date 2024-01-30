package net.leawind.mc.thirdperson.event;


import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.Window;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.api.cameraoffset.CameraOffsetMode;
import net.leawind.mc.thirdperson.api.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.thirdperson.api.config.Config;
import net.leawind.mc.thirdperson.impl.core.rotation.RotateTarget;
import net.leawind.mc.thirdperson.mixin.CameraMixin;
import net.leawind.mc.thirdperson.mixin.GameRendererMixin;
import net.leawind.mc.thirdperson.mixin.MinecraftMixin;
import net.leawind.mc.thirdperson.mixin.MouseHandlerMixin;
import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.vector.api.Vector2d;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public final class ThirdPersonEvents {
	public static void register () {
		ClientTickEvent.CLIENT_PRE.register(ThirdPersonEvents::onClientTickPre);
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(ThirdPersonEvents::onClientPlayerRespawn);
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(ThirdPersonEvents::onClientPlayerJoin);
		ClientRawInputEvent.MOUSE_SCROLLED.register(ThirdPersonEvents::onMouseScrolled);
	}

	/**
	 * Client tick 前
	 *
	 * @see ClientTickEvent#CLIENT_PRE
	 */
	private static void onClientTickPre (@NotNull Minecraft minecraft) {
		if (minecraft.isPaused()) {
			return;
		}
		if (!ThirdPerson.isAvailable()) {
			return;
		}
		ThirdPerson.ENTITY_AGENT.onClientTickPre();
		ThirdPerson.CAMERA_AGENT.onClientTickPre();
	}

	/**
	 * 当玩家死亡后重生或加入新的维度时触发
	 *
	 * @see ClientPlayerEvent#CLIENT_PLAYER_RESPAWN
	 */
	private static void onClientPlayerRespawn (@NotNull LocalPlayer oldPlayer, @NotNull LocalPlayer newPlayer) {
		onPlayerReset();
		ThirdPerson.LOGGER.info("on Client player respawn");
	}

	/**
	 * 当玩家加入时触发
	 *
	 * @see ClientPlayerEvent#CLIENT_PLAYER_JOIN
	 */
	private static void onClientPlayerJoin (@NotNull LocalPlayer player) {
		onPlayerReset();
		ThirdPerson.LOGGER.info("on Client player join");
	}

	/**
	 * 使用滚轮调整距离
	 *
	 * @param minecraft mc
	 * @param amount    向前滚是+1，向后滚是-1
	 * @see ClientRawInputEvent#MOUSE_SCROLLED
	 */
	private static @NotNull EventResult onMouseScrolled (@NotNull Minecraft minecraft, double amount) {
		Config config = ThirdPerson.getConfig();
		if (ThirdPerson.isAdjustingCameraDistance()) {
			double dist = config.getCameraOffsetScheme().getMode().getMaxDistance();
			dist = config.getDistanceMonoList().offset(dist, (int)-Math.signum(amount));
			config.getCameraOffsetScheme().getMode().setMaxDistance(dist);
			return EventResult.interruptFalse();
		} else {
			return EventResult.pass();
		}
	}

	/**
	 * 重置玩家
	 *
	 * @see ThirdPersonEvents#onClientPlayerRespawn(LocalPlayer, LocalPlayer)
	 * @see ThirdPersonEvents#onClientPlayerJoin(LocalPlayer)
	 */
	private static void onPlayerReset () {
		ThirdPerson.ENTITY_AGENT.reset();
		ThirdPerson.CAMERA_AGENT.reset();
	}

	/**
	 * 调用Camera.setup时触发
	 * <p>
	 * 该调用位于真正渲染画面之前。
	 * <p>
	 * GameRender#render -> GameRender#renderLevel -> Camera#setup
	 *
	 * @see Camera#setup
	 * @see CameraMixin#setup_head
	 */
	public static void onCameraSetup (@NotNull BlockGetter level, float partialTick) {
		ThirdPerson.lastPartialTick = partialTick;
		ThirdPerson.CAMERA_AGENT.setLevel(level);
		Minecraft mc = Minecraft.getInstance();
		if (!ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
			return;
		}
		double now    = Blaze3D.getTime();
		double period = now - ThirdPerson.lastCameraSetupTimeStamp;
		ThirdPerson.lastCameraSetupTimeStamp = now;
		if (ThirdPerson.isThirdPerson()) {
			ThirdPerson.CAMERA_AGENT.onCameraSetup(period);
		}
		if (mc.options.getCameraType().isMirrored()) {
			mc.options.setCameraType(CameraType.FIRST_PERSON);
		}
	}

	/**
	 * gameRenderer 渲染之前
	 *
	 * @see GameRenderer#render(float, long, boolean)
	 * @see GameRendererMixin#pre_render(float, long, boolean, CallbackInfo)
	 */
	public static void onPreRender (float partialTick) {
		double now    = Blaze3D.getTime();
		double period = now - ThirdPerson.lastRenderTickTimeStamp;
		ThirdPerson.lastRenderTickTimeStamp = now;
		if (ThirdPerson.isThirdPerson() && ThirdPerson.isAvailable() && ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
			ThirdPerson.ENTITY_AGENT.onRenderTickPre(period, partialTick);
			ThirdPerson.CAMERA_AGENT.onRenderTickPre(period, partialTick);
		}
	}

	/**
	 * @see ThirdPersonKeys#ADJUST_POSITION
	 */
	public static void onStartAdjustingCameraOffset () {
	}

	/**
	 * @see ThirdPersonKeys#ADJUST_POSITION
	 */
	public static void onStopAdjustingCameraOffset () {
		ThirdPerson.CONFIG_MANAGER.trySave();
	}

	/**
	 * 移动鼠标调整相机偏移
	 *
	 * @param movement 移动的像素
	 * @see MouseHandler#turnPlayer()
	 * @see MouseHandlerMixin#turnPlayer_head(CallbackInfo)
	 */
	public static void onAdjustingCameraOffset (@NotNull Vector2d movement) {
		if (movement.lengthSquared() == 0) {
			return;
		}
		Config             config     = ThirdPerson.getConfig();
		Window             window     = Minecraft.getInstance().getWindow();
		Vector2d           screenSize = Vector2d.of(window.getScreenWidth(), window.getScreenHeight());
		CameraOffsetScheme scheme     = config.getCameraOffsetScheme();
		CameraOffsetMode   mode       = scheme.getMode();
		if (mode.isCentered()) {
			// 相机在头顶，只能上下调整
			double topOffset = mode.getCenterOffsetRatio();
			topOffset += -movement.y() / screenSize.y();
			topOffset = LMath.clamp(topOffset, -1, 1);
			mode.setCenterOffsetRatio(topOffset);
		} else {
			// 相机没固定在头顶，可以上下左右调整
			Vector2d offset = mode.getSideOffsetRatio(Vector2d.of());
			offset.sub(movement.div(screenSize));
			offset.clamp(-1, 1);
			scheme.setSide(Math.signum(offset.x()));
			mode.setSideOffsetRatio(offset);
		}
	}

	/**
	 * 当玩家与环境交互时，趁交互事件处理前，让玩家看向相机落点
	 *
	 * @see MinecraftMixin#handleKeybinds_head(CallbackInfo)
	 */
	public static void onBeforeHandleKeybinds () {
		if (ThirdPerson.ENTITY_AGENT.wasInterecting()) {
			// 该方法中使用了mixin，修改了 viewVector
			Minecraft.getInstance().gameRenderer.pick(1.0f);
		}
	}

	/**
	 * 退出第三人称视角
	 *
	 * @see CameraMixin#setup_head(BlockGetter, Entity, boolean, boolean, float, CallbackInfo)
	 */
	public static void onLeaveThirdPerson () {
		if (ThirdPerson.getConfig().turn_with_camera_when_enter_first_person) {
			ThirdPerson.ENTITY_AGENT.setRotateTarget(RotateTarget.CAMERA_ROTATION);
		}
	}

	/**
	 * 进入第三人称视角时触发
	 *
	 * @see CameraMixin#setup_head(BlockGetter, Entity, boolean, boolean, float, CallbackInfo)
	 */
	public static void onEnterThirdPerson () {
		ThirdPerson.lastPartialTick          = Minecraft.getInstance().getFrameTime();
		ThirdPerson.lastCameraSetupTimeStamp = Blaze3D.getTime();
		ThirdPerson.CAMERA_AGENT.reset();
		ThirdPerson.ENTITY_AGENT.reset();
	}
}
