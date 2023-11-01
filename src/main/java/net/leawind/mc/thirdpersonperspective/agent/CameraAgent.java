package net.leawind.mc.thirdpersonperspective.agent;


import com.mojang.blaze3d.Blaze3D;
import net.leawind.mc.thirdpersonperspective.Config;
import net.leawind.mc.thirdpersonperspective.ThirdPersonPerspective;
import net.leawind.mc.thirdpersonperspective.mixininterface.CameraMixinInterface;
import net.leawind.mc.util.Vectors;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 第三人称自由视角相机代理
 */
public class CameraAgent {
	@NotNull
	public final  CameraMixinInterface camera;
	public        LocalPlayer          player;
	/**
	 * 当前是否是第三人称自由视角
	 */
	public        boolean              isFreeTpv             = false;
	// 虚球心
	public        Vec3                 eyePositionSmooth;
	// 虚相机坐标
	private       Vec3                 virtualPosition;
	/**
	 * 上次 renderTick 的时间
	 */
	private       double               lastTickTime          = 0;
	private       float                lerpK                 = 1.0f;
	/**
	 * 上次使用鼠标旋转第三人称自由视角的时间
	 */
	private       double               lastTurnTime          = 0;
	public        boolean              isAiming;
	private       Vec2                 cameraOffsetSmooth    = Vec2.ZERO;
	/**
	 * 相机偏移类型（机位）
	 */
	public        CameraOffsetType     cameraOffsetType      = CameraOffsetType.NORMAL_RIGHT;
	public        boolean              wasAtWall             = false;
	/**
	 * 鼠标停止移动多长时间后退出相对环绕模式 随玩家移动速度变化而变化 单位是 秒(second)
	 */
	public static double               relativeModeLastsTime = 0.5;

	/**
	 * 相对运动模式的持续时间 由玩家速度决定
	 * <p>
	 * 玩家速度越快，持续时间越短
	 */
	public void updateRelativeModeLastsTime () {
		relativeModeLastsTime = 1.5 * Math.exp(-((Camera)camera).getEntity().getDeltaMovement().horizontalDistance());
	}

	/**
	 * 判断当前是否在瞄准<br/>
	 * <p>
	 * 如果正在使用弓或三叉戟瞄准，返回true
	 * <p>
	 * 如果正在手持上了弦的弩，返回true
	 * <p>
	 * 如果按住了相应按键，返回true
	 * <p>
	 * 如果通过按相应按键切换到了持续瞄准状态，返回true
	 */
	public boolean isAiming () {
		LocalPlayer player = (LocalPlayer)((Camera)camera).getEntity();
		if (player.isUsingItem()) {
			ItemStack itemStack = player.getUseItem();
			if (itemStack.is(Items.BOW) || itemStack.is(Items.TRIDENT)) {
				// 正在使用弓或三叉戟瞄准
				return true;
			}
		}
		ItemStack mainHandItem = player.getMainHandItem();
		if (mainHandItem.is(Items.CROSSBOW) && CrossbowItem.isCharged(mainHandItem)) {
			// 主手拿着上了弦的弩
			return true;
		}
		ItemStack offhandItem = player.getOffhandItem();
		if (offhandItem.is(Items.CROSSBOW) && CrossbowItem.isCharged(offhandItem)) {
			// 副手拿着上了弦的弩
			return true;
		}
		return ThirdPersonPerspective.Options.isForceKeepAiming || ThirdPersonPerspective.Options.isToggleToAiming;
	}

