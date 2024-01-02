package net.leawind.mc.thirdperson.core;


import com.mojang.blaze3d.Blaze3D;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetMode;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.thirdperson.mixin.CameraInvoker;
import net.leawind.mc.thirdperson.mixin.LocalPlayerInvoker;
import net.leawind.mc.util.math.Vec2d;
import net.leawind.mc.util.math.Vec3d;
import net.leawind.mc.util.smoothvalue.ExpSmoothDouble;
import net.leawind.mc.util.smoothvalue.ExpSmoothVec2d;
import net.leawind.mc.util.smoothvalue.ExpSmoothVec3d;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CameraAgent {
	public static final Logger          LOGGER                     = LoggerFactory.getLogger(ThirdPersonMod.MOD_ID);
	/**
	 * 成像平面到相机的距离，这是一个固定值，硬编码在Minecraft源码中。
	 * <p>
	 * 取自 {@link net.minecraft.client.Camera#getNearPlane()}
	 */
	public static final double          NEAR_PLANE_DISTANCE        = 0.05;
	@Nullable
	public static       BlockGetter     level;
	public static       Camera          camera;
	protected static    Camera          fakeCamera                 = new Camera();
	/**
	 * renderTick 中更新
	 */
	public static       boolean         wasAttachedEntityInvisible = false;
	/**
	 * 在 renderTick 中更新
	 */
	public static       boolean         wasAiming                  = false;
	/**
	 * 相机偏移量
	 */
	public static       ExpSmoothVec2d  smoothOffsetRatio          = new ExpSmoothVec2d().setValue(0, 0);
	/**
	 * 上一次 render tick 的时间戳
	 */
	public static       double          lastRenderTickTimeStamp    = 0;
	/**
	 * 上次玩家操控转动视角的时间
	 */
	public static       double          lastCameraTurnTimeStamp    = 0;
	/**
	 * 虚相机到平滑眼睛的距离
	 */
	public static       ExpSmoothDouble smoothVirtualDistance      = new ExpSmoothDouble().setValue(0).setTarget(0);
	/**
	 * 眼睛的平滑位置
	 */
	public static       ExpSmoothVec3d  smoothEyePosition          = new ExpSmoothVec3d();
	public static       Vec2d           relativeRotation           = Vec2d.ZERO;

	/**
	 * 判断：模组功能已启用，且相机和玩家都已经初始化
	 */
	public static boolean isAvailable () {
		Minecraft mc = Minecraft.getInstance();
		if (!Config.is_mod_enable) {
			return false;
		} else if (!mc.gameRenderer.getMainCamera().isInitialized()) {
			return false;
		} else {
			return mc.player != null;
		}
	}

	/**
	 * 当前是否在控制玩家
	 * <p>
	 * 如果当前玩家处于旁观者模式，附着在其他实体上，则返回false
	 */
	public static boolean isControlledCamera () {
		Minecraft mc = Minecraft.getInstance();
		return (mc.player != null) && ((LocalPlayerInvoker)mc.player).invokeIsControlledCamera();
	}

	/**
	 * 鼠标移动导致的相机旋转
	 *
	 * @param y 偏航角变化量
	 * @param x 俯仰角变化量
	 */
	public static void onCameraTurn (double y, double x) {
		if (Config.is_mod_enable && !ModOptions.isAdjustingCameraOffset()) {
			y *= 0.15;
			x *= Config.lock_camera_pitch_angle ? 0: -0.15;
			if (y != 0 || x != 0) {
				lastCameraTurnTimeStamp = Blaze3D.getTime();
				relativeRotation        = new Vec2d(Mth.clamp(relativeRotation.x + x, -89.8, 89.8),
													(relativeRotation.y + y) % 360f);
			}
		}
	}

	/**
	 * 进入第三人称视角时触发
	 */
	public static void onEnterThirdPerson () {
		reset();
		PlayerAgent.reset();
		wasAiming                   = false;
		ModOptions.isToggleToAiming = false;
		lastRenderTickTimeStamp     = Blaze3D.getTime();
	}

	/**
	 * 重置玩家对象，重置相机的位置、角度等参数
	 */
	public static void reset () {
		Minecraft mc = Minecraft.getInstance();
		camera = mc.gameRenderer.getMainCamera();
		smoothOffsetRatio.setValue(0, 0);
		smoothVirtualDistance.set(Config.distanceMonoList.get(0));
		if (mc.cameraEntity != null) {
			relativeRotation = new Vec2d(-mc.cameraEntity.getViewXRot(mc.getFrameTime()),
										 mc.cameraEntity.getViewYRot(mc.getFrameTime()) - 180);
		}
		LOGGER.info("Reset CameraAgent");
	}

	/**
	 * 退出第三人称视角
	 */
	public static void onLeaveThirdPerson () {
		if (Config.turn_with_camera_when_enter_first_person) {
			PlayerAgent.turnWithCamera(true);
		}
	}

	/**
	 * 计算并更新相机的朝向和坐标
	 *
	 * @param level          维度
	 * @param attachedEntity 附着的实体
	 */
	@PerformanceSensitive
	public static void onRenderTick (BlockGetter level, Entity attachedEntity, float partialTick) {
		PlayerAgent.lastPartialTick = partialTick;
		CameraAgent.level           = level;
		wasAiming                   = PlayerAgent.isAiming();
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) {
			return;
		}
		Vec3 eyePosition = attachedEntity.getEyePosition(partialTick);
		if (mc.options.getCameraType().isMirrored()) {
			mc.options.setCameraType(CameraType.FIRST_PERSON);
		}
		// 时间
		double now = Blaze3D.getTime();
		//		double now      = mc.frameTimer.getLogEnd();
		double tickCost = now - lastRenderTickTimeStamp;
		lastRenderTickTimeStamp = now;
		CameraOffsetScheme scheme = Config.cameraOffsetScheme;
		scheme.setAiming(wasAiming);
		if (isThirdPerson()) {
			// 平滑更新距离
			updateSmoothVirtualDistance(tickCost);
			// 平滑更新相机偏移量
			updateSmoothOffsetRatio(tickCost);
			//			// 更新眼睛位置
			//			updateSmoothEyePosition(tickCost);
			// 设置相机朝向和位置
			updateFakeCameraRotationPosition();
			preventThroughWall();
			updateFakeCameraRotationPosition();
			applyCamera();
			CameraAgent.wasAttachedEntityInvisible = ModOptions.isAttachedEntityInvisible();
			if (CameraAgent.wasAttachedEntityInvisible) {
				((CameraInvoker)fakeCamera).invokeSetPosition(eyePosition);
				applyCamera();
			}
		}
		PlayerAgent.onRenderTick();
	}

	public static Vec3 getPositionWithoutOffset () {
		//		return smoothEyePosition.get().add(Vec3.directionFromRotation(relativeRotation).scale(smoothVirtualDistance
		//		.get()));
		return smoothEyePosition.get(PlayerAgent.lastPartialTick).add(Vec3d.directionFromRotation(relativeRotation)
																		   .scale(smoothVirtualDistance.get()));
	}

	public static void updateSmoothVirtualDistance (double tickCost) {
		boolean          isAdjusting = ModOptions.isAdjustingCameraOffset();
		CameraOffsetMode mode        = Config.cameraOffsetScheme.getMode();
		smoothVirtualDistance.setSmoothFactor(isAdjusting ? 1E-5: mode.getDistanceSmoothFactor());
		smoothVirtualDistance.setTarget(mode.getMaxDistance()).update(tickCost);
		// 如果是非瞄准模式下，且距离过远则强行放回去
		if (!Config.cameraOffsetScheme.isAiming && !isAdjusting) {
			smoothVirtualDistance.set(Math.min(mode.getMaxDistance(), smoothVirtualDistance.get()));
		}
	}

	public static void updateSmoothOffsetRatio (double tickCost) {
		smoothOffsetRatio.setSmoothFactor(ModOptions.isAdjustingCameraOffset()
										  ? new Vec2d(1e-7F, 1e-7F)
										  : Config.cameraOffsetScheme.getMode().getOffsetSmoothFactor());
		smoothOffsetRatio.setTarget(Config.cameraOffsetScheme.getMode().getOffsetRatio());
		smoothOffsetRatio.update(tickCost);
	}

	public static void updateSmoothEyePosition (double tickCost) {
		Minecraft          mc     = Minecraft.getInstance();
		CameraOffsetScheme scheme = Config.cameraOffsetScheme;
		if (mc.cameraEntity != null && mc.player != null) {
			Vec3 eyePosition = mc.cameraEntity.getEyePosition(mc.getFrameTime());
			if (CameraAgent.wasAttachedEntityInvisible) {
				// 假的第一人称，没有平滑
				CameraAgent.smoothEyePosition.setValue(eyePosition);
			} else {
				// 平滑更新眼睛位置，飞行时使用专用的平滑系数
				if (mc.player.isFallFlying()) {
					CameraAgent.smoothEyePosition.setSmoothFactor(Config.flying_smooth_factor);
				} else {
					CameraAgent.smoothEyePosition.setSmoothFactor(scheme.getMode().getEyeSmoothFactor());
				}
				CameraAgent.smoothEyePosition.setTarget(eyePosition).update(tickCost);
			}
		}
	}

	public static boolean isThirdPerson () {
		return !Minecraft.getInstance().options.getCameraType().isFirstPerson();
	}

	/**
	 * 根据角度、距离、偏移量计算假相机实际朝向和位置
	 */
	private static void updateFakeCameraRotationPosition () {
		Minecraft mc = Minecraft.getInstance();
		// 宽高比
		double aspectRatio = (double)mc.getWindow().getWidth() / mc.getWindow().getHeight();
		// 垂直视野角度一半(弧度制）
		double verticalRadianHalf = Math.toRadians(mc.options.fov().get()) / 2;
		// 成像平面宽高
		double heightHalf = Math.tan(verticalRadianHalf) * NEAR_PLANE_DISTANCE;
		double widthHalf  = aspectRatio * heightHalf;
		//		// 水平视野角度一半(弧度制）
		//		double horizonalRadianHalf = Math.atan(widthHalf / NEAR_PLANE_DISTANCE);
		// 没有偏移的情况下相机位置
		Vec3 positionWithoutOffset = getPositionWithoutOffset();
		// 应用到假相机
		((CameraInvoker)fakeCamera).invokeSetRotation((float)(relativeRotation.y + 180), (float)-relativeRotation.x);
		((CameraInvoker)fakeCamera).invokeSetPosition(positionWithoutOffset);
		double leftOffset = smoothOffsetRatio.get().x * smoothVirtualDistance.get() * widthHalf / NEAR_PLANE_DISTANCE;
		double upOffset   = smoothOffsetRatio.get().y * smoothVirtualDistance.get() * Math.tan(verticalRadianHalf);
		((CameraInvoker)fakeCamera).invokeMove(0, upOffset, leftOffset);
	}

	/**
	 * 为防止穿墙，重新计算 smoothVirtualDistance 的值
	 */
	public static void preventThroughWall () {
		final double offset = 0.18;
		// 防止穿墙
		Vec3   cameraPosition = fakeCamera.getPosition();
		Vec3   eyePosition    = smoothEyePosition.get();
		Vec3   eyeToCamera    = eyePosition.vectorTo(cameraPosition);
		double initDistance   = eyeToCamera.length();
		double minDistance    = initDistance;
		assert level != null;
		for (int i = 0; i < 8; ++i) {
			double offsetX = (i & 1) * 2 - 1;
			double offsetY = (i >> 1 & 1) * 2 - 1;
			double offsetZ = (i >> 2 & 1) * 2 - 1;
			offsetX *= offset;
			offsetY *= offset;
			offsetZ *= offset;
			Vec3 pickStart = eyePosition.add(offsetX, offsetY, offsetZ);
			HitResult hitresult = level.clip(new ClipContext(pickStart,
															 pickStart.add(eyeToCamera),
															 ClipContext.Block.VISUAL,
															 ClipContext.Fluid.NONE,
															 Minecraft.getInstance().cameraEntity));
			if (hitresult.getType() != HitResult.Type.MISS) {
				minDistance = Math.min(minDistance, hitresult.getLocation().distanceTo(pickStart));
			}
		}
		smoothVirtualDistance.setValue(smoothVirtualDistance.get() * minDistance / initDistance);
	}

	/**
	 * 将假相机的朝向和位置应用到真相机上
	 */
	private static void applyCamera () {
		// 应用到真相机
		((CameraInvoker)camera).invokeSetRotation(fakeCamera.getYRot(), fakeCamera.getXRot());
		((CameraInvoker)camera).invokeSetPosition(fakeCamera.getPosition());
	}

	/**
	 * 根据相对角度计算相机朝向
	 */
	public static Vec2d calculateRotation () {
		return new Vec2d(relativeRotation.y + 180, -relativeRotation.x);
	}

	/**
	 * 获取相机视线落点坐标
	 */
	public static @Nullable Vec3 getPickPosition () {
		return getPickPosition(smoothVirtualDistance.get() + Config.camera_ray_trace_length);
	}

	/**
	 * 获取相机视线落点坐标
	 *
	 * @param pickRange 最大探测距离
	 */
	public static @Nullable Vec3 getPickPosition (double pickRange) {
		HitResult hitResult = pick(pickRange);
		return hitResult.getType() == HitResult.Type.MISS ? null: hitResult.getLocation();
	}

	public static @NotNull HitResult pick () {
		return pick(smoothVirtualDistance.get() + Config.camera_ray_trace_length);
	}

	public static @NotNull HitResult pick (double pickRange) {
		EntityHitResult ehr = pickEntity(pickRange);
		return ehr == null ? pickBlock(pickRange): ehr;
	}

	private static @Nullable EntityHitResult pickEntity (double pickRange) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.cameraEntity == null) {
			return null;
		}
		Vec3 viewStart  = camera.getPosition();
		Vec3 viewVector = new Vec3(camera.getLookVector());
		Vec3 viewEnd    = viewVector.scale(pickRange).add(viewStart);
		//
		AABB aabb = mc.cameraEntity.getBoundingBox().expandTowards(viewVector.scale(pickRange)).inflate(1.0D, 1.0D, 1.0D);
		return ProjectileUtil.getEntityHitResult(mc.cameraEntity,
												 viewStart,
												 viewEnd,
												 aabb,
												 (Entity target) -> !target.isSpectator() && target.isPickable(),
												 pickRange);
	}

	/**
	 * pick 方块
	 * <p>
	 * 瞄准时忽略草
	 */
	private static @NotNull BlockHitResult pickBlock (double pickRange) {
		Vec3      viewStart  = camera.getPosition();
		Vec3      viewVector = new Vec3(camera.getLookVector());
		Vec3      viewEnd    = viewVector.scale(pickRange).add(viewStart);
		Minecraft mc         = Minecraft.getInstance();
		assert mc.cameraEntity != null;
		return mc.cameraEntity.level.clip(new ClipContext(viewStart,
														  viewEnd,
														  wasAiming ? ClipContext.Block.COLLIDER: ClipContext.Block.OUTLINE,
														  ClipContext.Fluid.NONE,
														  mc.cameraEntity));
	}
}
