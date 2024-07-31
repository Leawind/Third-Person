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
import net.leawind.mc.thirdperson.cameraoffset.AbstractCameraOffsetMode;
import net.leawind.mc.thirdperson.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.util.ItemPattern;
import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.vector.Vector2d;
import net.leawind.mc.util.math.vector.Vector3d;
import net.minecraft.client.Camera;
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
			GameEvents.thirdPersonCameraSetup = ThirdPersonEvents::onThirdPersonCameraSetup;
			GameEvents.minecraftPick          = ThirdPersonEvents::onMinecraftPickEvent;
			GameEvents.renderTickStart        = ThirdPersonEvents::onRenderTickStart;
			GameEvents.calculateMoveImpulse   = ThirdPersonEvents::onCalculateMoveImpulse;
			GameEvents.renderEntity           = ThirdPersonEvents::onRenderEntity;
			GameEvents.handleKeybindsStart    = ThirdPersonEvents::onHandleKeybindsStart;
			GameEvents.mouseTurnPlayerStart   = ThirdPersonEvents::onMouseTurnPlayerStart;
			GameEvents.entityTurnStart        = ThirdPersonEvents::onEntityTurnStart;
		}
	}

	private static void onEntityTurnStart (EntityTurnStartEvent event) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && !ThirdPersonStatus.shouldCameraTurnWithEntity()) {
			ThirdPerson.CAMERA_AGENT.turnCamera(event.dYRot, event.dXRot);
			event.cancelDefault();
		}
	}

	private static boolean onRenderEntity (RenderEntityEvent event) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && event.entity == ThirdPerson.ENTITY_AGENT.getRawCameraEntity()) {
			return ThirdPersonStatus.shouldRenderCameraEntity();
		}
		return true;
	}

	private static void onCalculateMoveImpulse (CalculateMoveImpulseEvent event) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && ThirdPerson.ENTITY_AGENT.isControlled()) {
			Camera camera = ThirdPerson.CAMERA_AGENT.getRawCamera();
			// 计算世界坐标系下的向前和向左 impulse
			Vector3d lookImpulse        = LMath.toVector3d(camera.getLookVector()).normalize();    // 视线向量
			Vector3d leftImpulse        = LMath.toVector3d(camera.getLeftVector()).normalize();
			Vector2d lookImpulseHorizon = Vector2d.of(lookImpulse.x(), lookImpulse.z()).normalize().mul(event.forwardImpulse);    // 水平方向上的视线向量
			Vector2d leftImpulseHorizon = Vector2d.of(leftImpulse.x(), leftImpulse.z()).normalize().mul(event.leftImpulse);
			lookImpulseHorizon.add(leftImpulseHorizon, ThirdPersonStatus.impulseHorizon);
			// 世界坐标系下的 impulse
			lookImpulse.mul(event.forwardImpulse);    // 这才是 impulse
			leftImpulse.mul(event.leftImpulse);
			lookImpulse.add(leftImpulse, ThirdPersonStatus.impulse);
			// impulse 不为0，
			if (ThirdPersonStatus.impulseHorizon.length() > 1E-5) {
				float    playerYRot        = ThirdPerson.ENTITY_AGENT.getRawPlayerEntity().getViewYRot(ThirdPersonStatus.lastPartialTick);
				Vector2d playerLookHorizon = LMath.directionFromRotationDegree(playerYRot).normalize();
				Vector2d playerLeftHorizon = LMath.directionFromRotationDegree(playerYRot - 90).normalize();
				event.forwardImpulse = (float)(ThirdPersonStatus.impulseHorizon.dot(playerLookHorizon));
				event.leftImpulse    = (float)(ThirdPersonStatus.impulseHorizon.dot(playerLeftHorizon));
			}
		}
	}

	private static void onMinecraftPickEvent (MinecraftPickEvent event) {
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
	private static void onThirdPersonCameraSetup (ThirdPersonCameraSetupEvent event) {
		if (ThirdPerson.isAvailable()) {
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
		Config config = ThirdPerson.getConfig();
		if (ThirdPerson.mc.options.getCameraType() != CameraType.FIRST_PERSON) {
			// 目标是第三人称
			Entity cameraEntity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
			// 如果非旁观者模式的玩家在墙里边，就暂时切换到第一人称
			GameStatus.isPerspectiveInverted = !cameraEntity.isSpectator() && cameraEntity.isInWall();
			if (cameraEntity instanceof LivingEntity livingEntity && livingEntity.isUsingItem()) {
				if (ItemPattern.anyMatch(livingEntity.getUseItem(), config.getUseToFirstPersonItemPatterns(), ThirdPersonResources.itemPatternManager.useToFirstPersonItemPatterns)) {
					GameStatus.isPerspectiveInverted = true;
				}
			}
		}
		ThirdPerson.ENTITY_AGENT.onClientTickStart();
		ThirdPerson.CAMERA_AGENT.onClientTickStart();
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
	private static void onRenderTickStart (RenderTickStartEvent event) {
		if (!ThirdPerson.getConfig().is_mod_enable) {
			return;
		}
		ThirdPerson.CAMERA_AGENT.checkGameStatus();
		ThirdPersonStatus.lastPartialTick = event.partialTick;
		// in seconds
		double now    = System.currentTimeMillis() / 1000D;
		double period = now - ThirdPersonStatus.lastRenderTickTimeStamp;
		ThirdPersonStatus.lastRenderTickTimeStamp = now;
		final boolean isRenderingInThirdPerson = ThirdPersonStatus.isRenderingInThirdPerson();
		if (isRenderingInThirdPerson != ThirdPersonStatus.wasRenderInThirdPersonLastRenderTick) {
			if (isRenderingInThirdPerson) {
				onEnterThirdPerson();
			} else {
				onEnterFirstPerson();
			}
			ThirdPerson.mc.levelRenderer.needsUpdate();
			ThirdPersonStatus.wasRenderInThirdPersonLastRenderTick = isRenderingInThirdPerson;
		}
		if (isRenderingInThirdPerson) {
			boolean shouldCameraTurnWithEntity = ThirdPersonStatus.shouldCameraTurnWithEntity();
			if (shouldCameraTurnWithEntity && !ThirdPersonStatus.wasShouldCameraTurnWithEntity) {
				// 将玩家朝向设为与相机一致
				if (ThirdPersonStatus.isRenderingInThirdPerson()) {
					ThirdPerson.ENTITY_AGENT.setRawRotation(ThirdPerson.CAMERA_AGENT.getRotation());
				}
			}
			ThirdPersonStatus.wasShouldCameraTurnWithEntity = shouldCameraTurnWithEntity;
		}
		if (ThirdPerson.isAvailable() && ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
			ThirdPerson.ENTITY_AGENT.onRenderTickStart(now, period, event.partialTick);
			ThirdPerson.CAMERA_AGENT.onRenderTickStart(now, period, event.partialTick);
		}
		GameStatus.allowThirdPersonCrosshair = ThirdPersonStatus.shouldRenderCrosshair();
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
	 * 如果此时相机跟随玩家旋转，那么不做修改，让鼠标直接控制玩家旋转。
	 *
	 * @see MouseHandler#turnPlayer()
	 * @see MouseHandlerMixin#preMouseTurnPlayer(CallbackInfo)
	 */
	private static void onMouseTurnPlayerStart (MouseTurnPlayerStartEvent event) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isAdjustingCameraOffset() && !ThirdPersonStatus.shouldCameraTurnWithEntity()) {
			if (event.accumulatedDX == 0 || event.accumulatedDY == 0) {
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
				topOffset += -event.accumulatedDY / screenSize.y();
				topOffset = LMath.clamp(topOffset, -1, 1);
				mode.setCenterOffsetRatio(topOffset);
			} else {
				// 相机没固定在头顶，可以上下左右调整
				Vector2d offset = mode.getSideOffsetRatio(Vector2d.of());
				offset.sub(Vector2d.of(event.accumulatedDX, event.accumulatedDY).div(screenSize));
				offset.clamp(-1, 1);
				scheme.setSide(Math.signum(offset.x()));
				mode.setSideOffsetRatio(offset);
			}
			event.cancelDefault();
		}
	}

	/**
	 * @see MinecraftMixin#preHandleKeybinds(CallbackInfo)
	 */
	private static void onHandleKeybindsStart () {
		if (ThirdPerson.isAvailable()) {
			Config config = ThirdPerson.getConfig();
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
	private static void onEnterFirstPerson () {
		ThirdPerson.ENTITY_AGENT.setRawRotation(ThirdPerson.CAMERA_AGENT.getRotation());
		ThirdPerson.mc.gameRenderer.checkEntityPostEffect(ThirdPerson.mc.getCameraEntity());
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
