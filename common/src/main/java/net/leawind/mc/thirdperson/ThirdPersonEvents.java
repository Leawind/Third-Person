package net.leawind.mc.thirdperson;


import com.mojang.blaze3d.platform.Window;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.leawind.mc.api.base.GameEvents;
import net.leawind.mc.api.base.GameStatus;
import net.leawind.mc.api.client.events.*;
import net.leawind.mc.mixin.GameRendererMixin;
import net.leawind.mc.mixin.MinecraftMixin;
import net.leawind.mc.mixin.MouseHandlerMixin;
import net.leawind.mc.thirdperson.mod.cameraoffset.AbstractCameraOffsetMode;
import net.leawind.mc.thirdperson.mod.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.thirdperson.mod.config.Config;
import net.leawind.mc.util.itempattern.ItemPattern;
import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.vector.api.Vector2d;
import net.leawind.mc.util.math.vector.api.Vector3d;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
public final class ThirdPersonEvents {
	public static void register () {
		ClientTickEvent.CLIENT_PRE.register(ThirdPersonEvents::onClientTickPre);
		ClientLifecycleEvent.CLIENT_STOPPING.register(ThirdPersonEvents::onClientStopping);
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(ThirdPersonEvents::onClientPlayerRespawn);
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(ThirdPersonEvents::onClientPlayerJoin);
		ClientRawInputEvent.MOUSE_SCROLLED.register(ThirdPersonEvents::onMouseScrolled);
		{
			GameEvents.setupCamera          = ThirdPersonEvents::onCameraSetup;
			GameEvents.minecraftPick        = ThirdPersonEvents::onMinecraftPickEvent;
			GameEvents.preRenderTick        = ThirdPersonEvents::onPreRender;
			GameEvents.calculateMoveImpulse = ThirdPersonEvents::onCalculateMoveImpulse;
			GameEvents.renderEntity         = ThirdPersonEvents::onRenderEntity;
			GameEvents.preHandleKeybinds    = ThirdPersonEvents::onPreHandleKeybinds;
		}
	}

