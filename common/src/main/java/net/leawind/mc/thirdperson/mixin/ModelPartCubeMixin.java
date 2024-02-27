package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value=ModelPart.Cube.class, priority=2000)
public class ModelPartCubeMixin {
	@ModifyVariable(at=@At("HEAD"), method="compile", index=8, argsOnly=true)
	public float compile (float opacity) {
		return Math.min(opacity, ThirdPerson.ENTITY_AGENT.getSmoothOpacity());
	}
}
