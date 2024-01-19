package net.leawind.mc.thirdperson.impl.core;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.api.ModConstants;
import net.leawind.mc.thirdperson.api.core.EntityAgent;
import net.leawind.mc.thirdperson.core.ModReferee;
import net.leawind.mc.thirdperson.impl.config.Config;
import net.leawind.mc.thirdperson.impl.core.rotation.RotateTarget;
import net.leawind.mc.util.api.ItemPattern;
import net.leawind.mc.util.api.math.vector.Vector2d;
import net.leawind.mc.util.api.math.vector.Vector3d;
import net.leawind.mc.util.math.LMath;
import net.leawind.mc.util.math.smoothvalue.ExpSmoothVector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EntityAgentImpl implements EntityAgent {
	private final    Minecraft         minecraft;
	private final    ExpSmoothVector3d smoothEyePosition;
	private @NotNull RotateTarget      rotateTarget = RotateTarget.NONE;
	/**
	 * 在上一个 client tick 中的 isAiming() 的值
	 */
	private          boolean           wasAiming    = false;

	public EntityAgentImpl (Minecraft minecraft) {
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
	}

	@Override
	public void onPreRender (double period, float partialTick) {
		Config config = ThirdPerson.getConfig();
		if (!isControlled()) {
			return;
		}
		if (isAiming() || ModReferee.doesPlayerWantToAim()) {
			rotateTarget = RotateTarget.CAMERA_HIT_RESULT;
		} else if (isFallFlying()) {
			rotateTarget = RotateTarget.CAMERA_ROTATION;
		} else if (config.player_rotate_with_camera_when_not_aiming) {
			rotateTarget = RotateTarget.CAMERA_ROTATION;
		} else if (config.auto_rotate_interacting && isInterecting()) {
			rotateTarget = config.rotate_interacting_type      //
						   ? RotateTarget.CAMERA_HIT_RESULT    //
						   : RotateTarget.CAMERA_ROTATION;
		}
	}

	@Override
	public void onClientTickPre () {
		wasAiming = isAiming();
		throw new RuntimeException("Method not implemented yet.");
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
}