	/**
	 * 计算并更新相机的朝向和坐标
	 * <p>
	 *
	 * @param blockGetter 维度
	 * @param entity      实体
	 * @param isFarther   是否远距离
	 */
	//	@PerformanceSensitive
	public void onRenderTick (BlockGetter blockGetter, Entity entity, boolean isFarther, float lerpK) {
		this.player = (LocalPlayer)entity;
		this.lerpK  = lerpK;
		// 时间
		double now           = Blaze3D.getTime();
		double sinceLastTick = now - lastTickTime;
		lastTickTime = now;
		double sinceLastTurn = now - lastTurnTime;
		if (player.isDeadOrDying()) {
			onEnterThirdPerson(lerpK);
			return;
		}

		/*
		  相对模式
		  启用时，虚球心到虚相机的相对角度不变，即vRot不变
		 */
		boolean isRelativeMode = sinceLastTurn < relativeModeLastsTime;
		// 计算虚球体半径
		if (isFreeTpv) {
			CameraRenderTickContext context     = new CameraRenderTickContext(this);
			Vec3                    eyePosition = entity.getEyePosition(lerpK);
			// 更新相对模式的持续时间
			updateRelativeModeLastsTime();
			if (Config.auto_switch_camera_offset_type && !cameraOffsetType.isTop()) {
				/*
					根据周围方块状态自动调整机位，以获得最佳视野

					# 表示阻挡视线的方块
					0 表示不阻挡视线的方块
					* 表示任意方块

					头顶机位：
						* 0 *  |  # 0 #
						# * #  |  * * *
						* * *  |  * * *
					右侧：
						# * 0
						* * 0
						* * 0
					左侧：
						0 * #
						0 * *
						0 * *
				*/
				float cameraRotY = getRelativeRotation().y;
				SurroundingBlocks surroundingBlocks = new SurroundingBlocks(blockGetter,
																			BlockPos.containing(eyePosition),
																			Direction.fromYRot(cameraRotY + 180),
																			Direction.fromYRot(cameraRotY + 90));
				if (surroundingBlocks.match(new Boolean[]{true, false, true, null, null, null, null, null, null,})) {
					cameraOffsetType = cameraOffsetType.toTop();
				} else if (surroundingBlocks.match(new Boolean[]{null, false, null, true, null, true, null, null, null,})) {
					cameraOffsetType = cameraOffsetType.toTop();
				} else if (surroundingBlocks.match(new Boolean[]{true, null, false, null, null, false, null, null, false,})) {
					cameraOffsetType = cameraOffsetType.toRight();
				} else if (surroundingBlocks.match(new Boolean[]{false, null, true, false, null, null, false, null, null,})) {
					cameraOffsetType = cameraOffsetType.toLeft();
				}
				//				surroundingBlocks.reprint();
			}
			{    // 更新相机偏移量的平滑值
				Vec2 cameraOffsetTarget = cameraOffsetType.getOffsetRatio(getRelativeDistance());
				cameraOffsetSmooth = new Vec2((float)Mth.lerp(
					1 - Math.pow(cameraOffsetType.getOffsetUpdateSpeed(), sinceLastTick),
					cameraOffsetSmooth.x,
					cameraOffsetTarget.x),
											  (float)Mth.lerp(
												  1 - Math.pow(cameraOffsetType.getOffsetUpdateSpeed(), sinceLastTick),
												  cameraOffsetSmooth.y,
												  cameraOffsetTarget.y));
			}
			Vec2   relativeRotation = getRelativeRotation();
			double relativeDistance = getRelativeDistance();
			{// 让虚球心平滑移向实体眼睛，如果是相对模式则虚相机也同步移动，保持虚球心和虚相机的相对位置不变
				eyePositionSmooth = Vectors.lerp(eyePositionSmooth,
												 eyePosition,
												 Vectors.pow(cameraOffsetType.getSmoothEyeUpdateSpeed(), sinceLastTick)
														.reverse().add(1, 1, 1));
				if (isRelativeMode) {
					applyRelatives(relativeRotation, relativeDistance);
				} else {
					relativeDistance = getRelativeDistance();
					relativeRotation = getRelativeRotation();
				}
			}
			// 平滑地将虚相机移动到虚球体表面
			relativeDistance = Mth.lerp(
				1 - Math.pow(Config.move_to_surface_speed * cameraOffsetType.getMoveToSurfaceSpeedAmplifier(), sinceLastTick),
				relativeDistance,
				cameraOffsetType.getVirtualSphereRadius(isFarther));
			applyRelatives(relativeRotation, relativeDistance);
			isAiming = isAiming();
			if (isAiming) { // 瞄准模式
				cameraOffsetType = cameraOffsetType.toAim();
			} else {  // 非瞄准的普通模式
				cameraOffsetType = cameraOffsetType.toNormal();
				// 检查相机虚位置是否超出虚球体，如果超出则放回去
				if (isRelativeMode) {
					// 相对旋转模式
					relativeDistance = Math.min(relativeDistance, cameraOffsetType.getVirtualSphereRadius(isFarther));
					applyRelatives(relativeRotation, relativeDistance);
				} else {
					// 绝对位置模式
					if (relativeDistance > cameraOffsetType.getVirtualSphereRadius(isFarther)) {
						virtualPosition = eyePositionSmooth.vectorTo(getVirtualPosition()).normalize()
														   .scale(cameraOffsetType.getVirtualSphereRadius(isFarther)).add(
								eyePositionSmooth);
					}
				}
				{    // 检查虚相机是否进入了玩家圆柱内，如果进入则移出
					final AABB aabb = player.getBoundingBox();
					// 圆柱半径
					final double radius = 0.5 * Math.sqrt(
						aabb.getXsize() * aabb.getXsize() + aabb.getZsize() * aabb.getZsize());
					// 水平距离
					final double horizontalDistance = eyePositionSmooth.vectorTo(virtualPosition).horizontalDistance();
					if (2E-4 < horizontalDistance && horizontalDistance < radius) {
						virtualPosition = new Vec3(
							eyePositionSmooth.x + radius * (virtualPosition.x - eyePositionSmooth.x) / horizontalDistance,
							virtualPosition.y,
							eyePositionSmooth.z + radius * (virtualPosition.z - eyePositionSmooth.z) / horizontalDistance);
					}
				}
				// 根据虚相机位置计算实相机朝向，让实相机看向实体
				turnToEntity(context);
			}
			{    // 防止穿墙
				relativeRotation = getRelativeRotation();
				relativeDistance = getRelativeDistance();
				Minecraft mc          = Minecraft.getInstance();
				double    aspectRatio = (double)mc.getWindow().getWidth() / (double)mc.getWindow().getHeight();
				double    halfHeight  = Math.tan(mc.options.fov().get() * Math.PI / 180 / 2) * 0.05;
				double    halfWidth   = halfHeight * aspectRatio;
				Vec3      forward     = (new Vec3(((Camera)camera).getLookVector())).scale(0.05F);
				Vec3      up          = (new Vec3(((Camera)camera).getUpVector())).scale(halfHeight).add(0, 0.1, 0);
				Vec3      left        = (new Vec3(((Camera)camera).getLeftVector())).scale(halfWidth);
				Vec3      viewVector  = eyePosition.vectorTo(context.position);
				double    length      = viewVector.length();
				double    maxDistance = length;
				for (Vec3 offset: Arrays.asList(forward.add(up).add(left),
												forward.add(up).subtract(left),
												forward.subtract(up).add(left),
												forward.subtract(up).subtract(left))) {
					Vec3 offsetCenter = eyePosition.subtract(offset);
					Vec3 viewEnd      = offsetCenter.add(viewVector);
					BlockHitResult hitResult = blockGetter.clip(new ClipContext(offsetCenter,
																				viewEnd,
																				Block.VISUAL,
																				ClipContext.Fluid.NONE,
																				entity));
					if (hitResult.getType() != HitResult.Type.MISS) {
						maxDistance = Mth.clamp(hitResult.getLocation().distanceTo(offsetCenter), 0, length);
					}
				}
				if (maxDistance < length) {
					wasAtWall = true;
					relativeDistance *= maxDistance / length;
					applyRelatives(relativeRotation, relativeDistance);
				}
			}
			turnToEntity(context);
			context.apply();
			LocalPlayerAgent.getInstance().onRenderTick(lerpK);
		}
	}

