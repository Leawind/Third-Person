package net.leawind.mc.thirdperson.impl.core;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.api.ModConstants;
import net.leawind.mc.thirdperson.api.core.EntityAgent;
import net.leawind.mc.thirdperson.api.core.rotation.SmoothType;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.ModReferee;
import net.leawind.mc.thirdperson.impl.config.Config;
import net.leawind.mc.thirdperson.impl.core.rotation.RotateTarget;
import net.leawind.mc.util.api.ItemPattern;
import net.leawind.mc.util.api.math.LMath;
import net.leawind.mc.util.api.math.vector.Vector2d;
import net.leawind.mc.util.api.math.vector.Vector3d;
import net.leawind.mc.util.math.decisionmap.api.DecisionFactorEnum;
import net.leawind.mc.util.math.decisionmap.api.DecisionMap;
import net.leawind.mc.util.math.smoothvalue.ExpSmoothRotation;
import net.leawind.mc.util.math.smoothvalue.ExpSmoothVector3d;
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
import java.util.function.BooleanSupplier;

/**
 * NOW dynamical smoothFactor for Rotation
 */
public class EntityAgentImpl implements EntityAgent {
	private final    Minecraft            minecraft;
	private final    ExpSmoothVector3d smoothEyePosition;
	private @NotNull RotateTarget      rotateTarget       = RotateTarget.NONE;
	private @NotNull SmoothType        smoothRotationType = SmoothType.EXP_LINEAR;
	private final    ExpSmoothRotation    smoothRotation       = ExpSmoothRotation.createWithHalflife(0.5);
	/**
	 * 在上一个 client tick 中的 isAiming() 的值
	 */
	private          boolean              wasAiming            = false;
	/**
	 * 上一个 client tick 中的 isInterecting 的值
	 */
	private          boolean              wasInterecting       = false;
	private final    DecisionMap<?>       rotateDecisionMap    = DecisionMap.of(RotateFactors.class);

	public EntityAgentImpl (@NotNull Minecraft minecraft) {
		this.minecraft    = minecraft;
		smoothEyePosition = new ExpSmoothVector3d();
		smoothEyePosition.setSmoothFactorWeight(ModConstants.EYE_POSITIOIN_SMOOTH_WEIGHT);
	}

	@Override
	public boolean isCameraEntityExist () {
		return minecraft.cameraEntity != null;
	}

	@Override
	public void reset () {
		ThirdPerson.lastPartialTick = minecraft.getFrameTime();
		smoothEyePosition.set(getRawEyePosition(ThirdPerson.lastPartialTick));
		wasAiming      = false;
		wasInterecting = false;
	}

	@Override
	public void setRotateStrategy (@NotNull RotateTarget rotateTarget) {
		this.rotateTarget = rotateTarget;
	}

	@PerformanceSensitive
	@Override
	public void onPreRender (double period, float partialTick) {
		if (!isControlled()) {
			return;
		}
		Vector2d targetRotation = rotateTarget.getRotation(partialTick);
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
		wasAiming      = isAiming();
		wasInterecting = isInterecting();
		updateRotateStrategy();
		smoothRotation.update(period);
		switch (smoothRotationType) {
			case HARD, EXP -> {
			}
			case LINEAR, EXP_LINEAR -> {
				smoothRotation.setTarget(rotateTarget.getRotation(1));
				smoothRotation.update(period);
			}
		}
	}

	/**
	 * 更新旋转策略、平滑类型、平滑系数
	 * <p>
	 * 根据配置、游泳、飞行、瞄准等状态判断。
	 */
	private void updateRotateStrategy () {
		Config config = ThirdPerson.getConfig();
		// 初始化默认值
		setRotateStrategy(config.rotate_to_moving_direction ? RotateTarget.HORIZONTAL_IMPULSE_DIRECTION: RotateTarget.NONE);
		smoothRotationType = SmoothType.EXP_LINEAR;
		smoothRotation.setHalflife(0.1);
		if (getRawCameraEntity().isSwimming()) {
			setRotateStrategy(RotateTarget.IMPULSE_DIRECTION);
			smoothRotationType = SmoothType.LINEAR;
			smoothRotation.setHalflife(0.01);
		} else if (isAiming() || ModReferee.doesPlayerWantToAim()) {
			setRotateStrategy(RotateTarget.CAMERA_HIT_RESULT);
			smoothRotationType = SmoothType.HARD;
		} else if (isFallFlying()) {
			setRotateStrategy(RotateTarget.CAMERA_ROTATION);
			smoothRotationType = SmoothType.LINEAR;
			smoothRotation.setHalflife(0);
		} else if (config.player_rotate_with_camera_when_not_aiming) {
			setRotateStrategy(RotateTarget.CAMERA_ROTATION);
			smoothRotationType = SmoothType.LINEAR;
			smoothRotation.setHalflife(1);
		} else if (config.auto_rotate_interacting && isInterecting()) {
			setRotateStrategy(config.rotate_interacting_type      //
							  ? RotateTarget.CAMERA_HIT_RESULT    //
							  : RotateTarget.CAMERA_ROTATION);
			smoothRotationType = SmoothType.LINEAR;
			smoothRotation.setHalflife(0);
		}
		updateBodyRotation();
	}