	public static boolean onRenderEntity (RenderEntityEvent event) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && event.entity == ThirdPerson.ENTITY_AGENT.getRawCameraEntity()) {
			return ThirdPersonStatus.shouldRenderCameraEntity();
		}
		return true;
	}

	public static void onCalculateMoveImpulse (CalculateMoveImpulseEvent event) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && ThirdPerson.ENTITY_AGENT.isControlled()) {
			// 相机坐标系下的impulse
			double cameraLookImpulse = (event.input.up ? 1: 0) - (event.input.down ? 1: 0);
			double cameraLeftImpulse = (event.input.left ? 1: 0) - (event.input.right ? 1: 0);
			// 计算世界坐标系下的向前和向左 impulse
			Vector3d lookImpulse        = LMath.toVector3d(ThirdPerson.CAMERA_AGENT.getRawCamera().getLookVector()).normalize();    // 视线向量
			Vector3d leftImpulse        = LMath.toVector3d(ThirdPerson.CAMERA_AGENT.getRawCamera().getLeftVector()).normalize();
			Vector2d lookImpulseHorizon = Vector2d.of(lookImpulse.x(), lookImpulse.z()).normalize();    // 水平方向上的视线向量
			Vector2d leftImpulseHorizon = Vector2d.of(leftImpulse.x(), leftImpulse.z()).normalize();
			lookImpulse.mul(cameraLookImpulse);    // 这才是 impulse
			leftImpulse.mul(cameraLeftImpulse);
			lookImpulseHorizon.mul(cameraLookImpulse);    // 水平 impulse
			leftImpulseHorizon.mul(cameraLeftImpulse);
			// 世界坐标系下的 impulse
			lookImpulse.add(leftImpulse, ThirdPersonStatus.impulse);
			lookImpulseHorizon.add(leftImpulseHorizon, ThirdPersonStatus.impulseHorizon);
			// impulse 不为0，
			if (ThirdPersonStatus.impulseHorizon.length() > 1E-5) {
				ThirdPersonStatus.impulseHorizon.normalize();
				float    playerYRot        = ThirdPerson.ENTITY_AGENT.getRawPlayerEntity().getViewYRot(ThirdPersonStatus.lastPartialTick);
				Vector2d playerLookHorizon = LMath.directionFromRotationDegree(playerYRot).normalize();
				Vector2d playerLeftHorizon = LMath.directionFromRotationDegree(playerYRot - 90).normalize();
				event.forwardImpulse = (float)(ThirdPersonStatus.impulseHorizon.dot(playerLookHorizon));
				event.leftImpulse    = (float)(ThirdPersonStatus.impulseHorizon.dot(playerLeftHorizon));
			}
		}
	}

	public static void onMinecraftPickEvent (MinecraftPickEvent event) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			Entity cameraEntity      = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
			Vec3   cameraPosition    = ThirdPerson.CAMERA_AGENT.getRawCamera().getPosition();
			Vec3   eyePosition       = cameraEntity.getEyePosition(event.partialTick);
			Vec3   cameraHitPosition = ThirdPerson.CAMERA_AGENT.getHitResult().getLocation();
			event.pickTo(cameraHitPosition);
			double pickRange;
			if (ThirdPersonStatus.shouldPickFromCamera()) {
				event.pickFrom(cameraPosition);
				event.setPickRange(cameraHitPosition.distanceTo(cameraPosition) + 1e-5);
			} else {
				event.pickFrom(eyePosition);
				event.setPickRange(event.playerReach);
			}
		}
	}

	/**
	 * 设置相机位置和朝向
	 */
	public static void onCameraSetup (CameraSetupEvent event) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			ThirdPerson.CAMERA_AGENT.setBlockGetter(event.level);
			if (!ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
				return;
			}
			if (ThirdPersonStatus.isRenderingInThirdPerson()) {
				ThirdPerson.CAMERA_AGENT.onCameraSetup(event);
			}
		}
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
		Config  config                  = ThirdPerson.getConfig();
		boolean wasTemporaryFirstPerson = ThirdPersonStatus.isTemporaryFirstPerson;
		ThirdPersonStatus.isTemporaryFirstPerson = false;
		if (config.is_third_person_mode) {
			Entity cameraEntity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
			if (!cameraEntity.isSpectator() && cameraEntity.isInWall()) {
				// 如果非旁观者模式的玩家在墙里边，就暂时切换到第一人称
				ThirdPersonStatus.isTemporaryFirstPerson = true;
			}
			if (cameraEntity instanceof LivingEntity livingEntity && livingEntity.isUsingItem()) {
				if (ItemPattern.anyMatch(livingEntity.getUseItem(), config.getUseToFirstPersonItemPatterns(), ThirdPersonResources.itemPatternManager.useToFirstPersonItemPatterns)) {
					ThirdPersonStatus.isTemporaryFirstPerson = true;
				}
			}
			if (ThirdPersonStatus.isTemporaryFirstPerson && !wasTemporaryFirstPerson) {
				ThirdPerson.ENTITY_AGENT.setRawRotation(ThirdPerson.CAMERA_AGENT.getRotation());
				ThirdPerson.mc.options.setCameraType(CameraType.FIRST_PERSON);
				ThirdPerson.mc.gameRenderer.checkEntityPostEffect(ThirdPerson.mc.getCameraEntity());
			}
		}
		ThirdPerson.ENTITY_AGENT.onClientTickPre();
		ThirdPerson.CAMERA_AGENT.onClientTickPre();
	}

	private static void onClientStopping (Minecraft minecraft) {
		ThirdPerson.CONFIG_MANAGER.trySave();
	}

	/**
	 * 当玩家死亡后重生或加入新的维度时触发
	 *
	 * @see ClientPlayerEvent#CLIENT_PLAYER_RESPAWN
	 */
	private static void onClientPlayerRespawn (@NotNull LocalPlayer oldPlayer, @NotNull LocalPlayer newPlayer) {
		if (ThirdPerson.getConfig().is_mod_enable) {
			onPlayerReset();
			ThirdPerson.LOGGER.info("on Client player respawn");
		}
	}

	/**
	 * 当玩家加入时触发
	 *
	 * @see ClientPlayerEvent#CLIENT_PLAYER_JOIN
	 */
	private static void onClientPlayerJoin (@NotNull LocalPlayer player) {
		if (ThirdPerson.getConfig().is_mod_enable) {
			onPlayerReset();
			ThirdPerson.LOGGER.info("on Client player join");
		}
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
		if (ThirdPersonStatus.isAdjustingCameraDistance()) {
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
	 * gameRenderer 渲染之前
	 *
	 * @see GameRenderer#render(float, long, boolean)
	 * @see GameRendererMixin#pre_render(float, long, boolean, CallbackInfo)
	 */
	public static void onPreRender (PreRenderTickEvent event) {
		if (!ThirdPerson.getConfig().is_mod_enable) {
			return;
		}
		ThirdPersonStatus.lastPartialTick = event.partialTick;
		// in seconds
		double now    = System.currentTimeMillis() / 1000D;
		double period = now - ThirdPersonStatus.lastRenderTickTimeStamp;
		ThirdPersonStatus.lastRenderTickTimeStamp = now;
		final boolean isRenderInThirdPerson = !ThirdPerson.mc.options.getCameraType().isFirstPerson();
		if (isRenderInThirdPerson != ThirdPersonStatus.wasRenderInThirdPersonLastRenderTick) {
			if (isRenderInThirdPerson) {
				onEnterThirdPerson();
			} else {
				onEnterFirstPerson();
			}
			ThirdPerson.mc.levelRenderer.needsUpdate();
			ThirdPersonStatus.wasRenderInThirdPersonLastRenderTick = isRenderInThirdPerson;
		}
		if (isRenderInThirdPerson) {
			boolean shouldCameraTurnWithEntity = ThirdPersonStatus.shouldCameraTurnWithEntity();
			if (shouldCameraTurnWithEntity && !ThirdPersonStatus.wasSouldCameraTurnWithEntity) {
				onStartCameraTurnWithEntity();
			}
			ThirdPersonStatus.wasSouldCameraTurnWithEntity = shouldCameraTurnWithEntity;
		}
		if (ThirdPerson.isAvailable() && ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
			if (ThirdPersonStatus.isRenderingInThirdPerson()) {
				ThirdPerson.ENTITY_AGENT.onPreRender(now, period, event.partialTick);
				ThirdPerson.CAMERA_AGENT.onPreRender(now, period, event.partialTick);
			}
		}
		GameStatus.allowThirdPersonCrosshair = ThirdPersonStatus.shouldRenderCrosshair();
	}

	/**
	 * 进入“相机跟随玩家转动”状态
	 */
	public static void onStartCameraTurnWithEntity () {
		// 将玩家朝向设为与相机一致
		if (ThirdPersonStatus.isRenderingInThirdPerson()) {
			ThirdPerson.ENTITY_AGENT.setRawRotation(ThirdPerson.CAMERA_AGENT.getRotation());
		}
	}

	/**
	 * @see ThirdPersonKeys#ADJUST_POSITION
	 */
	@SuppressWarnings("EmptyMethod")
	public static void onStartAdjustingCameraOffset () {
	}

	/**
	 * @see ThirdPersonKeys#ADJUST_POSITION
	 */
	public static void onStopAdjustingCameraOffset () {
		ThirdPerson.CONFIG_MANAGER.lazySave();
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
		Config                   config     = ThirdPerson.getConfig();
		Window                   window     = ThirdPerson.mc.getWindow();
		Vector2d                 screenSize = Vector2d.of(window.getScreenWidth(), window.getScreenHeight());
		CameraOffsetScheme       scheme     = config.getCameraOffsetScheme();
		AbstractCameraOffsetMode mode       = scheme.getMode();
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
	 * @see MinecraftMixin#handleKeybinds_head(CallbackInfo)
	 */
	public static void onPreHandleKeybinds () {
		if (ThirdPerson.isAvailable()) {
			Config config = ThirdPerson.getConfig();
		/*
		  接管“切换视角”按键绑定
		 */
			while (ThirdPerson.mc.options.keyTogglePerspective.consumeClick()) {
				config.is_third_person_mode = !config.is_third_person_mode;
			}
			if (ThirdPersonStatus.isRenderingInThirdPerson()) {
				if (ThirdPerson.ENTITY_AGENT.isInterecting()) {
					// 立即更新玩家注视着的目标 Minecraft#hitResult
					ThirdPerson.mc.gameRenderer.pick(1f);
				}
			}
		}
	}

	/**
	 * 进入第一人称视角
	 */
	public static void onEnterFirstPerson () {
		ThirdPerson.mc.gameRenderer.checkEntityPostEffect(ThirdPerson.ENTITY_AGENT.getRawCameraEntity());
	}

	/**
	 * 进入第三人称视角
	 */
	public static void onEnterThirdPerson () {
		ThirdPersonStatus.lastPartialTick = Minecraft.getInstance().getFrameTime();
		ThirdPerson.mc.gameRenderer.checkEntityPostEffect(null);
		ThirdPerson.CAMERA_AGENT.reset();
		ThirdPerson.ENTITY_AGENT.reset();
	}
}
