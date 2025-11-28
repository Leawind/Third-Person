package com.github.leawind.thirdperson.mixin;

import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ModelPart.Cube.class, priority = 2000)
public class ModelPartCubeMixin {
  @ModifyVariable(
      at = @At("HEAD"),
      method =
          "compile(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
      index = 5,
      argsOnly = true)
  private int compile(int argb) {
    float opacity = ARGB.alpha(argb) / 255f;

    float partialTick = (float) (Minecraft.getInstance().getFrameTimeNs() / Util.NANOS_PER_MILLI);
    if (ThirdPerson.isAvailable()
        && ThirdPersonStatus.isRenderingInThirdPerson()
        && ThirdPersonStatus.useCameraEntityOpacity(partialTick)) {
      opacity = Math.min(opacity, ThirdPerson.ENTITY_AGENT.getSmoothOpacity(partialTick));
    }

    return ARGB.color(ARGB.as8BitChannel(opacity), argb);
  }
}