	private void updateBodyRotation () {
		// TODO 侧身拉弓
		Config config = ThirdPerson.getConfig();
		if (config.auto_turn_body_drawing_a_bow && CameraAgent.isControlledCamera()) {
			assert minecraft.player != null;
			if (minecraft.player.isUsingItem() && minecraft.player.getUseItem().is(Items.BOW)) {
				double k = minecraft.player.getUsedItemHand() == InteractionHand.MAIN_HAND ? 1: -1;
				if (minecraft.options.mainHand().get() == HumanoidArm.LEFT) {
					k = -k;
				}
				minecraft.player.yBodyRot = (float)(k * 45 + minecraft.player.getYRot());
			}
		}
	}

	/**
	 * 立即设置玩家朝向
	 * <p>
	 * 同时修改原始玩家实体的朝向和旧朝向
	 */
	private void setRawRotation (double y, double x) {
		Entity entity = getRawPlayerEntity();
		entity.setYRot(entity.yRotO = (float)y);
		entity.setXRot(entity.xRotO = (float)x);
	}

	private void setRawRotation (Vector2d rot) {
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
	public @NotNull Vector2d getRawRotation (float partialTick) {
		Entity entity = getRawCameraEntity();
		return Vector2d.of(entity.getViewXRot(partialTick), entity.getViewYRot(partialTick));
	}

	@Override
	public @NotNull Vector3d getSmoothEyePosition (float partialTick) {
		return smoothEyePosition.get(partialTick);
	}

	@Override
	public boolean isInterecting () {
		if (isControlled()) {
			Options options = minecraft.options;
			return options.keyUse.isDown() || options.keyAttack.isDown() || options.keyPickItem.isDown();
		} else {
			return getRawCameraEntity() instanceof LivingEntity livingEntity && livingEntity.isUsingItem();
		}
	}

	@Override
	public boolean isFallFlying () {
		return getRawCameraEntity() instanceof LivingEntity livingEntity && livingEntity.isFallFlying();
	}

	@Override
	public boolean isAiming () {
		Config config = ThirdPerson.getConfig();
		if (getRawCameraEntity() instanceof LivingEntity livingEntity) {
			if (livingEntity.isUsingItem()) {
				return ItemPattern.anyMatch(config.use_aim_item_patterns, livingEntity.getUseItem());
			} else {
				return ItemPattern.anyMatch(config.aim_item_patterns, livingEntity.getMainHandItem()) || //
					   ItemPattern.anyMatch(config.aim_item_patterns, livingEntity.getOffhandItem());
			}
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

	@SuppressWarnings("unused")
	public enum RotateFactors implements DecisionFactorEnum {
		ROTATE_TO_MOVING_DIRECTION(() -> ThirdPerson.getConfig().rotate_to_moving_direction),
		IS_SWIMMING(() -> ThirdPerson.ENTITY_AGENT.getRawCameraEntity().isSwimming()),
		IS_AIMING(() -> ThirdPerson.ENTITY_AGENT.isAiming() || ModReferee.doesPlayerWantToAim()),
		IS_FALL_FLYING(() -> ThirdPerson.ENTITY_AGENT.isFallFlying()),
		ROTATE_WITH_CAMERA_WHEN_NOT_AIMING(() -> ThirdPerson.getConfig().player_rotate_with_camera_when_not_aiming),
		ROTATE_INTERECTING(() -> ThirdPerson.getConfig().auto_rotate_interacting && ThirdPerson.ENTITY_AGENT.isInterecting()),
		;
		final @NotNull BooleanSupplier supplier;

		RotateFactors (@NotNull BooleanSupplier supplier) {
			this.supplier = supplier;
		}

		public @NotNull BooleanSupplier getSupplier () {
			return supplier;
		}
	}
}
