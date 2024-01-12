package net.leawind.mc.thirdperson.core;


import com.mojang.blaze3d.Blaze3D;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.core.cameraoffset.CameraOffsetMode;
import net.leawind.mc.thirdperson.event.ModKeys;
import net.leawind.mc.thirdperson.mixin.CameraInvoker;
import net.leawind.mc.thirdperson.mixin.LocalPlayerInvoker;
import net.leawind.mc.util.smoothvalue.ExpSmoothDouble;
import net.leawind.mc.util.smoothvalue.ExpSmoothVector2d;
import net.leawind.mc.util.smoothvalue.ExpSmoothVector3d;
import net.leawind.mc.util.vector.Vector2d;
import net.leawind.mc.util.vector.Vector3d;
import net.leawind.mc.util.vector.Vectors;
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

public class CameraAgent {
	@Nullable public static      BlockGetter       level;
	@Nullable public static      Camera            camera;
	@NotNull public static final Camera            fakeCamera                 = new Camera();
	/**
	 * renderTick 中更新
	 */
	public static                boolean           wasAttachedEntityInvisible = false;
	/**
	 * 上一次 render tick 的时间戳
	 */
	public static                double            lastRenderTickTimeStamp    = 0;
	/**
	 * 上次玩家操控转动视角的时间
	 */
	public static                double            lastCameraTurnTimeStamp    = 0;
	@NotNull public static final Vector2d          relativeRotation           = new Vector2d(0);
	/**
	 * 相机偏移量
	 */
	public static final          ExpSmoothVector2d smoothOffsetRatio          = new ExpSmoothVector2d().setSmoothFactorWeight(ModConstants.OFFSET_RATIO_SMOOTH_WEIGHT).set(new Vector2d(0));
	/**
	 * 眼睛的平滑位置
	 */
	public static final          ExpSmoothVector3d smoothEyePosition          = new ExpSmoothVector3d().setSmoothFactorWeight(ModConstants.EYE_POSITIOIN_SMOOTH_WEIGHT);
	/**
	 * 虚相机到平滑眼睛的距离
	 */
	public static final          ExpSmoothDouble   smoothDistanceToEye        = new ExpSmoothDouble().setSmoothFactorWeight(ModConstants.DISTANCE_TO_EYE_SMOOTH_WEIGHT).set(0D);

