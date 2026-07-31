package io.github.leawind.thirdperson.internal.bridge.mixin.cameraentityopacity;

import io.github.leawind.thirdperson.internal.bridge.CameraEntityRenderState;
/*? if >=1.21 {*/
import net.minecraft.client.DeltaTracker;
/*? }*/
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// Clears the camera-entity render context before Minecraft extracts or renders the next frame.
@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
  /*? if >=26.2 {*/
  @Inject(method = "update", at = @At("HEAD"))
  private void beforeFrameExtraction(DeltaTracker deltaTracker, CallbackInfo ci) {
    CameraEntityRenderState.beginFrame();
  }
  /*? } else if >=1.21 {*/
  /*@Inject(method = "render", at = @At("HEAD"))
  private void beforeRender(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
    CameraEntityRenderState.beginFrame();
  }
  *//*? } else {*/
  /*@Inject(method = "render", at = @At("HEAD"))
  private void beforeRender(
      float partialTick, long frameStartNanos, boolean renderLevel, CallbackInfo ci) {
    CameraEntityRenderState.beginFrame();
  }
  *//*? }*/
}
