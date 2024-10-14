package com.github.leawind.thirdperson.core;


import com.github.leawind.api.base.GameStatus;
import com.github.leawind.api.client.event.ThirdPersonCameraSetupEvent;
import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonConstants;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.github.leawind.thirdperson.config.AbstractConfig;
import com.github.leawind.thirdperson.mixin.CameraInvoker;
import com.github.leawind.thirdperson.mixin.ClientLevelInvoker;
import com.github.leawind.thirdperson.mixin.GameRendererInvoker;
import com.github.leawind.util.FiniteChecker;
import com.github.leawind.util.Zone;
import com.github.leawind.util.annotation.VersionSensitive;
import com.github.leawind.util.math.LMath;
import com.github.leawind.util.math.smoothvalue.ExpSmoothDouble;
import com.github.leawind.util.math.smoothvalue.ExpSmoothVector2d;
import com.github.leawind.util.math.smoothvalue.ExpSmoothVector3d;
import com.github.leawind.util.math.vector.Vector2d;
import com.github.leawind.util.math.vector.Vector3d;
import com.google.common.collect.Lists;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class CameraAgent {
	public final           FiniteChecker     FINITE_CHECKER   = new FiniteChecker(err -> ThirdPerson.LOGGER.error(err.toString()));
	private final @NotNull Minecraft         minecraft;
	private final @NotNull ExpSmoothVector3d smoothRotateCenter;
	private final @NotNull Camera            tempCamera       = new Camera();
	private final @NotNull Vector2d          relativeRotation = Vector2d.of(0);
	/**
	 * 相机偏移量
	 */
	private final @NotNull ExpSmoothVector2d smoothOffsetRatio;
	/**
	 * 虚相机到平滑眼睛外壳的距离系数
	 * <p>
	 * 外壳的半径是 {@link EntityAgent#getBodyRadius()}
	 */
	private final @NotNull ExpSmoothDouble   smoothDistance;
	/**
	 * 平滑变化的视野大小乘数
	 */
	private final @NotNull ExpSmoothDouble   smoothFovDivisor;
	/**
	 * 在 {@link CameraAgent#onRenderTickStart} 中更新
	 */
	private @NotNull       HitResult         hitResult        = BlockHitResult.miss(Vec3.ZERO, Direction.EAST, BlockPos.ZERO);

	public CameraAgent (@NotNull Minecraft minecraft) {
		this.minecraft     = minecraft;
		smoothRotateCenter = new ExpSmoothVector3d();
		smoothOffsetRatio  = new ExpSmoothVector2d();
		smoothDistance     = new ExpSmoothDouble();
		smoothFovDivisor   = new ExpSmoothDouble();
		smoothFovDivisor.set(1D);
	}

	/**
	 * 重置各种属性
	 */
	public void reset () {
		ThirdPerson.LOGGER.debug("Reset CameraAgent");
		smoothOffsetRatio.setValue(0, 0);
		smoothDistance.set(0D);
		smoothFovDivisor.set(1D);
		if (ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
			smoothRotateCenter.set(getRotateCenterTarget(1));
			var entity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
			relativeRotation.set(-entity.getXRot(), entity.getYRot() - 180);
		}
	}

	public void checkGameStatus () {
		if (minecraft.options.getCameraType() == CameraType.FIRST_PERSON) {
			GameStatus.isPerspectiveInverted = smoothDistance.get() > ThirdPersonConstants.FIRST_PERSON_TRANSITION_END_THRESHOLD;
		}
	}

	/**
	 * 渲染前
	 */
	public void onRenderTickStart (double now, double period, float partialTick) {
		if (!minecraft.isPaused() && ThirdPersonStatus.isRenderingInThirdPerson()) {
			// mc 没有暂停，且正在以第三人称渲染
			// 更新探测结果
			hitResult = pick(getPickRange());
			// 平滑更新距离
			updateSmoothVirtualDistance(period);
			// 平滑更新相机偏移量
			updateSmoothOffsetRatio(period);
			// 平滑更新 FOV 乘数
			updateSmoothFovMultiplier(period);
			//
			if (ThirdPersonStatus.shouldCameraTurnWithEntity()) {
				// 将相机朝向与相机实体朝向同步
				var rot = ThirdPerson.ENTITY_AGENT.getRawRotation(partialTick);
				FINITE_CHECKER.checkOnce(rot.x(), rot.y());
				relativeRotation.set(-rot.x(), rot.y() - 180);
			}
		}
	}

	public double getSmoothFovDivisor () {
		return smoothFovDivisor.get();
	}

	/**
	 * 渲染过程中放置相机
	 */
	public void onCameraSetup (@NotNull ThirdPersonCameraSetupEvent event) {
		updateTempCameraRotationPosition(event.partialTick);
		event.setPosition(tempCamera.getPosition());
		float yRot = tempCamera.getYRot();
		float xRot = tempCamera.getXRot();
		FINITE_CHECKER.checkOnce(xRot, yRot);
		event.setRotation(xRot, yRot);
	}

	/**
	 * 不平滑的旋转中心
	 */
	public Vector3d getRotateCenterTarget (float partialTick) {
		var config      = ThirdPerson.getConfig();
		var entity      = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
		var eyePosition = entity.getEyePosition(partialTick);
		return LMath.toVector3d(eyePosition.with(Direction.Axis.Y, eyePosition.y + config.rotate_center_height_offset));
	}

	/**
	 * 防止旋转中心穿墙
	 */
	public boolean limitRotateCenter (Vector3d rotateCenter, float partialTick) {
		var entity            = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
		var smoothEyePosition = LMath.toVec3(smoothRotateCenter.get(partialTick));
		var eyePosition       = new Vec3(smoothEyePosition.x, entity.getEyePosition(partialTick).y, smoothEyePosition.z);
		var limit             = Zone.ofAuto(eyePosition.y, rotateCenter.y()).expendRadius(ThirdPersonConstants.ROTATE_CENTER_RADIUS);
		{
			BlockHitResult hitResult;
			Vec3           pickEnd;
			pickEnd   = new Vec3(eyePosition.x, limit.max, eyePosition.z);
			hitResult = entity.level().clip(new ClipContext(eyePosition, pickEnd, ThirdPersonConstants.CAMERA_OBSTACLE_BLOCK_SHAPE_GETTER, ClipContext.Fluid.NONE, entity));
			if (hitResult.getType() == HitResult.Type.BLOCK) {
				limit = limit.withMax(hitResult.getLocation().y);
			}
			pickEnd   = new Vec3(eyePosition.x, limit.min, eyePosition.z);
			hitResult = entity.level().clip(new ClipContext(eyePosition, pickEnd, ThirdPersonConstants.CAMERA_OBSTACLE_BLOCK_SHAPE_GETTER, ClipContext.Fluid.NONE, entity));
			if (hitResult.getType() == HitResult.Type.BLOCK) {
				limit = limit.withMin(hitResult.getLocation().y);
			}
			for (int i = 0; i < 4; i++) {
				final double offsetX = 0.3 * ((i & 1) * 2 - 1);
				final double offsetZ = 0.3 * ((i >> 1 & 1) * 2 - 1);
				pickEnd   = new Vec3(eyePosition.x + offsetX, limit.max, eyePosition.z + offsetZ);
				hitResult = entity.level().clip(new ClipContext(eyePosition, pickEnd, ThirdPersonConstants.CAMERA_OBSTACLE_BLOCK_SHAPE_GETTER, ClipContext.Fluid.NONE, entity));
				if (hitResult.getType() == HitResult.Type.BLOCK) {
					limit = limit.withMax(hitResult.getLocation().y);
				}
				pickEnd   = new Vec3(eyePosition.x + offsetX, limit.min, eyePosition.z + offsetZ);
				hitResult = entity.level().clip(new ClipContext(eyePosition, pickEnd, ThirdPersonConstants.CAMERA_OBSTACLE_BLOCK_SHAPE_GETTER, ClipContext.Fluid.NONE, entity));
				if (hitResult.getType() == HitResult.Type.BLOCK) {
					limit = limit.withMin(hitResult.getLocation().y);
				}
			}
		}
		limit = limit.squeezeSafely(ThirdPersonConstants.ROTATE_CENTER_RADIUS);
		double  newY   = limit.nearest(rotateCenter.y());
		boolean result = newY != rotateCenter.y();
		rotateCenter.y(newY);
		return result;
	}

	/**
	 * 获取平滑的相机旋转中心
	 */
	public @NotNull Vector3d getRotateCenterFinally (float partialTick) {
		var     rotateCenter     = smoothRotateCenter.get(partialTick);
		var     smoothFactor     = smoothRotateCenter.smoothFactor;
		boolean isHorizontalZero = smoothFactor.x() * smoothFactor.z() == 0;
		boolean isVerticalZero   = smoothFactor.y() == 0;
		if (isHorizontalZero || isVerticalZero) {
			var rotateCenterTarget = getRotateCenterTarget(partialTick);
			rotateCenter = Vector3d.of(isHorizontalZero ? rotateCenterTarget.x(): rotateCenter.x(),//
									   isVerticalZero ? rotateCenterTarget.y(): rotateCenter.y(),//
									   isHorizontalZero ? rotateCenterTarget.z(): rotateCenter.z());
		}
		if (limitRotateCenter(rotateCenter, partialTick)) {
			smoothRotateCenter.getRawTarget().y(rotateCenter.y());
		}
		return rotateCenter;
	}

	public void onClientTickStart () {
		var config = ThirdPerson.getConfig();
		{
			final Vector3d halflife;
			if (minecraft.options.getCameraType() == CameraType.FIRST_PERSON) {
				halflife = Vector3d.of(0);
			} else if (ThirdPerson.ENTITY_AGENT.isFallFlying()) {
				halflife = Vector3d.of(config.flying_smooth_halflife);
			} else {
				halflife = config.getCameraOffsetScheme().getMode().getEyeSmoothHalflife();
			}
			final double dist = getRotateCenterFinally(1).distance(ThirdPerson.CAMERA_AGENT.getRawCameraPosition());
			halflife.mul(Math.pow(dist, 0.5) * ThirdPersonConstants.EYE_HALFLIFE_MULTIPLIER);
			smoothRotateCenter.setHalflife(halflife);
		}
		smoothRotateCenter.setTarget(getRotateCenterTarget(1));
		smoothRotateCenter.update(ThirdPersonConstants.VANILLA_CLIENT_TICK_TIME);
	}

	/**
	 * 获取原版相机对象
	 */
	public @NotNull Camera getRawCamera () {
		return Objects.requireNonNull(Minecraft.getInstance().gameRenderer.getMainCamera());
	}

	/**
	 * 获取原始相机位置
	 */
	public @NotNull Vector3d getRawCameraPosition () {
		return LMath.toVector3d(getRawCamera().getPosition());
	}

	/**
	 * 第三人称相机朝向
	 */
	public @NotNull Vector2d getRotation () {
		return Vector2d.of(-relativeRotation.x(), relativeRotation.y() + 180);
	}

	/**
	 * 玩家控制的相机旋转
	 *
	 * @param dYRot 方向角变化量
	 * @param dXRot 俯仰角变化量
	 */
	public void turnCamera (double dYRot, double dXRot) {
		FINITE_CHECKER.checkOnce(dYRot, dXRot);
		var config = ThirdPerson.getConfig();
		if (config.is_mod_enabled && !ThirdPersonStatus.isAdjustingCameraOffset()) {
			if (dYRot != 0 || dXRot != 0) {
				double yRot = getRelativeRotation().y() + dYRot;
				yRot %= 360f;
				double xRot;
				if (config.lock_camera_pitch_angle) {
					xRot = 0;
				} else {
					xRot = getRelativeRotation().x() - dXRot;
					xRot = LMath.clamp(xRot, -ThirdPersonConstants.CAMERA_PITCH_DEGREE_LIMIT, ThirdPersonConstants.CAMERA_PITCH_DEGREE_LIMIT);
				}
				relativeRotation.set(xRot, yRot);
			}
		}
	}

	/**
	 * 获取相对旋转角度
	 */
	public @NotNull Vector2d getRelativeRotation () {
		return relativeRotation;
	}

	/**
	 * render tick 开始时，相机的 hitResult
	 */
	public @NotNull HitResult getHitResult () {
		return hitResult;
	}

	public double getPickRange () {
		return ThirdPerson.ENTITY_AGENT.getBodyRadius() + smoothDistance.get() + ThirdPerson.getConfig().camera_ray_trace_length;
	}

	/**
	 * 获取pick结果坐标
	 * <p>
	 * 使用默认距离
	 */
	public @Nullable Vector3d getPickPosition () {
		return getPickPosition(getPickRange());
	}

	/**
	 * 获取相机视线落点坐标
	 *
	 * @param pickRange 最大探测距离
	 */
	public @Nullable Vector3d getPickPosition (double pickRange) {
		var hitResult = pick(pickRange);
		return hitResult.getType() == HitResult.Type.MISS ? null: LMath.toVector3d(hitResult.getLocation());
	}

	/**
	 * 从相机出发探测所选方块或实体。
	 * <p>
	 * 当探测不到时，返回的是{@link HitResult.Type#MISS}类型。坐标将为探测终点
	 *
	 * @param pickRange 探测距离限制
	 */
	@VersionSensitive
	public @NotNull HitResult pick (double pickRange) {
		var cameraPos       = getRawCamera().getPosition();
		var entityHitResult = pickEntity(pickRange);
		var blockHitResult  = pickBlock(pickRange);
		if (entityHitResult != null) {
			double blockDistance  = blockHitResult.getLocation().distanceTo(cameraPos);
			double entityDistance = hitResult.getLocation().distanceTo(cameraPos);
			if (blockDistance < entityDistance) {
				return entityHitResult;
			}
		}
		return blockHitResult;
	}

	/**
	 * 根据相机的视线确定所选实体
	 * <p>
	 * 如果探测不到就返回空值
	 *
	 * @param pickRange 探测距离
	 */
	@VersionSensitive
	public @Nullable EntityHitResult pickEntity (double pickRange) {
		if (!ThirdPerson.ENTITY_AGENT.isCameraEntityExist()) {
			return null;
		}
		var cameraEntity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
		var camera       = getRawCamera();
		var viewVector   = new Vec3(camera.getLookVector());
		var pickFrom     = camera.getPosition();
		var pickTo       = viewVector.scale(pickRange).add(pickFrom);
		var aabb         = new AABB(pickFrom, pickTo);
		return ProjectileUtil.getEntityHitResult(cameraEntity, pickFrom, pickTo, aabb, target -> !target.isSpectator() && target.isPickable(), pickRange);
	}

	/**
	 * 根据相机的视线探测方块
	 *
	 * @param pickRange  从相机出发的探测距离
	 * @param blockShape 方块形状获取器
	 * @param fluidShape 液体形状获取器
	 */
	public @NotNull BlockHitResult pickBlock (double pickRange, @NotNull ClipContext.Block blockShape, @NotNull ClipContext.Fluid fluidShape) {
		var camera       = getRawCamera();
		var pickFrom     = camera.getPosition();
		var viewVector   = new Vec3(camera.getLookVector());
		var pickTo       = viewVector.scale(pickRange).add(pickFrom);
		var cameraEntity = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
		return cameraEntity.level().clip(new ClipContext(pickFrom, pickTo, blockShape, fluidShape, cameraEntity));
	}

	/**
	 * 瞄准时使用的方块形状获取器是 {@link ClipContext.Block#COLLIDER}，不包含草
	 * <p>
	 * 非瞄准时使用的方块形状获取器是 {@link ClipContext.Block#OUTLINE}
	 * <p>
	 * 当探测不到方块时，返回的是 {@link HitResult.Type#MISS} 类型，坐标将为探测终点，即 相机位置 + 视线向量.normalize(探测距离)
	 *
	 * @param pickRange 从相机出发的探测距离
	 */
	@VersionSensitive
	public @NotNull BlockHitResult pickBlock (double pickRange) {
		return pickBlock(pickRange, ThirdPerson.ENTITY_AGENT.wasAiming() ? ClipContext.Block.COLLIDER: ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE);
	}

	/**
	 * 相机是否正在注视某个实体（无视其他实体或方块）
	 */
	@VersionSensitive
	public boolean isLookingAt (@NotNull Entity entity) {
		var from = getRawCamera().getPosition();
		var to   = from.add(new Vec3(getRawCamera().getLookVector()).scale(getPickRange()));
		var aabb = entity.getBoundingBox();
		return aabb.contains(from) || aabb.clip(from, to).isPresent();
	}

	/**
	 * 预测玩家可能想要射击的目标实体
	 */
	public @Nullable Entity predictTargetEntity (float partialTick) {
		var config = ThirdPerson.getConfig();
		// 候选目标实体
		List<Entity> candidateTargets = Lists.newArrayList();
		var          cameraPos        = getRawCamera().getPosition();
		var          cameraRot        = getRotation();
		var          cameraViewVector = LMath.directionFromRotationDegree(cameraRot).normalize();
		if (ThirdPerson.ENTITY_AGENT.isControlled()) {
			var playerEntity = ThirdPerson.ENTITY_AGENT.getRawPlayerEntity();
			var clientLevel  = (ClientLevel)playerEntity.level();
			var entityGetter = ((ClientLevelInvoker)clientLevel).invokeGetEntityGetter();
			for (var target: entityGetter.getAll()) {
				if (!(target instanceof LivingEntity)) {
					continue;
				}
				double distance = target.distanceTo(playerEntity);
				// 排除距离太近和太远的
				if (distance < 2 || distance > config.camera_ray_trace_length) {
					continue;
				}
				if (!target.is(playerEntity)) {
					var targetPos      = target.getPosition(partialTick);
					var bottomY        = LMath.toVector3d(targetPos.with(Direction.Axis.Y, target.getBoundingBox().minY));
					var vectorToBottom = bottomY.copy().sub(ThirdPerson.ENTITY_AGENT.getRawEyePosition(partialTick));
					if (LMath.rotationDegreeFromDirection(vectorToBottom).x() < cameraRot.x()) {
						continue;
					}
					var    vectorToTarget = LMath.toVector3d(targetPos.subtract(cameraPos)).normalizeSafely();
					double angrad         = Math.acos(cameraViewVector.dot(vectorToTarget));
					if (Math.toDegrees(angrad) < ThirdPersonConstants.TARGET_PREDICTION_DEGREES_LIMIT) {
						candidateTargets.add(target);
					}
				}
			}
		}
		if (!candidateTargets.isEmpty()) {
			candidateTargets.sort(new AimingTargetComparator(cameraPos, cameraViewVector));
			return candidateTargets.get(0);
		}
		return null;
	}

	/**
	 * 根据角度、距离、偏移量计算临时相机实际朝向和位置
	 * <p>
	 * 关于防止穿墙，参考 net.minecraft.client.Camera#getMaxZoom(double)
	 */
	private void updateTempCameraRotationPosition (float partialTick) {
		((CameraInvoker)tempCamera).invokeSetRotation((float)(relativeRotation.y() + 180), (float)-relativeRotation.x());
		var minecraft          = Minecraft.getInstance();
		var cameraDistanceMode = ThirdPerson.getConfig().camera_distance_mode;
		// 垂直视野角度一半(弧度制）
		double aspectRatio        = (double)minecraft.getWindow().getWidth() / minecraft.getWindow().getHeight();
		double fov                = ((GameRendererInvoker)minecraft.gameRenderer).invokeGetFov(getRawCamera(), partialTick, true);
		double verticalRadianHalf = Math.toRadians(fov) / 2;
		double heightHalf         = Math.tan(verticalRadianHalf) * ThirdPersonConstants.VANILLA_NEAR_PLANE_DISTANCE;
		double widthHalf          = aspectRatio * heightHalf;
		// 从旋转中心到相机的方向
		Vector3d direction;
		{
			var    forward           = LMath.toVector3d(tempCamera.getLookVector());
			var    left              = LMath.toVector3d(tempCamera.getLeftVector());
			var    up                = LMath.toVector3d(tempCamera.getUpVector());
			double verticalFovHalf   = Math.toRadians(fov);
			double horizontalFovHalf = 2 * Math.atan(widthHalf / ThirdPersonConstants.VANILLA_NEAR_PLANE_DISTANCE);
			var    offsetRatio       = smoothOffsetRatio.get();
			double offsetX           = offsetRatio.x();
			double offsetY           = offsetRatio.y();
			direction = forward.sub(up.mul(offsetY * Math.tan(verticalFovHalf / 2)).add(left.mul(offsetX * Math.tan(horizontalFovHalf / 2))));
			if (cameraDistanceMode == AbstractConfig.CameraDistanceMode.STRAIGHT) {
				direction.normalizeSafely();
			}
		}
		var    rotateCenterVector3d = getRotateCenterFinally(partialTick);
		double bodyRadius           = ThirdPerson.ENTITY_AGENT.getBodyRadius();
		var    cameraPosition       = LMath.toVec3(rotateCenterVector3d.sub(direction.mul(bodyRadius + smoothDistance.get())));
		((CameraInvoker)tempCamera).invokeSetPosition(cameraPosition);
		// 防止穿墙
		{
			var rotateCenter = LMath.toVec3(getRotateCenterFinally(partialTick));
			var entity       = ThirdPerson.ENTITY_AGENT.getRawCameraEntity();
			if (entity.isSpectator() && ThirdPerson.ENTITY_AGENT.isEyeInWall(ClipContext.Block.VISUAL)) {
				return;
			}
			var    rotateCenterToCamera = rotateCenter.vectorTo(cameraPosition);
			double initDistance         = rotateCenterToCamera.length();
			if (initDistance < 1e-5) {
				return;
			}
			double limit = initDistance;
			for (int i = 0; i < 8; ++i) {
				double offsetX   = ThirdPersonConstants.CAMERA_THROUGH_WALL_DETECTION * ((i & 1) * 2 - 1);
				double offsetY   = ThirdPersonConstants.CAMERA_THROUGH_WALL_DETECTION * ((i >> 1 & 1) * 2 - 1);
				double offsetZ   = ThirdPersonConstants.CAMERA_THROUGH_WALL_DETECTION * ((i >> 2 & 1) * 2 - 1);
				var    pickFrom  = rotateCenter.add(offsetX, offsetY, offsetZ);
				var    pickTo    = pickFrom.add(rotateCenterToCamera);
				var    hitResult = entity.level().clip(new ClipContext(pickFrom, pickTo, ThirdPersonConstants.CAMERA_OBSTACLE_BLOCK_SHAPE_GETTER, ClipContext.Fluid.NONE, ThirdPerson.ENTITY_AGENT.getRawCameraEntity()));
				if (hitResult.getType() != HitResult.Type.MISS) {
					limit = Math.min(limit, hitResult.getLocation().distanceTo(pickFrom));
				}
			}
			if (limit < initDistance) {
				switch (cameraDistanceMode) {
					case PLANE -> smoothDistance.setValue(Math.max(0, smoothDistance.get() + limit - initDistance));
					case STRAIGHT -> smoothDistance.setValue(Math.max(0, limit - bodyRadius));
					default -> throw new IllegalStateException("Invalid camera distance mode: " + cameraDistanceMode);
				}
				var limitedPosition = rotateCenter.add(rotateCenterToCamera.scale(limit / initDistance));
				((CameraInvoker)tempCamera).invokeSetPosition(limitedPosition);
			}
		}
	}

	private void updateSmoothVirtualDistance (double period) {
		var     config      = ThirdPerson.getConfig();
		boolean isAdjusting = ThirdPersonStatus.isAdjustingCameraDistance();
		var     mode        = config.getCameraOffsetScheme().getMode();
		if (minecraft.options.getCameraType() == CameraType.FIRST_PERSON) {
			// 当前的目标是第一人称
			smoothDistance.setHalflife(config.t2f_transition_halflife);
			smoothDistance.setTarget(-ThirdPerson.ENTITY_AGENT.getBodyRadius() * 0.5);
		} else {
			// 当前的目标不是第一人称
			smoothDistance.setHalflife(isAdjusting ? config.adjusting_distance_smooth_halflife: mode.getDistanceSmoothHalflife());
			smoothDistance.setTarget(mode.getDistanceLimit() * ThirdPerson.ENTITY_AGENT.vehicleTotalSizeCached * getSmoothFovDivisor());
		}
		smoothDistance.update(period);
		FINITE_CHECKER.checkOnce(smoothDistance.get());
	}

	private void updateSmoothOffsetRatio (double period) {
		var config = ThirdPerson.getConfig();
		var mode   = config.getCameraOffsetScheme().getMode();
		if (ThirdPersonStatus.isAdjustingCameraOffset()) {
			smoothOffsetRatio.setHalflife(config.adjusting_camera_offset_smooth_halflife);
		} else {
			smoothOffsetRatio.setHalflife(mode.getOffsetSmoothHalflife());
		}
		if (config.center_offset_when_flying && ThirdPerson.ENTITY_AGENT.isFallFlying()) {
			smoothOffsetRatio.setTarget(0, 0);
		} else {
			mode.getOffsetRatio(smoothOffsetRatio.target);
		}
		smoothOffsetRatio.update(period);
	}

	private void updateSmoothFovMultiplier (double period) {
		var config = ThirdPerson.getConfig();
		smoothFovDivisor.setHalflife(config.getCameraOffsetScheme().getMode().getDistanceSmoothHalflife());
		smoothFovDivisor.setTarget(ThirdPerson.ENTITY_AGENT.wasAiming() ? config.aiming_fov_divisor: 1);
		smoothFovDivisor.update(period);
	}
}
