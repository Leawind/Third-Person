package io.github.leawind.thirdperson.internal.bridge.mixin.cameraentityopacity;

import io.github.leawind.thirdperson.internal.bridge.CameraEntityRenderState;
/*? if >=1.21.11 {*/
import net.minecraft.client.renderer.SubmitNodeCollection;
import org.spongepowered.asm.mixin.Mixin;
/*? } else {*/
/*import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
*//*? }*/
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/*? if >=1.21.11 {*/
@Mixin(SubmitNodeCollection.class)
/*? } else {*/
/*@Pseudo
@Mixin(targets = "net.minecraft.client.renderer.SubmitNodeCollection")
*//*? }*/
abstract class SubmitNodeCollectionMixin {
  /*? if >=1.21.11 {*/
  @ModifyVariable(method = "submitModel", at = @At("HEAD"), index = 7, argsOnly = true)
  private int applyCameraEntityOpacity(int color) {
    return CameraEntityRenderState.applyOpacity(color);
  }

  /*? if <26.2 {*/
  @ModifyVariable(
      method = "submitModelPart",
      at = @At("HEAD"),
      index = 9,
      argsOnly = true,
      require = 0)
  private int applyCameraEntityOpacityToModelPart(int color) {
    return CameraEntityRenderState.applyOpacity(color);
  }
  /*? }*/
  /*? }*/
}