	public @Nullable Vec3 getPickPosition () {
		return getPickPosition(((Camera)camera).getPosition().distanceTo(player.getEyePosition(lerpK)) + 128);
	}

	/**
	 * 获取相机视线的击中的坐标
	 *
	 * @param pickRange 最大探测距离
	 */
	public @Nullable Vec3 getPickPosition (double pickRange) {
		HitResult hitResult = pick(pickRange);
		if (hitResult.getType() == HitResult.Type.MISS) {
			return null;
		}
		return hitResult.getLocation();
	}

	public @NotNull HitResult pick (double pickRange) {
		EntityHitResult ehr = pickEntity(pickRange);
		return ehr != null ? ehr : pickBlock(pickRange);
	}

	private @Nullable EntityHitResult pickEntity (double pickRange) {
		Vec3 eye        = ((Camera)camera).getPosition();
		Vec3 viewVector = new Vec3(((Camera)camera).getLookVector());
		Vec3 viewEnd    = viewVector.scale(pickRange).add(eye);
		AABB aabb       = player.getBoundingBox().expandTowards(viewVector.scale(pickRange)).inflate(1.0D, 1.0D, 1.0D);
		return ProjectileUtil.getEntityHitResult(player,
												 eye,
												 viewEnd,
												 aabb,
												 (Entity target) -> !target.isSpectator() && target.isPickable(),
												 pickRange);
	}

