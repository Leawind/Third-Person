package net.leawind.mc.thirdperson.impl.core;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.api.ModConstants;
import net.leawind.mc.thirdperson.api.core.EntityAgent;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.ModReferee;
import net.leawind.mc.thirdperson.impl.config.Config;
import net.leawind.mc.thirdperson.impl.core.rotation.RotateStrategy;
import net.leawind.mc.util.api.ItemPattern;
import net.leawind.mc.util.api.math.vector.Vector2d;
import net.leawind.mc.util.api.math.vector.Vector3d;
import net.leawind.mc.util.api.math.LMath;
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

public class EntityAgentImpl implements EntityAgent {
	private final    Minecraft         minecraft;
	private final    ExpSmoothVector3d smoothEyePosition;
	/**
	 * DOITNOW
	 * <p>
	 * apply in preRender
	 */
	private @NotNull RotateStrategy    rotateStrategy = RotateStrategy.NONE;
	/**
	 * 在上一个 client tick 中的 isAiming() 的值
	 */
	private          boolean           wasAiming      = false;
	private          boolean           wasInterecting = false;

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
	public void setRotateStrategy (@NotNull RotateStrategy rotateStrategy) {
		this.rotateStrategy = rotateStrategy;
	}

	@PerformanceSensitive
	@Override
	public void onPreRender (double period, float partialTick) {
		Config config = ThirdPerson.getConfig();
		if (!isControlled()) {
			return;
		}
		// 更新旋转策略
		{
			if (isAiming() || ModReferee.doesPlayerWantToAim()) {
				setRotateStrategy(RotateStrategy.CAMERA_HIT_RESULT);
				updateBodyRotation();
			} else if (isFallFlying()) {
				setRotateStrategy(RotateStrategy.CAMERA_ROTATION);
			} else if (config.player_rotate_with_camera_when_not_aiming) {
				setRotateStrategy(RotateStrategy.CAMERA_ROTATION);
			} else if (config.auto_rotate_interacting && isInterecting()) {
				setRotateStrategy(config.rotate_interacting_type      //
								  ? RotateStrategy.CAMERA_HIT_RESULT    //
								  : RotateStrategy.CAMERA_ROTATION);
			}
		}
		// 设置玩家朝向
		{
			Vector2d targetRotation = rotateStrategy.getRotation(partialTick);
		}
	}

	private void updateBodyRotation () {
		// 侧身拉弓
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

	@Override
	public void onClientTickPre () {
		wasAiming      = isAiming();
		wasInterecting = isInterecting();
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
	public @NotNull Vector3d getRawPosition (float partialTick) throws NullPointerException {
		return LMath.toVector3d(Objects.requireNonNull(getRawCameraEntity()).getPosition(partialTick));
	}

	@Override
	public @NotNull Vector2d getRawRotation (float partialTick) {
		Entity entity = getRawCameraEntity();
		return Vector2d.of(entity.getViewYRot(partialTick), entity.getViewXRot(partialTick));
	}

	@Override
	public @NotNull Vector3d getSmoothEyePosition (float partialTick) {
		return smoothEyePosition.get(partialTick);
	}

	@Override
	public @NotNull Vector2d getRotation (float partialTick) {
		throw new RuntimeException("Method not implemented yet.");
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
}