	/**
	 * 判断：模组功能已启用，且相机和玩家都已经初始化
	 */
	public static boolean isAvailable () {
		Minecraft mc = Minecraft.getInstance();
		if (!Config.get().is_mod_enable) {
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
		if (Config.get().is_mod_enable && !ModReferee.isAdjustingCameraOffset()) {
			y *= 0.15;
			x *= Config.get().lock_camera_pitch_angle ? 0: -0.15;
			if (y != 0 || x != 0) {
				lastCameraTurnTimeStamp = Blaze3D.getTime();
				relativeRotation.set(Mth.clamp(relativeRotation.x + x, -ModConstants.CAMERA_PITCH_DEGREE_LIMIT, ModConstants.CAMERA_PITCH_DEGREE_LIMIT), (relativeRotation.y + y) % 360f);
			}
		}
	}

	/**
	 * 进入第三人称视角时触发
	 */
	public static void onEnterThirdPerson () {
		reset();
		PlayerAgent.reset();
		PlayerAgent.wasAiming       = false;
		ModReferee.isToggleToAiming = false;
		lastRenderTickTimeStamp     = Blaze3D.getTime();
	}

	/**
	 * 重置玩家对象，重置相机的位置、角度等参数
	 */
	public static void reset () {
		Minecraft mc = Minecraft.getInstance();
		camera                      = mc.gameRenderer.getMainCamera();
		PlayerAgent.lastPartialTick = mc.getFrameTime();
		smoothOffsetRatio.setValue(0, 0);
		smoothDistanceToEye.set(Config.get().distanceMonoList.get(0));
		if (mc.cameraEntity != null) {
			relativeRotation.set(-mc.cameraEntity.getViewXRot(PlayerAgent.lastPartialTick), mc.cameraEntity.getViewYRot(PlayerAgent.lastPartialTick) - 180);
		}
	}

	/**
	 * 退出第三人称视角
	 */
	public static void onLeaveThirdPerson () {
		if (Config.get().turn_with_camera_when_enter_first_person) {
			PlayerAgent.turnToCameraRotation(true);
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
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) {
			return;
		}
		// 时间
		double now    = Blaze3D.getTime();
		double period = now - lastRenderTickTimeStamp;
		lastRenderTickTimeStamp = now;
		if (isThirdPerson()) {
			if (!mc.isPaused()) {
				// 平滑更新距离
				updateSmoothVirtualDistance(period);
				// 平滑更新相机偏移量
				updateSmoothOffsetRatio(period);
			}
			// 设置相机朝向和位置
			updateFakeCameraRotationPosition();
			preventThroughWall();
			updateFakeCameraRotationPosition();
			applyCamera();
			wasAttachedEntityInvisible = ModReferee.isAttachedEntityInvisible();
			//			if (wasAttachedEntityInvisible) {
			//				// 假的第一人称，强制将相机放在玩家眼睛处
			//				Vec3 eyePosition = attachedEntity.getEyePosition(partialTick);
			//				((CameraInvoker)fakeCamera).invokeSetPosition(eyePosition);
			//				applyCamera();
			//			}
		}
		PlayerAgent.onRenderTick();
		if (mc.options.getCameraType().isMirrored()) {
			mc.options.setCameraType(CameraType.FIRST_PERSON);
		}
	}

	public static Vector3d getSmoothEyePositionValue () {
		Vector3d  smoothEyePositionValue = smoothEyePosition.get(PlayerAgent.lastPartialTick);
		Minecraft mc                     = Minecraft.getInstance();
		assert mc.cameraEntity != null;
		Vector3d eyePosition      = Vectors.toVector3d(mc.cameraEntity.getEyePosition(PlayerAgent.lastPartialTick));
		double   dist             = smoothEyePositionValue.distance(eyePosition);
		Vector3d sf               = smoothEyePosition.smoothFactor;
		boolean  isHorizontalZero = sf.x * sf.z == 0;
		boolean  isVerticalZero   = sf.y == 0;
		if (isHorizontalZero || isVerticalZero) {
			smoothEyePositionValue = new Vector3d(isHorizontalZero ? eyePosition.x: smoothEyePositionValue.x, isVerticalZero ? eyePosition.y: smoothEyePositionValue.y, isHorizontalZero ? eyePosition.z: smoothEyePositionValue.z);
		}
		return smoothEyePositionValue;
	}

	public static Vector3d getPositionWithoutOffset () {
		return getSmoothEyePositionValue().add(Vectors.directionFromRotationDegree(relativeRotation).mul(smoothDistanceToEye.get()));
	}

	public static void updateSmoothVirtualDistance (double period) {
		boolean          isAdjusting = ModReferee.isAdjustingCameraDistance();
		CameraOffsetMode mode        = Config.get().cameraOffsetScheme.getMode();
		smoothDistanceToEye.setSmoothFactor(isAdjusting ? Config.get().adjusting_distance_smooth_factor: mode.getDistanceSmoothFactor());
		smoothDistanceToEye.setTarget(mode.getMaxDistance()).update(period);
		// 如果是非瞄准模式下，且距离过远则强行放回去
		if (!Config.get().cameraOffsetScheme.isAiming && !isAdjusting) {
			smoothDistanceToEye.set(Math.min(mode.getMaxDistance(), smoothDistanceToEye.get()));
		}
	}

	public static void updateSmoothOffsetRatio (double period) {
		CameraOffsetMode mode = Config.get().cameraOffsetScheme.getMode();
		if (ModKeys.ADJUST_POSITION.isDown()) {
			int i = 1 + 1;
		}
		if (ModReferee.isAdjustingCameraOffset()) {
			smoothOffsetRatio.setSmoothFactor(Config.get().adjusting_camera_offset_smooth_factor);
		} else {
			smoothOffsetRatio.setSmoothFactor(mode.getOffsetSmoothFactor());
		}
		if (Config.get().center_offset_when_flying && ModReferee.isAttachedEntityFallFlying()) {
			smoothOffsetRatio.setTarget(0, 0);
		} else {
			smoothOffsetRatio.setTarget(mode.getOffsetRatio());
		}
		smoothOffsetRatio.update(period);
	}

	public static void updateSmoothEyePosition (double period) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.cameraEntity != null && mc.player != null) {
			CameraOffsetMode mode        = Config.get().cameraOffsetScheme.getMode();
			Vector3d         eyePosition = Vectors.toVector3d(mc.cameraEntity.getEyePosition(PlayerAgent.lastPartialTick));
			// 飞行时使用专用的平滑系数
			if (ModReferee.isAttachedEntityFallFlying()) {
				smoothEyePosition.setSmoothFactor(Config.get().flying_smooth_factor);
			} else {
				smoothEyePosition.setSmoothFactor(mode.getEyeSmoothFactor());
			}
			smoothEyePosition.setTarget(eyePosition).update(period);
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
		double heightHalf = Math.tan(verticalRadianHalf) * ModConstants.NEAR_PLANE_DISTANCE;
		double widthHalf  = aspectRatio * heightHalf;
		//		// 水平视野角度一半(弧度制）
		//		double horizonalRadianHalf = Math.atan(widthHalf / NEAR_PLANE_DISTANCE);
		// 平滑值
		Vector2d smoothOffsetRatioValue     = smoothOffsetRatio.get();
		double   smoothVirtualDistanceValue = smoothDistanceToEye.get();
		// 偏移量
		double upOffset   = smoothOffsetRatioValue.y * smoothVirtualDistanceValue * Math.tan(verticalRadianHalf);
		double leftOffset = smoothOffsetRatioValue.x * smoothVirtualDistanceValue * widthHalf / ModConstants.NEAR_PLANE_DISTANCE;
		// 没有偏移的情况下相机位置
		Vector3d positionWithoutOffset = getPositionWithoutOffset();
		// 应用到假相机
		((CameraInvoker)fakeCamera).invokeSetRotation((float)(relativeRotation.y + 180), (float)-relativeRotation.x);
		((CameraInvoker)fakeCamera).invokeSetPosition(Vectors.toVec3(positionWithoutOffset));
		((CameraInvoker)fakeCamera).invokeMove(0, upOffset, leftOffset);
	}