	private @NotNull BlockHitResult pickBlock (double pickRange) {
		Vec3 eye        = ((Camera)camera).getPosition();
		Vec3 viewVector = new Vec3(((Camera)camera).getLookVector());
		Vec3 viewEnd    = viewVector.scale(pickRange).add(eye);
		return player.level().clip(new ClipContext(eye, viewEnd, Block.OUTLINE, ClipContext.Fluid.NONE, player));
	}

	/**
	 * 鼠标移动导致的相机旋转
	 *
	 * @param dy 水平角变化量
	 * @param dx 俯仰角变化量
	 */
	public void onCameraTurn (double dy, double dx) {
		dy *= 0.15;
		dx *= -0.15;
		if (dy != 0 || dx != 0) {
			Vec2   relativeRotation = getRelativeRotation();
			double relativeDistance = getRelativeDistance();
			relativeRotation = new Vec2((float)Mth.clamp(relativeRotation.x + dx, -89.8, 89.8),
										(float)(relativeRotation.y + dy) % 360f);
			applyRelatives(relativeRotation, relativeDistance);
			wasAtWall    = false;
			lastTurnTime = Blaze3D.getTime();
		}
	}

	/**
	 * 进入第三人称视角时
	 */
	public void onEnterThirdPerson (float lerpK) {
		player = Minecraft.getInstance().player;
		LocalPlayerAgent.getInstance().syncRotation();
		isFreeTpv = true;
		isAiming  = false;
		// 将虚拟球心放在实体眼睛处
		eyePositionSmooth = player.getEyePosition(lerpK);
		// 移动虚相机到实体后面一点
		virtualPosition                                 = new Vec3(((Camera)camera).getLookVector().normalize(-0.2f)).add(
			eyePositionSmooth);
		cameraOffsetSmooth                              = new Vec2(0, 0);
		wasAtWall                                       = false;
		ThirdPersonPerspective.Options.isToggleToAiming = false;
		lastTickTime                                    = Blaze3D.getTime();
	}

	private CameraOffsetType lastCameraOffsetType = CameraOffsetType.NORMAL_LEFT;

	/**
	 * 切换到下一个机位
	 * <p>
	 * 如果当前不是头顶，则左右切换
	 * <p>
	 * 如果是头顶，则切换到上一个机位
	 */
	public void nextCameraOffsetType () {
		if (cameraOffsetType.isTop()) {
			if (lastCameraOffsetType.isTop()) {
				lastCameraOffsetType = CameraOffsetType.NORMAL_RIGHT;
			}
			CameraOffsetType temp = cameraOffsetType;
			cameraOffsetType     = lastCameraOffsetType;
			lastCameraOffsetType = temp;
		} else {
			lastCameraOffsetType = cameraOffsetType;
			cameraOffsetType     = cameraOffsetType.oppsite();
		}
	}

	/**
	 * 切换至头顶机位
	 */
	public void setCameraOffsetTypeToTop () {
		if (!cameraOffsetType.isTop()) {
			lastCameraOffsetType = cameraOffsetType;
			cameraOffsetType     = cameraOffsetType.toTop();
		}
	}

	/**
	 * 相对朝向
	 *
	 * @return 从虚球心到虚相机的朝向
	 */
	public @NotNull Vec2 getRelativeRotation () {
		return Vectors.rotationAngleFromDirection(eyePositionSmooth.vectorTo(getVirtualPosition()));
	}

	/**
	 * 虚球心到虚相机的距离
	 */
	public double getRelativeDistance () {
		return eyePositionSmooth.distanceTo(virtualPosition);
	}

	/**
	 * 根据 相对朝向 和距离 更新虚相机位置
	 *
	 * @param relativeRotation 虚球心到虚相机的相对朝向
	 * @param relativeDistance 虚球心到虚相机的相对距离
	 */
	public void applyRelatives (Vec2 relativeRotation, double relativeDistance) {
		virtualPosition = Vec3.directionFromRotation(relativeRotation).scale(relativeDistance).add(eyePositionSmooth);
	}

