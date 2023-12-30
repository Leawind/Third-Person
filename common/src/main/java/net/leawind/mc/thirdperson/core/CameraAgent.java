package net.leawind.mc.thirdperson.core;


import com.mojang.blaze3d.Blaze3D;
import net.leawind.mc.thirdperson.ThirdPersonMod;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetScheme;
import net.leawind.mc.thirdperson.mixin.CameraInvoker;
import net.leawind.mc.thirdperson.mixin.LocalPlayerInvoker;
import net.leawind.mc.util.smoothvalue.ExpSmoothDouble;
import net.leawind.mc.util.smoothvalue.ExpSmoothVec2;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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
	public static final Logger          LOGGER                = LoggerFactory.getLogger(ThirdPersonMod.MOD_ID);
	/**
	 * 成像平面到相机的距离，这是一个固定值，硬编码在Minecraft源码中。
	 * <p>
	 * 取自 {@link net.minecraft.client.Camera#getNearPlane()}
	 */
	public static final double          nearPlaneDistance     = 0.05;
	public static       BlockGetter     level;
	public static       Camera          camera;
	public static       Camera          fakeCamera            = new Camera();
	/**
	 * 当前玩家实体
	 */
	public static       LocalPlayer     playerEntity;
	/**
	 * 当前相机附着的实体，当以旁观者模式附着其他实体时，此实体不同于当前玩家实体
	 */
	public static       Entity          attachedEntity;
	/**
	 * 当前是否为第三人称视角
	 */
	public static       boolean         isThirdPerson         = false;
	/**
	 * 相机偏移量
	 */
	public static       ExpSmoothVec2   smoothOffsetRatio     = new ExpSmoothVec2().setValue(0, 0);
	public static       double          lastTickTime          = 0;
	public static       boolean         isAiming              = false;
	/**
	 * 上次转动视角的时间
	 */
	public static       double          lastTurnTime          = 0;
	/**
	 * 虚相机到平滑眼睛的距离
	 */
	public static       ExpSmoothDouble smoothVirtualDistance = new ExpSmoothDouble().setValue(0).setTarget(0);
	public static       Vec2            relativeRotation      = Vec2.ZERO;
	/**
	 * 相机上次的朝向和位置
	 */
	public static       Vec3            lastPosition          = Vec3.ZERO;
	public static       Vec2            lastRotation          = Vec2.ZERO;

	@SuppressWarnings("unused")
	public static boolean isThirdPerson () {
		return !Minecraft.getInstance().options.getCameraType().isFirstPerson();
	}

	/**
	 * 判断：模组功能已启用，且相机和玩家都已经初始化
	 */
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

	/**
	 * 当前是否在控制玩家
	 * <p>
	 * 如果当前玩家处于旁观者模式，附着在其他实体上，则返回false
	 */
	public static boolean isControlledCamera () {
		return ((LocalPlayerInvoker)playerEntity).invokeIsControlledCamera();
	}

	/**
	 * 鼠标移动导致的相机旋转
	 *
	 * @param turnY 偏航角变化量
	 * @param turnX 俯仰角变化量
	 */
	public static void onCameraTurn (double turnY, double turnX) {
		if (Config.is_mod_enable && !ModOptions.isAdjustingCameraOffset()) {
			turnY *= 0.15;
			turnX *= -0.15;
			if (turnY != 0 || turnX != 0) {
				lastTurnTime     = Blaze3D.getTime();
				relativeRotation = new Vec2((float)Mth.clamp(relativeRotation.x + turnX, -89.8, 89.8),
											(float)(relativeRotation.y + turnY) % 360f);
			}
		}
	}

	/**
	 * 进入第三人称视角时触发
	 */
	public static void onEnterThirdPerson (float partialTick) {
		reset();
		PlayerAgent.reset();
		isThirdPerson            = true;
		isAiming                    = false;
		ModOptions.isToggleToAiming = false;
		lastTickTime                = Blaze3D.getTime();
		LOGGER.info("Enter third person, partialTick={}", partialTick);
	}

	/**
	 * 重置玩家对象，重置相机的位置、角度等参数
	 */
	public static void reset () {
		Minecraft mc = Minecraft.getInstance();
		assert mc.player != null;
		attachedEntity = playerEntity = mc.player;
		camera         = mc.gameRenderer.getMainCamera();
		smoothOffsetRatio.setValue(0, 0);
		smoothVirtualDistance.setValue(0);
		relativeRotation = new Vec2(-attachedEntity.getXRot(), attachedEntity.getYRot() - 180);
		LOGGER.info("Reset CameraAgent");
	}

	/**
	 * 退出第三人称视角
	 */
	public static void onLeaveThirdPerson (float partialTick) {
		isThirdPerson = false;
		PlayerAgent.turnToCameraHitResult(1);
		LOGGER.info("Leave third person, partialTick={}", partialTick);
	}

	/**
	 * 计算并更新相机的朝向和坐标
	 *
	 * @param level          维度
	 * @param attachedEntity 附着的实体
	 */
	@PerformanceSensitive
	public static void onRenderTick (BlockGetter level, Entity attachedEntity, boolean isMirrored, float partialTick) {
		CameraAgent.attachedEntity = attachedEntity;
		CameraAgent.level          = level;
		isAiming                   = PlayerAgent.isAiming();
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.getCameraType().isMirrored()) {
			mc.options.setCameraType(CameraType.FIRST_PERSON);
		}
		// 时间
		double now           = Blaze3D.getTime();
		double sinceLastTick = now - lastTickTime;
		lastTickTime = now;
		CameraOffsetScheme scheme = Config.cameraOffsetScheme;
		scheme.setAiming(isAiming);
		if (isThirdPerson) {
			boolean isAdjusting = ModOptions.isAdjustingCameraOffset();
			// 平滑更新距离
			smoothVirtualDistance.setSmoothFactor(isAdjusting ? 1e-5: scheme.getMode().getDistanceSmoothFactor());
			smoothVirtualDistance.setTarget(scheme.getMode().getMaxDistance()).update(sinceLastTick);
			// 如果是非瞄准模式下，且距离过远则强行放回去
			if (!scheme.isAiming && !ModOptions.isAdjustingCameraOffset()) {
				smoothVirtualDistance.setValue(Math.min(scheme.getMode().getMaxDistance(), smoothVirtualDistance.get()));
			}
			// 平滑更新相机偏移量
			smoothOffsetRatio.setSmoothFactor(isAdjusting ? new Vec2(1e-7F, 1e-7F): scheme.getMode().getOffsetSmoothFactor());
			smoothOffsetRatio.setTarget(scheme.getMode().getOffsetRatio());
			smoothOffsetRatio.update(sinceLastTick);
			// 设置相机朝向和位置
			updateFakeCameraRotationPosition();
			preventThroughWall();
			updateFakeCameraRotationPosition();
			applyCamera();
		}
		PlayerAgent.onRenderTick(partialTick, sinceLastTick);
		lastPosition = camera.getPosition();
		lastRotation = new Vec2(camera.getXRot(), camera.getYRot());
	}

	/**
	 * 根据角度、距离、偏移量计算相机实际朝向和位置
	 */
	private static void updateFakeCameraRotationPosition () {
		Minecraft mc = Minecraft.getInstance();
		// 宽高比
		double aspectRatio = (double)mc.getWindow().getWidth() / mc.getWindow().getHeight();
		// 垂直视野角度一半(弧度制）
		double verticalRadianHalf = Math.toRadians(mc.options.fov().get()) / 2;
		// 成像平面宽高
		double heightHalf = Math.tan(verticalRadianHalf) * nearPlaneDistance;
		double widthHalf  = aspectRatio * heightHalf;
		// 水平视野角度一半(弧度制）
		double horizonalRadianHalf = Math.atan(widthHalf / nearPlaneDistance);
		// 偏移
		double leftOffset = smoothOffsetRatio.get().x * smoothVirtualDistance.get() * widthHalf / nearPlaneDistance;
		double upOffset   = smoothOffsetRatio.get().y * smoothVirtualDistance.get() * Math.tan(verticalRadianHalf);
		// 没有偏移的情况下相机位置
		Vec3 positionWithoutOffset = getVirtualPosition();
		// 应用到假相机
		((CameraInvoker)fakeCamera).invokeSetRotation(relativeRotation.y + 180, -relativeRotation.x);
		((CameraInvoker)fakeCamera).invokeSetPosition(positionWithoutOffset);
		((CameraInvoker)fakeCamera).invokeMove(0, upOffset, leftOffset);
	}

	/**
	 * 为防止穿墙，重新计算 smoothVirtualDistance 的值
	 */
	public static void preventThroughWall () {
		final double offset = 0.18;
		// 防止穿墙
		Vec3   cameraPosition = fakeCamera.getPosition();
		Vec3   eyePosition    = PlayerAgent.smoothEyePosition.get();
		Vec3   eyeToCamera    = eyePosition.vectorTo(cameraPosition);
		double initDistance   = eyeToCamera.length();
		double minDistance    = initDistance;
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
															 attachedEntity));
			if (hitresult.getType() != HitResult.Type.MISS) {
				minDistance = Math.min(minDistance, hitresult.getLocation().distanceTo(pickStart));
			}
		}
		smoothVirtualDistance.setValue(smoothVirtualDistance.get() * minDistance / initDistance);
		smoothVirtualDistance.setTarget(smoothVirtualDistance.get() * minDistance / initDistance);
	}

	/**
	 * 将假相机的朝向和位置应用到真相机上
	 */
	private static void applyCamera () {
		// 应用到真相机
		((CameraInvoker)camera).invokeSetRotation(fakeCamera.getYRot(), fakeCamera.getXRot());
		((CameraInvoker)camera).invokeSetPosition(fakeCamera.getPosition());
	}

	public static Vec3 getVirtualPosition () {
		return PlayerAgent.smoothEyePosition.get().add(Vec3.directionFromRotation(relativeRotation)
														   .scale(smoothVirtualDistance.get()));
	}

	public static Vec2 calculateRotation () {
		return new Vec2(relativeRotation.y + 180, -relativeRotation.x);
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

	public static @NotNull HitResult pick (double pickRange) {
		EntityHitResult ehr = pickEntity(pickRange);
		return ehr == null ? pickBlock(pickRange): ehr;
	}

	private static @Nullable EntityHitResult pickEntity (double pickRange) {
		Vec3 viewStart  = camera.getPosition();
		Vec3 viewVector = new Vec3(camera.getLookVector());
		Vec3 viewEnd    = viewVector.scale(pickRange).add(viewStart);
		AABB aabb       = attachedEntity.getBoundingBox().expandTowards(viewVector.scale(pickRange)).inflate(1.0D, 1.0D, 1.0D);
		return ProjectileUtil.getEntityHitResult(attachedEntity,
												 viewStart,
												 viewEnd,
												 aabb,
												 (Entity target) -> !target.isSpectator() && target.isPickable(),
												 pickRange);
	}

	private static @NotNull BlockHitResult pickBlock (double pickRange) {
		Vec3 viewStart  = camera.getPosition();
		Vec3 viewVector = new Vec3(camera.getLookVector());
		Vec3 viewEnd    = viewVector.scale(pickRange).add(viewStart);
		return attachedEntity.level.clip(new ClipContext(viewStart,
														 viewEnd,
														 ClipContext.Block.OUTLINE,
														 ClipContext.Fluid.NONE,
														 attachedEntity));
	}

	public static @NotNull HitResult pick () {
		return pick(smoothVirtualDistance.get() + Config.camera_ray_trace_length);
	}
}
