package io.github.leawind.thirdperson.internal.bridge.mixin.cameraentityopacity;

import io.github.leawind.thirdperson.internal.bridge.CameraEntityRenderState;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntityRenderer.class)
abstract class LivingEntityRendererMixin {
  /// Reuses vanilla's force-transparent branch so the entity body is submitted to a blending
  /// RenderType before its vertex/model color receives the smoothed alpha.
  @ModifyVariable(method = "getRenderType", at = @At("HEAD"), argsOnly = true, ordinal = 1)
  private boolean forceCameraEntityTransparency(boolean forceTransparent) {
    return forceTransparent || CameraEntityRenderState.isApplyingTransparency();
  }
}
