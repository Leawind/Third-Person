package net.leawind.mc.thirdperson;


import com.mojang.blaze3d.vertex.PoseStack;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.thirdperson.core.ModOptions;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin 方法不支持热更新，所以需要这个类来调试
 */
public class MixinProxy {
	public static void entity_render (LivingEntityRenderer that,
									  LivingEntity entity,
									  float f,
									  float g,
									  PoseStack poseStack,
									  MultiBufferSource multiBufferSource,
									  int i,
									  CallbackInfo ci) {
		if (entity == CameraAgent.attachedEntity) {
//			CameraAgent.wasAttachedEntityInvisible = ModOptions.isAttachedEntityInvisible();
			if (CameraAgent.wasAttachedEntityInvisible) {
				ci.cancel();
			}
		}
	}
}
