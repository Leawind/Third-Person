package net.leawind.mc.thirdpersonperspective.core;


import com.mojang.blaze3d.Blaze3D;
import com.mojang.logging.LogUtils;
import net.leawind.mc.thirdpersonperspective.config.Config;
import net.leawind.mc.thirdpersonperspective.mixin.CameraInvoker;
import net.leawind.mc.thirdpersonperspective.userprofile.UserProfile;
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

public class CameraAgent {
	public static final Logger          LOGGER                = LogUtils.getLogger();
	/**
	 * 成像平面到相机的距离
	 */
	public static final double          nearPlaneDistance     = 0.05000000074505806;
	public static       BlockGetter     level;
	public static       Camera          camera;
	public static       Camera          fakeCamera            = new Camera();
	public static       LocalPlayer     player;
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

	public static boolean isThirdPerson () {
		return !Minecraft.getInstance().options.getCameraType().isFirstPerson();
	}

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

	public static void reset () {//TODO
		Minecraft mc = Minecraft.getInstance();
		player = mc.player;
		assert player != null;
		camera = mc.gameRenderer.getMainCamera();
		smoothOffsetRatio.setValue(0, 0);
		smoothVirtualDistance.setValue(0);
		relativeRotation = new Vec2(-player.getXRot(), player.getYRot() - 180);
		LOGGER.info("Reset CameraAgent");
	}

