package io.github.leawind.thirdperson.internal.bridge.mixin.cameraentityopacity;

import io.github.leawind.thirdperson.internal.bridge.CameraEntityRenderState;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
/*? if >=1.21.11 {*/
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
/*? } else {*/
/*import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
*//*? }*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CapeLayer.class, priority = 500)
abstract class CapeLayerMixin {
  /*? if >=1.21.11 {*/
  @Redirect(
      method = "submit",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/renderer/rendertype/RenderTypes;entitySolid(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"),
      require = 0)
  private RenderType useTranslucentCameraEntityCape(Identifier texture) {
    return CameraEntityRenderState.isApplyingTransparency()
        ? RenderTypes.entityTranslucent(texture)
        : RenderTypes.entitySolid(texture);
  }
  /*? } else {*/
  /*@Redirect(
      method = "render",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/renderer/RenderType;entitySolid(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"),
      require = 0)
  private RenderType useTranslucentCameraEntityCape(ResourceLocation texture) {
    return CameraEntityRenderState.isApplyingTransparency()
        ? RenderType.entityTranslucent(texture)
        : RenderType.entitySolid(texture);
  }
  *//*? }*/
}
