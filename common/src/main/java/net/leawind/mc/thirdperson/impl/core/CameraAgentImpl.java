package net.leawind.mc.thirdperson.impl.core;


import com.mojang.blaze3d.Blaze3D;
import net.leawind.mc.thirdperson.ModConstants;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.api.cameraoffset.CameraOffsetMode;
import net.leawind.mc.thirdperson.api.core.CameraAgent;
import net.leawind.mc.thirdperson.impl.config.Config;
import net.leawind.mc.thirdperson.mixin.CameraInvoker;
import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.smoothvalue.ExpSmoothDouble;
import net.leawind.mc.util.math.smoothvalue.ExpSmoothVector2d;
import net.leawind.mc.util.math.vector.api.Vector2d;
import net.leawind.mc.util.math.vector.api.Vector3d;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CameraAgentImpl implements CameraAgent {
	private final          Minecraft         minecraft;
	private final @NotNull Camera            fakeCamera              = new Camera();
	private final @NotNull Vector2d          relativeRotation        = Vector2d.of(0);
	/**
	 * 相机偏移量
	 */
	private final @NotNull ExpSmoothVector2d smoothOffsetRatio;
	/**
	 * 虚相机到平滑眼睛的距离
	 */
	private final @NotNull ExpSmoothDouble   smoothDistanceToEye;
	private @Nullable      BlockGetter       level;
	/**
	 * renderTick 中更新
	 */
	private                boolean           wasCameraCloseToEntity  = false;
	/**
	 * 上次玩家操控转动视角的时间
	 */
	private                double            lastCameraTurnTimeStamp = 0;

	public CameraAgentImpl (Minecraft minecraft) {
		this.minecraft    = minecraft;
		smoothOffsetRatio = new ExpSmoothVector2d();
		smoothOffsetRatio.setSmoothFactorWeight(ModConstants.OFFSET_RATIO_SMOOTH_WEIGHT);
		smoothDistanceToEye = new ExpSmoothDouble();
		smoothDistanceToEye.setSmoothFactorWeight(ModConstants.DISTANCE_TO_EYE_SMOOTH_WEIGHT);
	}

	@Override
	public void reset () {
		ThirdPerson.lastPartialTick = minecraft.getFrameTime();
		smoothOffsetRatio.setValue(0, 0);
		smoothDistanceToEye.set(ThirdPerson.getConfig().distanceMonoList.get(0));
		if (minecraft.cameraEntity != null) {
			relativeRotation.set(-minecraft.cameraEntity.getViewXRot(ThirdPerson.lastPartialTick), minecraft.cameraEntity.getViewYRot(ThirdPerson.lastPartialTick) - 180);
		}
	}

	@Override
	public void onPreRender (double period, float partialTick) {
		if (!minecraft.isPaused()) {
			// 平滑更新距离
			updateSmoothVirtualDistance(period);
			// 平滑更新相机偏移量
			updateSmoothOffsetRatio(period);
		}
	}

	@Override
	public void onCameraSetup (double period) {
		updateFakeCameraRotationPosition();
		preventThroughWall();
		updateFakeCameraRotationPosition();
		applyCamera();
		wasCameraCloseToEntity = ThirdPerson.wasCameraCloseToEntity();
		//			if (wasCameraCloseToEntity) {
		//				// 假的第一人称，强制将相机放在玩家眼睛处
		//				Vec3 eyePosition = attachedEntity.getEyePosition(partialTick);
		//				((CameraInvoker)fakeCamera).invokeSetPosition(eyePosition);
		//				applyCamera();
		//			}
	}

	@Override
	public void onClientTickPre () {
	}

	public @NotNull Camera getRawCamera () {
		return Objects.requireNonNull(Minecraft.getInstance().gameRenderer.getMainCamera());
	}

	@Override
	public void setLevel (@Nullable BlockGetter level) {
		this.level = level;
	}

	@Override
	public boolean wasCameraCloseToEntity () {
		return wasCameraCloseToEntity;
	}

	@Override
	public @NotNull Vector2d calculateRotation () {
		return Vector2d.of(-relativeRotation.x(), relativeRotation.y() + 180);
	}

	@Override
	public @Nullable Vector3d getPickPosition () {
		return getPickPosition(smoothDistanceToEye.get() + ThirdPerson.getConfig().camera_ray_trace_length);
	}

	@Override
	public @NotNull HitResult pick () {
		return pick(smoothDistanceToEye.get() + ThirdPerson.getConfig().camera_ray_trace_length);
	}

	@Override
	public @NotNull Camera getFakeCamera () {
		return fakeCamera;
	}

	@Override
	public void onCameraTurn (double dy, double dx) {
		Config config = ThirdPerson.getConfig();
		if (config.is_mod_enable && !ThirdPerson.isAdjustingCameraOffset()) {
			dy *= 0.15;
			dx *= config.lock_camera_pitch_angle ? 0: -0.15;
			if (dy != 0 || dx != 0) {
				lastCameraTurnTimeStamp = Blaze3D.getTime();
				double rx = getRelativeRotation().x() + dx;
				double ry = getRelativeRotation().y() + dy;
				rx = LMath.clamp(rx, -ModConstants.CAMERA_PITCH_DEGREE_LIMIT, ModConstants.CAMERA_PITCH_DEGREE_LIMIT);
				getRelativeRotation().set(rx, ry % 360f);
			}
		}
	}

	@Override
	public @NotNull Vector2d getRelativeRotation () {
		return relativeRotation;
	}

	@Override
	public @Nullable Vector3d getPickPosition (double pickRange) {
		HitResult hitResult = pick(pickRange);
		return hitResult.getType() == HitResult.Type.MISS ? null: LMath.toVector3d(hitResult.getLocation());
	}

	@Override
	public @NotNull HitResult pick (double pickRange) {
		Camera          camera = getRawCamera();
		EntityHitResult ehr    = pickEntity(pickRange);
		BlockHitResult  bhr    = pickBlock(pickRange);
		return ehr == null ? bhr: bhr.getLocation().distanceTo(camera.getPosition()) < ehr.getLocation().distanceTo(camera.getPosition()) ? bhr: ehr;
	}

	@Override
	public @Nullable EntityHitResult pickEntity (double pickRange) {
		Entity cameraEntity = Minecraft.getInstance().cameraEntity;
		Camera camera       = getRawCamera();
		if (cameraEntity == null) {
			return null;
		}
		Vec3 viewVector = new Vec3(camera.getLookVector());
		Vec3 pickEnd    = viewVector.scale(pickRange).add(camera.getPosition());
		AABB aabb       = cameraEntity.getBoundingBox().expandTowards(viewVector.scale(pickRange)).inflate(1.0D, 1.0D, 1.0D);
		aabb = aabb.move(cameraEntity.getEyePosition(1).vectorTo(camera.getPosition()));
		return ProjectileUtil.getEntityHitResult(cameraEntity, camera.getPosition(), pickEnd, aabb, (Entity target) -> !target.isSpectator() && target.isPickable(), pickRange);
	}

	@Override
	public @NotNull BlockHitResult pickBlock (double pickRange) {
		Camera camera       = getRawCamera();
		Vec3   pickStart    = camera.getPosition();
		Vec3   viewVector   = new Vec3(camera.getLookVector());
		Vec3   pickEnd      = viewVector.scale(pickRange).add(pickStart);
		Entity cameraEntity = Minecraft.getInstance().cameraEntity;
		assert cameraEntity != null;
		return cameraEntity.level.clip(new ClipContext(pickStart, pickEnd, ThirdPerson.ENTITY_AGENT.wasAiming() ? ClipContext.Block.COLLIDER: ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, cameraEntity));
	}

	/**
	 * 根据角度、距离、偏移量计算假相机实际朝向和位置
	 */
	private void updateFakeCameraRotationPosition () {
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
		double upOffset   = smoothOffsetRatioValue.y() * smoothVirtualDistanceValue * Math.tan(verticalRadianHalf);
		double leftOffset = smoothOffsetRatioValue.x() * smoothVirtualDistanceValue * widthHalf / ModConstants.NEAR_PLANE_DISTANCE;
		// 没有偏移的情况下相机位置
		Vector3d positionWithoutOffset = calculatePositionWithoutOffset();
		// 应用到假相机
		((CameraInvoker)fakeCamera).invokeSetRotation((float)(relativeRotation.y() + 180), (float)-relativeRotation.x());
		((CameraInvoker)fakeCamera).invokeSetPosition(LMath.toVec3(positionWithoutOffset));
		((CameraInvoker)fakeCamera).invokeMove(0, upOffset, leftOffset);
	}

	/**
	 * 为防止穿墙，重新计算 smoothVirtualDistance 的值
	 */
	private void preventThroughWall () {
		// 防止穿墙
		Vec3   cameraPosition    = fakeCamera.getPosition();
		Vec3   smoothEyePosition = LMath.toVec3(ThirdPerson.ENTITY_AGENT.getSmoothEyePosition(ThirdPerson.lastPartialTick));
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
			Vec3      pickEnd   = pickStart.add(smoothEyeToCamera);
			HitResult hitResult = level.clip(new ClipContext(pickStart, pickEnd, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, Minecraft.getInstance().cameraEntity));
			if (hitResult.getType() != HitResult.Type.MISS) {
				minDistance = Math.min(minDistance, hitResult.getLocation().distanceTo(pickStart));
			}
		}
		smoothDistanceToEye.setValue(smoothDistanceToEye.get() * minDistance / initDistance);
	}

	/**
	 * 将假相机的朝向和位置应用到真相机上
	 */
	private void applyCamera () {
		Camera camera = getRawCamera();
		((CameraInvoker)camera).invokeSetRotation(fakeCamera.getYRot(), fakeCamera.getXRot());
		((CameraInvoker)camera).invokeSetPosition(fakeCamera.getPosition());
	}

	private @NotNull Vector3d calculatePositionWithoutOffset () {
		return ThirdPerson.ENTITY_AGENT.getPossiblySmoothEyePosition(ThirdPerson.lastPartialTick).add(LMath.directionFromRotationDegree(relativeRotation).mul(smoothDistanceToEye.get()));
	}

	private void updateSmoothVirtualDistance (double period) {
		Config           config      = ThirdPerson.getConfig();
		boolean          isAdjusting = ThirdPerson.isAdjustingCameraDistance();
		CameraOffsetMode mode        = config.cameraOffsetScheme.getMode();
		smoothDistanceToEye.setSmoothFactor(isAdjusting ? config.adjusting_distance_smooth_factor: mode.getDistanceSmoothFactor());
		smoothDistanceToEye.setTarget(mode.getMaxDistance());
		smoothDistanceToEye.update(period);
		// 如果是非瞄准模式下，且距离过远则强行放回去
		if (!config.cameraOffsetScheme.isAiming() && !isAdjusting) {
			smoothDistanceToEye.set(Math.min(mode.getMaxDistance(), smoothDistanceToEye.get()));
		}
	}

	private void updateSmoothOffsetRatio (double period) {
		Config           config = ThirdPerson.getConfig();
		CameraOffsetMode mode   = config.cameraOffsetScheme.getMode();
		if (ThirdPerson.isAdjustingCameraOffset()) {
			smoothOffsetRatio.setSmoothFactor(config.adjusting_camera_offset_smooth_factor);
		} else {
			mode.getOffsetSmoothFactor(smoothOffsetRatio.smoothFactor);
		}
		if (config.center_offset_when_flying && ThirdPerson.ENTITY_AGENT.isFallFlying()) {
			smoothOffsetRatio.setTarget(0, 0);
		} else {
			mode.getOffsetRatio(smoothOffsetRatio.target);
		}
		smoothOffsetRatio.update(period);
	}
}