	/**
	 * 为防止穿墙，重新计算 smoothVirtualDistance 的值
	 */
	public static void preventThroughWall () {
		// 防止穿墙
		Vec3   cameraPosition    = fakeCamera.getPosition();
		Vec3   smoothEyePosition = Vectors.toVec3(getSmoothEyePositionValue());
		Vec3   smoothEyeToCamera = smoothEyePosition.vectorTo(cameraPosition);
		double initDistance      = smoothEyeToCamera.length();
		double minDistance       = initDistance;
		assert level != null;
		for (int i = 0; i < 8; ++i) {
			double offsetX = (i & 1) * 2 - 1;
			double offsetY = (i >> 1 & 1) * 2 - 1;
			double offsetZ = (i >> 2 & 1) * 2 - 1;
			offsetX *= ModConstants.CAMERA_THROUGH_WALL_DETECTION;
			offsetY *= ModConstants.CAMERA_THROUGH_WALL_DETECTION;
			offsetZ *= ModConstants.CAMERA_THROUGH_WALL_DETECTION;
			Vec3      pickStart = smoothEyePosition.add(offsetX, offsetY, offsetZ);
			HitResult hitresult = level.clip(new ClipContext(pickStart, pickStart.add(smoothEyeToCamera), ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, Minecraft.getInstance().cameraEntity));
			if (hitresult.getType() != HitResult.Type.MISS) {
				minDistance = Math.min(minDistance, hitresult.getLocation().distanceTo(pickStart));
			}
		}
		smoothDistanceToEye.setValue(smoothDistanceToEye.get() * minDistance / initDistance);
	}

	/**
	 * 将假相机的朝向和位置应用到真相机上
	 */
	private static void applyCamera () {
		assert camera != null;
		((CameraInvoker)camera).invokeSetRotation(fakeCamera.getYRot(), fakeCamera.getXRot());
		((CameraInvoker)camera).invokeSetPosition(fakeCamera.getPosition());
	}

	/**
	 * 根据相对角度计算相机朝向
	 */
	public static Vector2d calculateRotation () {
		return new Vector2d(relativeRotation.y + 180, -relativeRotation.x);
	}

	/**
	 * 获取相机视线落点坐标
	 */
	public static @Nullable Vector3d getPickPosition () {
		return getPickPosition(smoothDistanceToEye.get() + Config.get().camera_ray_trace_length);
	}

	/**
	 * 获取相机视线落点坐标
	 *
	 * @param pickRange 最大探测距离
	 */
	public static @Nullable Vector3d getPickPosition (double pickRange) {
		HitResult hitResult = pick(pickRange);
		return hitResult.getType() == HitResult.Type.MISS ? null: Vectors.toVector3d(hitResult.getLocation());
	}

	public static @NotNull HitResult pick () {
		return pick(smoothDistanceToEye.get() + Config.get().camera_ray_trace_length);
	}

	public static @NotNull HitResult pick (double pickRange) {
		assert camera != null;
		EntityHitResult ehr = pickEntity(pickRange);
		BlockHitResult  bhr = pickBlock(pickRange);
		return ehr == null ? bhr: bhr.getLocation().distanceTo(camera.getPosition()) < ehr.getLocation().distanceTo(camera.getPosition()) ? bhr: ehr;
	}

	private static @Nullable EntityHitResult pickEntity (double pickRange) {
		Entity cameraEntity = Minecraft.getInstance().cameraEntity;
		if (camera == null || cameraEntity == null) {
			return null;
		}
		Vec3 viewStart  = camera.getPosition();
		Vec3 viewVector = new Vec3(camera.getLookVector());
		Vec3 viewEnd    = viewVector.scale(pickRange).add(viewStart);
		AABB aabb       = cameraEntity.getBoundingBox().expandTowards(viewVector.scale(pickRange)).inflate(1.0D, 1.0D, 1.0D);
		return ProjectileUtil.getEntityHitResult(cameraEntity, viewStart, viewEnd, aabb, (Entity target) -> !target.isSpectator() && target.isPickable(), pickRange);
	}

	/**
	 * pick 方块
	 * <p>
	 * 瞄准时忽略草
	 */
	private static @NotNull BlockHitResult pickBlock (double pickRange) {
		assert (camera != null);
		Vec3   viewStart    = camera.getPosition();
		Vec3   viewVector   = new Vec3(camera.getLookVector());
		Vec3   viewEnd      = viewVector.scale(pickRange).add(viewStart);
		Entity cameraEntity = Minecraft.getInstance().cameraEntity;
		assert cameraEntity != null;
		return cameraEntity.level.clip(new ClipContext(viewStart, viewEnd, PlayerAgent.wasAiming ? ClipContext.Block.COLLIDER: ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, cameraEntity));
	}
}