	private void turnToEntity (CameraRenderTickContext context) {
		Minecraft mc = Minecraft.getInstance();
		// 将实相机放在虚相机处
		context.position = virtualPosition;
		// 虚相机到虚球心的向量
		Vec3 viewVector = virtualPosition.vectorTo(eyePositionSmooth);
		// 转向虚球心
		context.setRotation(Vectors.rotationAngleFromDirection(viewVector));
		// 通过更新实相机位置将虚球心放在屏幕指定位置
		// 屏幕视野角度
		double aspectRatio = (double)mc.getWindow().getWidth() / mc.getWindow().getHeight();
		double halfV       = mc.options.fov().get() * Math.PI / 180;              // 垂直视野角度(弧度制）
		double halfH       = 2 * Math.atan(aspectRatio * Math.tan(halfV / 2));    // 水平视野角度(弧度制）
		double distance    = virtualPosition.distanceTo(eyePositionSmooth);  // 到虚拟球心的距离
		double yRotOffset  = distance * Math.tan(cameraOffsetSmooth.y * halfV / 2);
		double xRotOffset  = distance * Math.tan(cameraOffsetSmooth.x * halfH / 2);
		context.moveRelative(0, yRotOffset, xRotOffset);
	}

	/**
	 * 获取虚相机位置
	 */
	public @NotNull Vec3 getVirtualPosition () {
		return virtualPosition;
	}

	/**
	 * 获取实相机朝向
	 */
	public @NotNull Vec2 getRealRotation () {
		return new Vec2(((Camera)camera).getXRot(), ((Camera)camera).getYRot());
	}

	/**
	 * 获取实相机水平朝向
	 */
	public float getRealRotY () {
		return ((Camera)camera).getYRot();
	}

	public CameraAgent (
		@NotNull
		Camera camera) {
		this.camera       = (CameraMixinInterface)camera;
		this.player       = (LocalPlayer)camera.getEntity();
		virtualPosition   = player.getEyePosition(lerpK);
		eyePositionSmooth = getVirtualPosition();
	}

	private static CameraAgent instance;

	/**
	 * 相机代理实例是否可用
	 */
	public static boolean isAvailable () {
		return getInstance() != null && getInstance().player != null;
	}

	/**
	 * 尝试获取相机代理实例
	 */
	public static CameraAgent getInstance () {
		if (instance == null) {
			Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
			if (camera.isInitialized()) {
				Vec3 pos = camera.getPosition();
				ThirdPersonPerspective.LOGGER.info(String.format("Creating camera agent for camera at <%.5f,%.5f,%.5f>",
																 pos.x,
																 pos.y,
																 pos.z));
				instance = new CameraAgent(camera);
			}
		}
		return instance;
	}

	/**
	 * 相机偏移类型
	 * <p>
	 * NORMAL 开头的是常规视角
	 * <p>
	 * AIM 开头的是瞄准视角
	 */
	public enum CameraOffsetType {
		NORMAL_RIGHT(isFarther -> isFarther ? Config.camera_max_distance_farther : Config.camera_max_distance_closer,
					 d -> new Vec2(-Config.camera_offset_ratio_x, Config.camera_offset_ratio_y),
					 () -> Config.camera_offset_update_speed,
					 () -> Config.smooth_eye_speed),
		NORMAL_LEFT(isFarther -> isFarther ? Config.camera_max_distance_farther : Config.camera_max_distance_closer,
					d -> new Vec2(+Config.camera_offset_ratio_x, Config.camera_offset_ratio_y),
					() -> Config.camera_offset_update_speed,
					() -> Config.smooth_eye_speed),
		NORMAL_TOP(isFarther -> isFarther ? Config.camera_max_distance_farther : Config.camera_max_distance_closer,
				   d -> new Vec2(0, Config.camera_offset_ratio_top),
				   () -> Config.camera_offset_update_speed,
				   () -> Config.smooth_eye_speed),
		AIM_RIGHT(isFarther -> Math.log(
			1 + (isFarther ? Config.camera_max_distance_farther : Config.camera_max_distance_closer)),
				  d -> new Vec2((float)-Math.atan2(Config.aim_offset, d), 0),
				  () -> Config.camera_offset_update_speed * 1e-3,
				  () -> Config.smooth_eye_speed.scale(1e-3)),
		AIM_LEFT(isFarther -> Math.log(
			1 + (isFarther ? Config.camera_max_distance_farther : Config.camera_max_distance_closer)),
				 d -> new Vec2((float)Math.atan2(Config.aim_offset, d), 0),
				 () -> Config.camera_offset_update_speed * 1e-3,
				 () -> Config.smooth_eye_speed.scale(1e-3)),
		AIM_TOP(isFarther -> Math.log(1 + (isFarther ? Config.camera_max_distance_farther :
										   Config.camera_max_distance_closer)),
				d -> new Vec2(0, (float)Math.atan2(Config.aim_offset_top, d)),
				() -> Config.camera_offset_update_speed * 1e-3,
				() -> Config.smooth_eye_speed.scale(1e-3));
		/**
		 * 虚球体半径
		 */
		private final Function<Boolean, Double> virtualSphereRadius;
		/**
		 * 相机偏移量
		 */
		private final Function<Double, Vec2>    offset;
		/**
		 * 平滑偏移量的更新速度
		 */
		private final Supplier<Double>          offsetUpdateSpeed;
		/**
		 * 平滑眼睛坐标的更新速度
		 */
		private final Supplier<Vec3>            smoothEyeUpdateSpeed;

