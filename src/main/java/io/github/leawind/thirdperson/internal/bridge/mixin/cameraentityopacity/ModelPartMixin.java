package io.github.leawind.thirdperson.internal.bridge.mixin.cameraentityopacity;

import io.github.leawind.thirdperson.internal.bridge.CameraEntityRenderState;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
/*? if <1.21.11 {*/
/*import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
*//*? }*/

@Mixin(value = ModelPart.class, priority = 500)
abstract class ModelPartMixin {
  /*? if <1.21 {*/
  /*@ModifyVariable(
      method =
          "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
      at = @At("HEAD"),
      index = 8,
      argsOnly = true)
  private float applyCameraEntityOpacity(float alpha) {
    return CameraEntityRenderState.applyOpacity(alpha);
  }
  *//*? } else if <1.21.11 {*/
  /*@ModifyVariable(
      method =
          "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V",
      at = @At("HEAD"),
      index = 5,
      argsOnly = true)
  private int applyCameraEntityOpacity(int color) {
    return CameraEntityRenderState.applyOpacity(color);
  }
  *//*? }*/
}
