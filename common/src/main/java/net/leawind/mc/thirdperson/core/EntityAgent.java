package net.leawind.mc.thirdperson.core;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.ThirdPersonConstants;
import net.leawind.mc.thirdperson.ThirdPersonResources;
import net.leawind.mc.thirdperson.ThirdPersonStatus;
import net.leawind.mc.thirdperson.config.Config;
import net.leawind.mc.thirdperson.core.rotation.RotateStrategy;
import net.leawind.mc.thirdperson.core.rotation.RotateTargetEnum;
import net.leawind.mc.thirdperson.core.rotation.SmoothTypeEnum;
import net.leawind.mc.util.ItemPattern;
import net.leawind.mc.util.annotation.VersionSensitive;
import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.decisionmap.api.DecisionMap;
import net.leawind.mc.util.math.smoothvalue.ExpSmoothDouble;
import net.leawind.mc.util.math.smoothvalue.ExpSmoothRotation;
import net.leawind.mc.util.math.smoothvalue.ExpSmoothVector3d;
import net.leawind.mc.util.math.vector.Vector2d;
import net.leawind.mc.util.math.vector.Vector3d;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class EntityAgent {
	private final    Minecraft           minecraft;
	private final    ExpSmoothVector3d   smoothEyePosition;
	private final    ExpSmoothRotation   smoothRotation     = ExpSmoothRotation.createWithHalflife(0.5);
	private final    ExpSmoothDouble     smoothOpacity;
	/**
	 * @see RotateStrategy#build
	 */
	private final    DecisionMap<Double> rotateDecisionMap  = DecisionMap.of(RotateStrategy.class);
	private @NotNull RotateTargetEnum    rotateTarget       = RotateTargetEnum.DEFAULT;
	private @NotNull SmoothTypeEnum      smoothRotationType = SmoothTypeEnum.EXP_LINEAR;
	/**
	 * 在上一个 client tick 中的 isAiming() 的值
	 */
	private          boolean             wasAiming          = false;

	public EntityAgent (@NotNull Minecraft minecraft) {
		this.minecraft    = minecraft;
		smoothEyePosition = new ExpSmoothVector3d();
		smoothOpacity     = new ExpSmoothDouble();
		smoothOpacity.set(1d);
		ThirdPerson.LOGGER.debug(rotateDecisionMap.toString());
	}

	@Contract("_ -> new")
	public static @NotNull EntityAgent create (@NotNull Minecraft mc) {
		return new EntityAgent(mc);
	}

	/**
	 * 相机实体 {@link Minecraft#cameraEntity} 是否已经存在
	 */
	public boolean isCameraEntityExist () {
		return minecraft.cameraEntity != null;
	}

	/**
	 * 重置各种属性
	 * <p>
	 * 当初始化或进入第三人称时调用
	 */
	public void reset () {
		ThirdPerson.LOGGER.debug("Reset EntityAgent");
		ThirdPersonStatus.lastPartialTick = minecraft.getFrameTime();
		if (isCameraEntityExist()) {
			smoothEyePosition.set(getRawEyePosition(ThirdPersonStatus.lastPartialTick));
		}
		smoothOpacity.set(0d);
		wasAiming = false;
	}

	/**
	 * 设置旋转目标
	 */
	public void setRotateTarget (@NotNull RotateTargetEnum rotateTarget) {
		this.rotateTarget = rotateTarget;
	}

	@NotNull
	public RotateTargetEnum getRotateTarget () {
		return rotateTarget;
	}

	/**
	 * 设置平滑类型
	 * <p>
	 * 在 clientTick 和 renderTick 中要根据平滑类型采用不同的处理方式
	 */
	public void setRotationSmoothType (@NotNull SmoothTypeEnum smoothType) {
		smoothRotationType = smoothType;
	}

	public @NotNull SmoothTypeEnum getRotationSmoothType () {
		return smoothRotationType;
	}

	/**
	 * 设置平滑转向的半衰期
	 */
	@SuppressWarnings("unused")
	public void setSmoothRotationHalflife (double halflife) {
		smoothRotation.setHalflife(halflife);
	}

	/**
	 * 获取相机实体不透明度
	 */
	public float getSmoothOpacity () {
		return smoothOpacity.get(ThirdPersonStatus.lastPartialTick).floatValue();
	}

	/**
	 * @param period 相邻两次 render tick 的时间差，单位：s
	 */
	@SuppressWarnings("unused")
	@PerformanceSensitive
	public void onRenderTickStart (double now, double period, float partialTick) {
		if (ThirdPersonStatus.isRenderingInThirdPerson() && isControlled() && !ThirdPersonStatus.shouldCameraTurnWithEntity()) {
			Vector2d targetRotation = getRotateTarget().getRotation();
			smoothRotation.setTarget(targetRotation);
			switch (smoothRotationType) {
				case HARD -> setRawRotation(targetRotation);
				case LINEAR, EXP_LINEAR -> setRawRotation(smoothRotation.get(partialTick));
				case EXP -> {
					smoothRotation.update(period);
					setRawRotation(smoothRotation.get());
				}
			}
		}
	}

	/**
	 * 在 client tick 之前
	 * <p>
	 * 通常频率固定为 20Hz
	 */
	public void onClientTickStart () {
		final double period = 0.05;
		Config       config = ThirdPerson.getConfig();
		wasAiming = isAiming();
		config.getCameraOffsetScheme().setAiming(wasAiming());
		updateRotateStrategy();
		updateSmoothOpacity(period, 1);
		smoothRotation.update(period);
		{
			Vector3d eyePosition = getRawEyePosition(1);
			{
				final Vector3d halflife;
				if (minecraft.options.getCameraType() == CameraType.FIRST_PERSON) {
					halflife = Vector3d.of(0);
				} else if (isFallFlying()) {
					halflife = Vector3d.of(config.flying_smooth_halflife);
				} else {
					halflife = config.getCameraOffsetScheme().getMode().getEyeSmoothHalflife();
				}
				final double dist = getSmoothEyePosition(1).distance(ThirdPerson.CAMERA_AGENT.getRawCameraPosition());
				halflife.mul(dist * ThirdPersonConstants.EYE_HALFLIFE_MULTIPLIER);
				smoothEyePosition.setHalflife(halflife);
			}
			smoothEyePosition.setTarget(eyePosition);
			smoothEyePosition.update(period);
		}
		switch (smoothRotationType) {
			case HARD, EXP -> {
			}
			case LINEAR, EXP_LINEAR -> {
				smoothRotation.setTarget(getRotateTarget().getRotation());
				smoothRotation.update(period);
			}
		}
	}

	/**
	 * 设置实体朝向
	 */
	public void setRawRotation (@NotNull Vector2d rot) {
		assert rot.isFinite();
		Entity entity = getRawPlayerEntity();
		entity.setYRot(entity.yRotO = (float)rot.y());
		entity.setXRot(entity.xRotO = (float)rot.x());
	}

	/**
	 * 玩家当前是否在操控这个实体
	 */
	public boolean isControlled () {
		return getRawPlayerEntity() == minecraft.cameraEntity;
	}

	/**
	 * 获取相机附着的实体
	 *
	 * @see EntityAgent#isCameraEntityExist
	 */
	public @NotNull Entity getRawCameraEntity () {
		return Objects.requireNonNull(minecraft.cameraEntity);
	}

	/**
	 * 获取玩家实体
	 */
	public @NotNull LocalPlayer getRawPlayerEntity () {
		return Objects.requireNonNull(minecraft.player);
	}

	/**
	 * 直接从相机实体获取眼睛坐标
	 */
	public @NotNull Vector3d getRawEyePosition (float partialTick) {
		return LMath.toVector3d(getRawCameraEntity().getEyePosition(partialTick));
	}

	/**
	 * 直接从实体获取朝向
	 */
	@VersionSensitive
	public @NotNull Vector2d getRawRotation (float partialTick) {
		Entity entity = getRawCameraEntity();
		return Vector2d.of(entity.getViewXRot(partialTick), entity.getViewYRot(partialTick));
	}

	/**
	 * 获取平滑的眼睛坐标
	 */
	public @NotNull Vector3d getSmoothEyePosition (float partialTick) {
		return smoothEyePosition.get(partialTick);
	}

	/**
	 * 如果平滑系数为0，则返回完全不平滑的值
	 * <p>
	 * 如果平滑系数不为0，则采用 EXP_LINEAR 平滑
	 */
	public @NotNull Vector3d getPossibleSmoothEyePosition (float partialTick) {
		Vector3d smoothEyePositionValue = smoothEyePosition.get(partialTick);
		Vector3d rawEyePosition         = LMath.toVector3d(getRawCameraEntity().getEyePosition(partialTick));
		Vector3d smoothFactor           = smoothEyePosition.smoothFactor.copy();
		boolean  isHorizontalZero       = smoothFactor.x() * smoothFactor.z() == 0;
		boolean  isVerticalZero         = smoothFactor.y() == 0;
		if (isHorizontalZero || isVerticalZero) {
			smoothEyePositionValue = Vector3d.of(isHorizontalZero ? rawEyePosition.x(): smoothEyePositionValue.x(),//
												 isVerticalZero ? rawEyePosition.y(): smoothEyePositionValue.y(),//
												 isHorizontalZero ? rawEyePosition.z(): smoothEyePositionValue.z());
		}
		return smoothEyePositionValue;
	}

	/**
	 * 实体的眼睛是否在墙里
	 * <p>
	 * 与{@link Entity#isInWall()}不同的是，旁观者模式下此方法仍然可以返回true
	 */
	public boolean isEyeInWall (@NotNull ClipContext.ShapeGetter shapeGetter) {
		final Entity   cameraEntity = getRawCameraEntity();
		Vec3           eyePos       = cameraEntity.getEyePosition();
		final BlockPos blockPos     = BlockPos.containing(eyePos.x(), eyePos.y(), eyePos.z());
		final AABB     eyeAabb      = AABB.ofSize(eyePos, 0.8, 1e-6, 0.8);
		BlockState     blockState   = cameraEntity.level().getBlockState(blockPos);
		return shapeGetter.get(blockState, cameraEntity.level(), blockPos, CollisionContext.empty()).toAabbs().stream().anyMatch(a -> a.move(blockPos).intersects(eyeAabb));
	}

	/**
	 * 实体是否在交互
	 * <p>
	 * 当控制玩家时，相当于是否按下了 使用|攻击|选取 键
	 * <p>
	 * 当附身其他实体时，另做判断
	 */
	@VersionSensitive
	public boolean isInterecting () {
		if (!isControlled()) {
			return getRawCameraEntity() instanceof LivingEntity livingEntity && livingEntity.isUsingItem();
		}
		Options options = minecraft.options;
		return options.keyUse.isDown() || options.keyAttack.isDown() || options.keyPickItem.isDown();
	}

	/**
	 * 实体是否在飞行
	 */
	@VersionSensitive
	public boolean isFallFlying () {
		return getRawCameraEntity() instanceof LivingEntity livingEntity && livingEntity.isFallFlying();
	}

	/**
	 * 实体是否在奔跑
	 */
	public boolean isSprinting () {
		return getRawCameraEntity().isSprinting();
	}

	/**
	 * 正在吃食物
	 * <p>
	 * 使用 {@link ItemStack#isEdible()} 判断是否是食物
	 */
	public boolean isEating () {
		if (getRawCameraEntity() instanceof LivingEntity livingEntity) {
			return livingEntity.getUseItem().isEdible();
		}
		return false;
	}

	/**
	 * 根据以下因素判断是否在瞄准
	 * <li>是否在使用物品</li>
	 * <li>实体拿着的物品</li>
	 * <li>按键</li>
	 * <li>使用物品时正在播放的动画</li>
	 */
	public boolean isAiming () {
		Config config = ThirdPerson.getConfig();
		if (ThirdPersonStatus.doesPlayerWantToAim()) {
			return true;
		}
		if (getRawCameraEntity() instanceof LivingEntity livingEntity) {
			if (config.determine_aim_mode_by_animation && livingEntity.isUsingItem()) {
				UseAnim anim = livingEntity.getUseItem().getUseAnimation();
				if (anim == UseAnim.BOW || anim == UseAnim.SPEAR) {
					return true;
				}
			}
			boolean shouldBeAiming = ItemPattern.anyMatch(livingEntity.getMainHandItem(), config.getHoldToAimItemPatterns(), ThirdPersonResources.itemPatternManager.holdToAimItemPatterns) || //
									 ItemPattern.anyMatch(livingEntity.getOffhandItem(), config.getHoldToAimItemPatterns(), ThirdPersonResources.itemPatternManager.holdToAimItemPatterns);
			if (livingEntity.isUsingItem()) {
				shouldBeAiming |= ItemPattern.anyMatch(livingEntity.getUseItem(), config.getUseToAimItemPatterns(), ThirdPersonResources.itemPatternManager.useToAimItemPatterns);
			}
			return shouldBeAiming;
		}
		return false;
	}

	/**
	 * 在上一个 clientTick 中是否在瞄准
	 */
	public boolean wasAiming () {
		return wasAiming;
	}

	public AABB getBoundingBox (float partialTick) {
		Entity entity = this.getRawCameraEntity();
		return entity.getDimensions(entity.getPose()).makeBoundingBox(entity.getPosition(partialTick));
	}

	/**
	 * 计算点到碰撞箱的距离。如果点在碰撞箱内，则返回0
	 */
	public double boxDistanceTo (@NotNull Vector3d target, float partialTick) {
		AABB     aabb = getBoundingBox(partialTick);
		Vector3d c    = Vector3d.of();
		c.x(LMath.clamp(target.x(), aabb.minX, aabb.maxX));
		c.y(LMath.clamp(target.y(), aabb.minY, aabb.maxY));
		c.z(LMath.clamp(target.z(), aabb.minZ, aabb.maxZ));
		return c.distance(target);
	}

	public double getBodyRadius () {
		return getRawCameraEntity().getBbWidth() * 0.8660254037844386; // 0.5 * sqrt(3)
	}
	/**
	 * 更新旋转策略、平滑类型、平滑系数
	 * <p>
	 * 根据配置、游泳、飞行、瞄准等状态判断。
	 */
	private void updateRotateStrategy () {
		setSmoothRotationHalflife(rotateDecisionMap.remake());
		updateBodyRotation();
	}

	/**
	 * 脖子最多左右转85度
	 *
	 * @see net.minecraft.client.renderer.entity.LivingEntityRenderer#render
	 */
	private void updateBodyRotation () {
		// net.minecraft.client.renderer.entity.LivingEntityRenderer.render
		Config config = ThirdPerson.getConfig();
		if (config.auto_turn_body_drawing_a_bow && ThirdPerson.ENTITY_AGENT.isControlled()) {
			LocalPlayer player = getRawPlayerEntity();
			if (player.isUsingItem() && player.getUseItem().is(Items.BOW)) {
				double k = player.getUsedItemHand() == InteractionHand.MAIN_HAND ? 1: -1;
				if (minecraft.options.mainHand().get() == HumanoidArm.LEFT) {
					k = -k;
				}
				player.yBodyRot = (float)(k * 45 + player.getYRot());
			}
		}
	}

	/**
	 * 更新平滑的透明度
	 */
	private void updateSmoothOpacity (double period, float partialTick) {
		double targetOpacity = 1.0;
		Config config        = ThirdPerson.getConfig();
		if (config.player_fade_out_enabled) {
			final double C              = ThirdPersonConstants.CAMERA_THROUGH_WALL_DETECTION * 2;
			Vector3d     cameraPosition = LMath.toVector3d(ThirdPerson.CAMERA_AGENT.getRawCamera().getPosition());
			final double distance       = getRawEyePosition(partialTick).distance(cameraPosition);
			targetOpacity = (distance - C) / (1 - C);
			assert !Double.isNaN(targetOpacity);
			if (targetOpacity > config.gaze_opacity && !isFallFlying() && ThirdPerson.CAMERA_AGENT.isLookingAt(getRawCameraEntity())) {
				targetOpacity = config.gaze_opacity;
			}
		}
		smoothOpacity.setTarget(LMath.clamp(targetOpacity, 0, 1));
		smoothOpacity.setHalflife(ThirdPersonConstants.OPACITY_HALFLIFE * (wasAiming() ? 0.25: 1));
		smoothOpacity.update(period);
	}

	/**
	 * 当相机在身后时，兴趣点是准星指向的目标点
	 * <p>
	 * 当相机在面前时，兴趣点是相机
	 */
	public Optional<Vec3> getInterestPoint () {
		if (LMath.subtractDegrees(getRawPlayerEntity().yBodyRot, ThirdPerson.CAMERA_AGENT.getRelativeRotation().y()) > 90) {
			return Optional.of(ThirdPerson.CAMERA_AGENT.getHitResult().getLocation());
		} else {
			return Optional.of(LMath.toVec3(ThirdPerson.CAMERA_AGENT.getRawCameraPosition()));
		}
	}
}
