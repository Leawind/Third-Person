package io.github.leawind.thirdperson.internal.bridge.mixin.cameraentityopacity;

import io.github.leawind.thirdperson.internal.bridge.CameraEntityRenderState;
/*? if <1.21.11 {*/
/*import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
*//*? } else {*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
/*? }*/
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*? if <1.21.11 {*/
/*@Mixin(RenderType.class)
*//*? } else {*/
@Pseudo
@Mixin(targets = "net.minecraft.client.renderer.RenderType")
/*? }*/
abstract class RenderTypeMixin {
  /*? if <1.21.11 {*/
  /*@Inject(method = "armorCutoutNoCull", at = @At("HEAD"), cancellable = true)
  private static void useTranslucentCameraEntityArmor(
      ResourceLocation texture, CallbackInfoReturnable<RenderType> ci) {
    if (CameraEntityRenderState.isApplyingTransparency()) {
      ci.setReturnValue(RenderType.entityTranslucent(texture));
    }
  }
  *//*? }*/
}
