package net.leawind.mc.thirdperson.impl.core;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.ThirdPersonConstants;
import net.leawind.mc.thirdperson.ThirdPersonResources;
import net.leawind.mc.thirdperson.ThirdPersonStatus;
import net.leawind.mc.thirdperson.api.config.Config;
import net.leawind.mc.thirdperson.api.core.EntityAgent;
import net.leawind.mc.thirdperson.api.core.rotation.SmoothType;
import net.leawind.mc.thirdperson.impl.core.rotation.RotateStrategy;
import net.leawind.mc.thirdperson.impl.core.rotation.RotateTarget;
import net.leawind.mc.util.annotations.VersionSensitive;
import net.leawind.mc.util.itempattern.ItemPattern;
import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.decisionmap.api.DecisionMap;
import net.leawind.mc.util.math.smoothvalue.ExpSmoothDouble;
import net.leawind.mc.util.math.smoothvalue.ExpSmoothRotation;
import net.leawind.mc.util.math.smoothvalue.ExpSmoothVector3d;
import net.leawind.mc.util.math.vector.api.Vector2d;
import net.leawind.mc.util.math.vector.api.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EntityAgentImpl implements EntityAgent {
	private final    Minecraft           minecraft;
	private final    ExpSmoothVector3d   smoothEyePosition;
	private final    ExpSmoothRotation   smoothRotation     = ExpSmoothRotation.createWithHalflife(0.5);
	private final    ExpSmoothDouble     smoothOpacity;
	private final    DecisionMap<Double> rotateDecisionMap  = DecisionMap.of(RotateStrategy.class);
	private @NotNull RotateTarget        rotateTarget       = RotateTarget.NONE;
	private @NotNull SmoothType          smoothRotationType = SmoothType.EXP_LINEAR;
	/**
	 * 在上一个 client tick 中的 isAiming() 的值
	 */
	private          boolean             wasAiming          = false;
	/**
	 * 上一个 client tick 中的 isInterecting 的值
	 */
	private          boolean             wasInterecting     = false;

	public EntityAgentImpl (@NotNull Minecraft minecraft) {
		this.minecraft = minecraft;
		{
			smoothEyePosition = new ExpSmoothVector3d();
			smoothEyePosition.setSmoothFactorWeight(ThirdPersonConstants.EYE_POSITIOIN_SMOOTH_WEIGHT);
		}
		{
			smoothOpacity = new ExpSmoothDouble();
			smoothOpacity.set(1d);
		}
		ThirdPerson.LOGGER.debug(rotateDecisionMap.toString());
	}

	@Override
	public boolean isCameraEntityExist () {
		return minecraft.cameraEntity != null;
	}

	@Override
	public void reset () {
		ThirdPerson.LOGGER.debug("Reset EntityAgent");
		ThirdPersonStatus.lastPartialTick = minecraft.getFrameTime();
		if (isCameraEntityExist()) {
			smoothEyePosition.set(getRawEyePosition(ThirdPersonStatus.lastPartialTick));
		}
		smoothOpacity.set(0d);
		wasAiming      = false;
		wasInterecting = false;
	}

	@Override
	public void setRotateTarget (@NotNull RotateTarget rotateTarget) {
		this.rotateTarget = rotateTarget;
	}

	@Override
	public void setRotationSmoothType (@NotNull SmoothType smoothType) {
		smoothRotationType = smoothType;
	}

	@Override
	public void setSmoothRotationHalflife (double halflife) {
		smoothRotation.setHalflife(halflife);
	}

	@Override
	public float getSmoothOpacity () {
		return smoothOpacity.get(ThirdPersonStatus.lastPartialTick).floatValue();
	}

	@PerformanceSensitive
	@Override
	public void onRenderTickPre (double now, double period, float partialTick) {
		if (!isControlled()) {
			return;
		}
		Vector2d targetRotation = rotateTarget.getRotation();
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

	@Override
	public void onClientTickPre () {
		final double period = 0.05;
		Config       config = ThirdPerson.getConfig();
		config.getCameraOffsetScheme().setAiming(ThirdPerson.ENTITY_AGENT.wasAiming());
		wasAiming      = isAiming();
		wasInterecting = isInterecting();
		updateRotateStrategy();
		updateSmoothOpacity(period, 1);
		smoothRotation.update(period);
		{
			Vector3d eyePosition = getRawEyePosition(1);
			if (ThirdPerson.CAMERA_AGENT.isTransitioningToFirstPerson()) {
				smoothEyePosition.setSmoothFactor(0);
			} else if (isFallFlying()) {
				smoothEyePosition.setSmoothFactor(config.flying_smooth_factor);
			} else {
				config.getCameraOffsetScheme().getMode().getEyeSmoothFactor(smoothEyePosition.smoothFactor);
			}
			smoothEyePosition.setTarget(eyePosition);
			smoothEyePosition.update(period);
		}
		switch (smoothRotationType) {
			case HARD, EXP -> {
			}
			case LINEAR, EXP_LINEAR -> {
				smoothRotation.setTarget(rotateTarget.getRotation());
				smoothRotation.update(period);
			}
		}
	}

	/**
	 * 立即设置玩家朝向
	 * <p>
	 * 同时修改原始玩家实体的朝向和旧朝向
	 */
	@Override
	public void setRawRotation (@NotNull Vector2d rot) {
		Entity entity = getRawPlayerEntity();
		entity.setYRot(entity.yRotO = (float)rot.y());
		entity.setXRot(entity.xRotO = (float)rot.x());
	}

	@Override
	public boolean isControlled () {
		return getRawPlayerEntity() == getRawCameraEntity();
	}

	@Override
	public @NotNull Entity getRawCameraEntity () {
		return Objects.requireNonNull(minecraft.cameraEntity);
	}

	@Override
	public @NotNull LocalPlayer getRawPlayerEntity () {
		return Objects.requireNonNull(minecraft.player);
	}

	@Override
	public @NotNull Vector3d getRawEyePosition (float partialTick) {
		return LMath.toVector3d(getRawCameraEntity().getEyePosition(partialTick));
	}

	@Override
	public @NotNull Vector3d getRawPosition (float partialTick) {
		return LMath.toVector3d(Objects.requireNonNull(getRawCameraEntity()).getPosition(partialTick));
	}

	@Override
	@VersionSensitive
	public @NotNull Vector2d getRawRotation (float partialTick) {
		Entity entity = getRawCameraEntity();
		return Vector2d.of(entity.getViewXRot(partialTick), entity.getViewYRot(partialTick));
	}

	@Override
	public @NotNull Vector3d getSmoothEyePosition (float partialTick) {
		return smoothEyePosition.get(partialTick);
	}

	@Override
	public @NotNull Vector3d getPossiblySmoothEyePosition (float partialTick) {
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

	@Override
	@VersionSensitive
	public boolean isInterecting () {
		if (isControlled()) {
			Options options = minecraft.options;
			return options.keyUse.isDown() || options.keyAttack.isDown() || options.keyPickItem.isDown();
		} else {
			return getRawCameraEntity() instanceof LivingEntity livingEntity && livingEntity.isUsingItem();
		}
	}

	@Override
	@VersionSensitive
	public boolean isFallFlying () {
		return getRawCameraEntity() instanceof LivingEntity livingEntity && livingEntity.isFallFlying();
	}

	@Override
	public boolean isSprinting () {
		return getRawCameraEntity().isSprinting();
	}

	@Override
	public boolean isAiming () {
		Config config = ThirdPerson.getConfig();
		if (getRawCameraEntity() instanceof LivingEntity livingEntity) {
			boolean shouldBeAiming = ItemPattern.anyMatch(livingEntity.getMainHandItem(), config.getHoldToAimItemPatterns(), ThirdPersonResources.itemPatternManager.holdToAimItemPatterns) || //
									 ItemPattern.anyMatch(livingEntity.getOffhandItem(), config.getHoldToAimItemPatterns(), ThirdPersonResources.itemPatternManager.holdToAimItemPatterns);
			if (livingEntity.isUsingItem()) {
				shouldBeAiming |= ItemPattern.anyMatch(livingEntity.getUseItem(), config.getUseToAimItemPatterns(), ThirdPersonResources.itemPatternManager.useToAimItemPatterns);
			}
			return shouldBeAiming;
		}
		return false;
	}

	@Override
	public boolean wasAiming () {
		return wasAiming;
	}

	@Override
	public boolean wasInterecting () {
		return wasInterecting;
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
		if (ThirdPerson.getConfig().player_fade_out_enabled) {
			final double C              = ThirdPersonConstants.CAMERA_THROUGH_WALL_DETECTION * 2;
			Vector3d     cameraPosition = LMath.toVector3d(ThirdPerson.CAMERA_AGENT.getRawCamera().getPosition());
			final double distance       = getRawEyePosition(partialTick).distance(cameraPosition);
			targetOpacity = (distance - C) / (1 - C);
			if (targetOpacity > ThirdPersonConstants.GAZE_OPACITY && ThirdPerson.CAMERA_AGENT.isLookingAt(getRawCameraEntity())) {
				targetOpacity = ThirdPersonConstants.GAZE_OPACITY;
			}
		}
		smoothOpacity.setTarget(LMath.clamp(targetOpacity, 0, 1));
		smoothOpacity.setHalflife(ThirdPersonConstants.OPACITY_HALFLIFE * (wasAiming ? 0.25: 1));
		smoothOpacity.update(period);
	}
}
