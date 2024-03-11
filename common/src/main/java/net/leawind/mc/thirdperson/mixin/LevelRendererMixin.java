package net.leawind.mc.thirdperson.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.ThirdPersonStatus;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=LevelRenderer.class, priority=2000)
public class LevelRendererMixin {
	/**
	 * 如果不透明度足够低则取消渲染实体
	 */
	@Inject(method="renderEntity", at=@At("HEAD"), cancellable=true)
	public void renderEntity_head (Entity entity, double x, double y, double z, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, CallbackInfo ci) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && entity == ThirdPerson.ENTITY_AGENT.getRawCameraEntity()) {
			if (!ThirdPersonStatus.shouldRenderCameraEntity()) {
				ci.cancel();
			}
		}
	}

	@Inject(method="renderEntity", at=@At("TAIL"))
	public void renderEntity_tail (Entity entity, double x, double y, double z, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, CallbackInfo ci) {
		if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson() && entity == ThirdPerson.ENTITY_AGENT.getRawCameraEntity()) {
			if (ThirdPersonStatus.shouldRenderCameraEntity()) {
				((MultiBufferSource.BufferSource)multiBufferSource).endLastBatch();
			}
		}
	}
}
