package io.github.leawind.thirdperson.internal.bridge.mixin.cameraentityopacity;

import io.github.leawind.thirdperson.internal.bridge.CameraEntityRenderState;
/*? if >=1.21.11 {*/
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
/*? } else {*/
/*import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
*//*? }*/
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*? if >=1.21.11 {*/
@Mixin(RenderTypes.class)
/*? } else {*/
/*@Pseudo
@Mixin(targets = "net.minecraft.client.renderer.rendertype.RenderTypes")
*//*? }*/
abstract class RenderTypesMixin {
  /*? if >=1.21.11 {*/
  @Inject(method = "armorCutoutNoCull", at = @At("HEAD"), cancellable = true)
  private static void useTranslucentCameraEntityArmor(
      Identifier texture, CallbackInfoReturnable<RenderType> ci) {
    if (CameraEntityRenderState.isApplyingTransparency()) {
      ci.setReturnValue(RenderTypes.armorTranslucent(texture));
    }
  }
  /*? }*/
}