		public Vec2 getOffsetRatio (double distance) {
			return offset.apply(distance);
		}

		public double getVirtualSphereRadius (boolean isFarther) {
			return virtualSphereRadius.apply(isFarther);
		}

		public double getOffsetUpdateSpeed () {
			return this.offsetUpdateSpeed.get();
		}

		public Vec3 getSmoothEyeUpdateSpeed () {
			return smoothEyeUpdateSpeed.get();
		}

		public double getMoveToSurfaceSpeedAmplifier () {
			return isAim() ? 1e-2 : 1;
		}

		CameraOffsetType (Function<Boolean, Double> virtualSphereRadius,
						  Function<Double, Vec2> offsetGetter,
						  Supplier<Double> offsetUpdateSpeedSupplier,
						  Supplier<Vec3> smoothEyeUpdateSpeed) {
			this.virtualSphereRadius  = virtualSphereRadius;
			this.offset               = offsetGetter;
			this.offsetUpdateSpeed    = offsetUpdateSpeedSupplier;
			this.smoothEyeUpdateSpeed = smoothEyeUpdateSpeed;
		}

		public CameraOffsetType oppsite () {
			return switch (this) {
				case NORMAL_RIGHT, NORMAL_TOP -> NORMAL_LEFT;
				case NORMAL_LEFT -> NORMAL_RIGHT;
				case AIM_RIGHT, AIM_TOP -> AIM_LEFT;
				case AIM_LEFT -> AIM_RIGHT;
			};
		}

		public CameraOffsetType toNormal () {
			return switch (this) {
				case NORMAL_RIGHT, AIM_RIGHT -> NORMAL_RIGHT;
				case NORMAL_TOP, AIM_TOP -> NORMAL_TOP;
				case NORMAL_LEFT, AIM_LEFT -> NORMAL_LEFT;
			};
		}

		public CameraOffsetType toAim () {
			return switch (this) {
				case NORMAL_RIGHT, AIM_RIGHT -> AIM_RIGHT;
				case NORMAL_LEFT, AIM_LEFT -> AIM_LEFT;
				case NORMAL_TOP, AIM_TOP -> AIM_TOP;
			};
		}

		public CameraOffsetType toRight () {
			return switch (this) {
				case AIM_RIGHT, AIM_LEFT, AIM_TOP -> AIM_RIGHT;
				case NORMAL_RIGHT, NORMAL_LEFT, NORMAL_TOP -> NORMAL_RIGHT;
			};
		}

		public CameraOffsetType toLeft () {
			return switch (this) {
				case AIM_RIGHT, AIM_LEFT, AIM_TOP -> AIM_LEFT;
				case NORMAL_RIGHT, NORMAL_LEFT, NORMAL_TOP -> NORMAL_LEFT;
			};
		}

		public CameraOffsetType toTop () {
			return switch (this) {
				case AIM_RIGHT, AIM_LEFT, AIM_TOP -> AIM_TOP;
				case NORMAL_RIGHT, NORMAL_LEFT, NORMAL_TOP -> NORMAL_TOP;
			};
		}

		public boolean isTop () {
			return switch (this) {
				case AIM_TOP, NORMAL_TOP -> true;
				default -> false;
			};
		}

		public boolean isAim () {
			return switch (this) {
				case AIM_RIGHT, AIM_LEFT, AIM_TOP -> true;
				default -> false;
			};
		}
	}
}