	/**
	 * 鼠标移动导致的相机旋转
	 *
	 * @param turnY 偏航角变化量
	 * @param turnX 俯仰角变化量
	 */
	public static void onCameraTurn (double turnY, double turnX) {
		if (Options.isAdjustingCameraOffset()) {
			CameraOffsetProfile profile = UserProfile.getCameraOffsetProfile();
		} else {
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
	public static void onEnterThirdPerson (float lerpK) {
		reset();
		PlayerAgent.reset();
		isThirdPerson            = true;
		isAiming                 = false;
		Options.isToggleToAiming = false;
		lastTickTime             = Blaze3D.getTime();
		LOGGER.info("Enter third person, lerpK={}", lerpK);
	}

	/**
	 * 退出第三人称视角
	 */
	public static void onLeaveThirdPerson (float lerpK) {
		isThirdPerson = false;
		PlayerAgent.turnToCameraHitResult(1);
		LOGGER.info("Leave third person, lerpK={}", lerpK);
	}

	/**
	 * 计算并更新相机的朝向和坐标
	 *
	 * @param level  维度
	 * @param entity 实体
	 */
	@PerformanceSensitive
	public static void onRenderTick (BlockGetter level, Entity entity, boolean isMirrored, float lerpK) {
		CameraAgent.level = level;
		player            = (LocalPlayer)entity;
		isAiming          = PlayerAgent.isAiming();
		if (Config.is_only_one_third_person_mode) {
			Minecraft mc = Minecraft.getInstance();
			if (mc.options.getCameraType().isMirrored()) {
				mc.options.setCameraType(CameraType.FIRST_PERSON);
			}
		}
		// 时间
		double now           = Blaze3D.getTime();
		double sinceLastTurn = now - lastTurnTime;
		double sinceLastTick = now - lastTickTime;
		lastTickTime = now;
		CameraOffsetProfile profile = UserProfile.getCameraOffsetProfile();
		profile.setAiming(isAiming);
		if (isThirdPerson) {
			boolean isAdjusting = Options.isAdjustingCameraOffset();
			// 平滑更新距离
			smoothVirtualDistance.setSmoothFactor(isAdjusting ? 1e-5: profile.getMode().distanceSmoothFactor);
			smoothVirtualDistance.setTarget(profile.getMode().maxDistance).update(sinceLastTick);
			// 如果是非瞄准模式下，且距离过远则强行放回去
			if (!profile.isAiming && !Options.isAdjustingCameraOffset()) {
				smoothVirtualDistance.setValue(Math.min(profile.getMode().maxDistance, smoothVirtualDistance.get()));
			}
			// 平滑更新相机偏移量
			smoothOffsetRatio.setSmoothFactor(isAdjusting ? new Vec2(1e-7F, 1e-7F): profile.getMode().offsetSmoothFactor);
			smoothOffsetRatio.setTarget(profile.getMode().getOffsetRatio(smoothVirtualDistance.get()));
			smoothOffsetRatio.update(sinceLastTick);
			// 设置相机朝向和位置
			updateFakeCameraRotationPosition();
			preventThroughWall();
			updateFakeCameraRotationPosition();
			applyCamera();
		}
		PlayerAgent.onRenderTick(lerpK, sinceLastTick);
		lastPosition = camera.getPosition();
		lastRotation = new Vec2(camera.getXRot(), camera.getYRot());
	}

	/**
	 * 根据角度、距离、偏移量计算相机实际位置
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
	 * 将假相机的朝向和位置应用到真相机上
	 */
	private static void applyCamera () {
		// 应用到真相机
		((CameraInvoker)camera).invokeSetRotation(fakeCamera.getYRot(), fakeCamera.getXRot());
		((CameraInvoker)camera).invokeSetPosition(fakeCamera.getPosition());
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
															 player));
			if (hitresult.getType() != HitResult.Type.MISS) {
				minDistance = Math.min(minDistance, hitresult.getLocation().distanceTo(pickStart));
			}
		}
		smoothVirtualDistance.setValue(smoothVirtualDistance.get() * minDistance / initDistance);
		smoothVirtualDistance.setTarget(smoothVirtualDistance.get() * minDistance / initDistance);
	}

	public static Vec2 calculateRotation () {
		return new Vec2(relativeRotation.y + 180, -relativeRotation.x);
	}

	/**
	 * TODO
	 * <p>
	 * 世界坐标点 pos 在画面中的坐标
	 * <p>
	 * x, y \in [0,1]
	 *
	 * @param pos (x,y)
	 */
	public static Vec2 toScreenCoord (Vec3 pos) {
		Minecraft mc                 = Minecraft.getInstance();
		double    aspectRatio        = (double)mc.getWindow().getWidth() / mc.getWindow().getHeight();
		double    verticalRadianHalf = Math.toRadians(mc.options.fov().get()) / 2;
		double    height             = Math.tan(verticalRadianHalf) * nearPlaneDistance * 2;
		double    width              = aspectRatio * height * 2;
		Vec3      toTarget           = fakeCamera.getPosition().vectorTo(pos);
		double    targetPlaneDist    = fakeCamera.getLookVector().dot(toTarget.toVector3f());
		Vec2 sc = new Vec2((float)(0.5 - fakeCamera.getLeftVector().dot(toTarget.toVector3f()) /
										 (targetPlaneDist / nearPlaneDistance * width)),
						   (float)(0.5 - fakeCamera.getUpVector().dot(toTarget.toVector3f()) /
										 (targetPlaneDist / nearPlaneDistance * height)));
		System.out.printf("\r sc=(%.5f, %.5f)", sc.x, sc.y);
		return sc;
		//		Vec2 direction = Vectors.rotationRadianFromDirection(toTarget).add(calculateRotation().negated());
		//		return new Vec2((float)(Math.tan(direction.y) * nearPlaneDistance / width + 0.5),
		//						(float)(Math.tan(direction.x) * nearPlaneDistance / height + 0.5));
	}

	public static Vec3 getVirtualPosition () {
		return PlayerAgent.smoothEyePosition.get().add(Vec3.directionFromRotation(relativeRotation)
														   .scale(smoothVirtualDistance.get()));
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
		Vec3 viewStart  = camera.getPosition();
		Vec3 viewVector = new Vec3(camera.getLookVector());
		Vec3 viewEnd    = viewVector.scale(pickRange).add(viewStart);
		AABB aabb       = player.getBoundingBox().expandTowards(viewVector.scale(pickRange)).inflate(1.0D, 1.0D, 1.0D);
		return ProjectileUtil.getEntityHitResult(player,
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
		return player.level().clip(new ClipContext(viewStart,
												   viewEnd,
												   ClipContext.Block.OUTLINE,
												   ClipContext.Fluid.NONE,
												   player));
	}
}
