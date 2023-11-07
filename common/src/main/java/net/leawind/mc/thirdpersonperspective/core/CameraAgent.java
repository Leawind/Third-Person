package net.leawind.mc.thirdpersonperspective.core;


import com.mojang.blaze3d.Blaze3D;
import com.mojang.logging.LogUtils;
import net.leawind.mc.thirdpersonperspective.config.Config;
import net.leawind.mc.thirdpersonperspective.mixin.CameraInvoker;
import net.leawind.mc.thirdpersonperspective.userprofile.UserProfile;
import net.leawind.mc.util.smoothvalue.ExpSmoothDouble;
import net.leawind.mc.util.smoothvalue.ExpSmoothVec2;
import net.minecraft.client.Camera;
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
	public static final Logger          LOGGER            = LogUtils.getLogger();
	public static       BlockGetter     level;
	public static       Camera          camera;
	public static       LocalPlayer     player;
	public static       boolean         isThirdPerson     = false;
	public static       ExpSmoothVec2   smoothOffsetRatio = new ExpSmoothVec2().setValue(0, 0);
	public static       double          lastTickTime      = 0;
	public static       boolean         isAiming          = false;
	// 上次转动视角的时间
	public static       double          lastTurnTime      = 0;
	/**
	 * 虚相机到平滑眼睛的距离
	 */
	public static       ExpSmoothDouble smoothDistance    = new ExpSmoothDouble().setValue(0).setTarget(0);
	public static       Vec2            relativeRotation  = Vec2.ZERO;
	/**
	 * 相机上次的朝向和位置
	 */
	public static       Vec3            lastPosition      = Vec3.ZERO;
	public static       Vec2            lastRotation      = Vec2.ZERO;

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
		smoothDistance.setValue(0);
		relativeRotation = new Vec2(-player.getXRot(), player.getYRot() - 180);
		LOGGER.info("Reset CameraAgent");
	}

	public static void updateUserProfile (CameraOffsetProfile profile) {
		smoothOffsetRatio.setSmoothFactor(profile.getMode().offsetSmoothFactor);
		smoothDistance.setSmoothFactor(profile.getMode().distanceSmoothFactor);
	}

	/**
	 * 鼠标移动导致的相机旋转
	 *
	 * @param turnY 偏航角变化量
	 * @param turnX 俯仰角变化量
	 */
	public static void onCameraTurn (double turnY, double turnX) {
		turnY *= 0.15;
		turnX *= -0.15;
		if (turnY != 0 || turnX != 0) {
			lastTurnTime     = Blaze3D.getTime();
			relativeRotation = new Vec2((float)Mth.clamp(relativeRotation.x + turnX, -89.8, 89.8),
										(float)(relativeRotation.y + turnY) % 360f);
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
		PlayerAgent.turnToCamera(1);
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
		// 时间
		double now           = Blaze3D.getTime();
		double sinceLastTurn = now - lastTurnTime;
		double sinceLastTick = now - lastTickTime;
		lastTickTime = now;
		// TODO public static CameraOffsetProfile profile;
		CameraOffsetProfile profile = UserProfile.getCameraOffsetProfile();
		profile.setAiming(isAiming);
		if (isThirdPerson) {
			smoothDistance.setSmoothFactor(Options.isAdjustingCameraOffset() ? 1e-5: profile.getMode().distanceSmoothFactor);
			// 平滑更新距离
			smoothDistance.setTarget(profile.getMode().maxDistance).update(sinceLastTick);
			// 如果是非瞄准模式下，且距离过远则强行放回去
			if (!profile.isAiming && !Options.isAdjustingCameraOffset()) {
				smoothDistance.setValue(Math.min(profile.getMode().maxDistance, smoothDistance.getValue()));
			}
			// 平滑更新相机偏移量
			smoothOffsetRatio.setTarget(profile.getMode().getOffsetRatio(smoothDistance.getValue()));
			smoothOffsetRatio.update(sinceLastTick);
			// 设置相机朝向和位置
			updateCameraRotationPosition();
			// TODO 防止穿墙
		}
		PlayerAgent.onRenderTick(lerpK, sinceLastTick);
		lastPosition = camera.getPosition();
		lastRotation = new Vec2(camera.getXRot(), camera.getYRot());
	}

	/**
	 * 根据偏移量计算相机实际位置
	 */
	private static void updateCameraRotationPosition () {
		Minecraft mc = Minecraft.getInstance();
		// 平滑眼睛到虚相机的向量
		Vec3 eyeToVirtualCamera = Vec3.directionFromRotation(relativeRotation).scale(smoothDistance.getValue());
		// 宽高比
		double aspectRatio = (double)mc.getWindow().getWidth() / mc.getWindow().getHeight();
		// 垂直视野角度一半(弧度制）
		double verticalRadianHalf = mc.options.fov().get() * Math.PI / 180 / 2;
		// 成像平面宽高
		double heightHalf = Math.tan(verticalRadianHalf) * 0.05;
		double widthHalf  = aspectRatio * heightHalf;
		// 水平视野角度一半(弧度制）
		double horizonalRadianHalf = Math.atan(widthHalf / 0.05);
		// 偏移
		double leftOffset = smoothOffsetRatio.getValue().x * smoothDistance.getValue() * widthHalf / 0.05;
		double upOffset   = smoothOffsetRatio.getValue().y * smoothDistance.getValue() * Math.tan(verticalRadianHalf);
		// 没有偏移的情况下相机位置
		Vec3 virtualPosition = PlayerAgent.smoothEyePosition.getValue().add(eyeToVirtualCamera);
		// 应用
		((CameraInvoker)camera).invokeSetRotation(relativeRotation.y + 180, -relativeRotation.x);
		((CameraInvoker)camera).invokeSetPosition(virtualPosition);
		((CameraInvoker)camera).invokeMove(0, upOffset, leftOffset);
	}

	/**
	 * 获取相机视线落点坐标
	 */
	public static @Nullable Vec3 getPickPosition () {
		return getPickPosition(smoothDistance.getValue() + Config.camera_ray_trace_length);
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
		return pick(smoothDistance.getValue() + Config.camera_ray_trace_length);
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