class CameraRenderTickContext {
	// Absolutely
	public        Vec3        position;
	private       Vec2        rotation;
	private final CameraAgent agent;
	private final Quaternionf rotationQuaternion = new Quaternionf(0.0F, 0.0F, 0.0F, 1.0F);
	private final Vector3f    forwards           = new Vector3f(0.0F, 0.0F, 1.0F);
	private final Vector3f    up                 = new Vector3f(0.0F, 0.0F, 1.0F);
	private final Vector3f    left               = new Vector3f(0.0F, 0.0F, 1.0F);

	public CameraRenderTickContext (CameraAgent agent) {
		this.agent    = agent;
		this.position = ((Camera)agent.camera).getPosition();
		this.rotation = agent.getRealRotation();
	}

	public void setRotation (Vec2 rot) {
		this.rotation = rot;
		this.rotationQuaternion.rotationYXZ(-rot.y * ((float)Math.PI / 180F), rot.x * ((float)Math.PI / 180F), 0.0F);
		this.forwards.set(0.0F, 0.0F, 1.0F).rotate(this.rotationQuaternion);
		this.up.set(0.0F, 1.0F, 0.0F).rotate(this.rotationQuaternion);
		this.left.set(1.0F, 0.0F, 0.0F).rotate(this.rotationQuaternion);
	}

	public void moveRelative (double toForward, double toUp, double toLeft) {
		double dx = forwards.x * toForward + up.x * toUp + left.x * toLeft;
		double dy = forwards.y * toForward + up.y * toUp + left.y * toLeft;
		double dz = forwards.z * toForward + up.z * toUp + left.z * toLeft;
		position = new Vec3(position.x + dx, position.y + dy, position.z + dz);
	}

	/**
	 * 应用修改
	 */
	public void apply () {
		agent.camera.third_Person_View$setRealPosition(position);
		agent.camera.third_Person_View$setRealRotation(rotation.y, rotation.x);
	}
}

/**
 * 用于处理以指定坐标为中心的 3x3 九宫格中的方块状态
 */
@SuppressWarnings("unused")
class SurroundingBlocks {
	final boolean[] blockMap = new boolean[]{false, false, false,//
											 false, false, false,//
											 false, false, false,};

	/**
	 * @param center  中心方块坐标
	 * @param forward 向正前方
	 * @param left    向左
	 */
	public SurroundingBlocks (BlockGetter blockGetter, BlockPos center, Direction forward, Direction left) {
		for (int xoff = -1; xoff <= 1; xoff++) {
			for (int zoff = -1; zoff <= 1; zoff++) {
				BlockPos blockPos =
					center.offset(forward.getNormal().multiply(-zoff)).offset(left.getNormal().multiply(-xoff));
				// 选哪个比较合适？
				//  ()			.isSolidRender(blockGetter, blockPos))
				//  (ok)		.isViewBlocking(blockGetter, blockPos)
				//  (maybe) 	!.canBeReplaced()
				//  (not good) 	.canOcclude()
				set(xoff, zoff, blockGetter.getBlockState(blockPos).isViewBlocking(blockGetter, blockPos));
			}
		}
	}

	/**
	 * (0,0)中间
	 * <p>
	 * (1,1)右后方
	 * <p>
	 * (0,-1)正前方
	 */
	public boolean get (int xoff, int zoff) {
		xoff += 1;
		zoff += 1;
		return blockMap[xoff + zoff * 3];
	}

	private void set (int xoff, int zoff, boolean value) {
									xoff += 1;
									zoff += 1;
		blockMap[xoff + zoff * 3] = value;
	}

	public void reprint () {
		System.out.print("\r");
		System.out.printf("%c", blockMap[0] ? '#' : ' ');
		System.out.printf("%c", blockMap[1] ? '#' : ' ');
		System.out.printf("%c", blockMap[2] ? '#' : ' ');
		System.out.print("|");
		System.out.printf("%c", blockMap[3] ? '#' : ' ');
		System.out.printf("%c", blockMap[4] ? '#' : ' ');
		System.out.printf("%c", blockMap[5] ? '#' : ' ');
		System.out.print("|");
		System.out.printf("%c", blockMap[6] ? '#' : ' ');
		System.out.printf("%c", blockMap[7] ? '#' : ' ');
		System.out.printf("%c", blockMap[8] ? '#' : ' ');
	}

	public boolean match (Boolean[] map) {
		for (int i = 0; i < 9; i++) {
			if (map[i] != null && map[i] != blockMap[i]) {
				return false;
			}
		}
		return true;
	}
}
