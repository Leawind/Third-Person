package com.github.leawind.thirdperson.mixin;


import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value=ModelPart.Cube.class, priority=2000)
public class ModelPartCubeMixin {
	@ModifyVariable(at=@At("HEAD"), method="compile", index=8, argsOnly=true)
	private float compile (float opacity) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && ThirdPersonStatus.useCameraEntityOpacity()) {
			return Math.min(opacity, ThirdPerson.ENTITY_AGENT.getSmoothOpacity());
		}
		return opacity;
	}
}
