package net.leawind.mc.thirdperson.mixin;


import com.mojang.blaze3d.vertex.PoseStack;
import net.leawind.mc.thirdperson.ThirdPerson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=LivingEntityRenderer.class, priority=2000)
public class LivingEntityRendererMixin {
	/**
	 * 注入到 render 的开头
	 * <p>
	 * 立即返回，即可阻止该实体的渲染，实现隐藏玩家实体。
	 */
	@Inject(method="render*", at=@At(value="HEAD"), cancellable=true)
	public void render_head (LivingEntity entity, float f, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
		if (entity == ThirdPerson.ENTITY_AGENT.getRawCameraEntity()) {
			if (ThirdPerson.CAMERA_AGENT.wasCameraCloseToEntity()) {
				ci.cancel();
			}
		}
	}
}
