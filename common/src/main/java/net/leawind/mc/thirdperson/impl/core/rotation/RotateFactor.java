package net.leawind.mc.thirdperson.impl.core.rotation;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.core.ModReferee;
import net.leawind.mc.util.math.decisionmap.api.DecisionFactorEnum;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

@SuppressWarnings("unused")
public enum RotateFactor implements DecisionFactorEnum {
	ROTATE_TO_MOVING_DIRECTION(() -> ThirdPerson.getConfig().rotate_to_moving_direction),
	IS_SWIMMING(() -> ThirdPerson.ENTITY_AGENT.getRawCameraEntity().isSwimming()),
	IS_AIMING(() -> ThirdPerson.ENTITY_AGENT.isAiming() || ModReferee.doesPlayerWantToAim()),
	IS_FALL_FLYING(() -> ThirdPerson.ENTITY_AGENT.isFallFlying()),
	ROTATE_WITH_CAMERA_WHEN_NOT_AIMING(() -> ThirdPerson.getConfig().player_rotate_with_camera_when_not_aiming),
	ROTATE_INTERECTING(() -> ThirdPerson.getConfig().auto_rotate_interacting && ThirdPerson.ENTITY_AGENT.isInterecting()),
	;
	final @NotNull BooleanSupplier supplier;

	RotateFactor (@NotNull BooleanSupplier supplier) {
		this.supplier = supplier;
	}

	public @NotNull BooleanSupplier getSupplier () {
		return supplier;
	}
}
