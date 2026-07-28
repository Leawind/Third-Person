package io.github.leawind.thirdperson.internal.bridge.mixin;

import io.github.leawind.thirdperson.internal.bridge.events.RenderFrameEvent;
/*? if >=1.21 {*/
import net.minecraft.client.DeltaTracker;
/*? }*/
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
  /*? if >=26.2 {*/
  @Inject(method = "update", at = @At("HEAD"))
  private void beforeFrameExtraction(DeltaTracker deltaTracker, CallbackInfo ci) {
    RenderFrameEvent.emit(deltaTracker.getGameTimeDeltaPartialTick(false));
  }
  /*? } else if >=1.21 {*/
  /*@Inject(method = "render", at = @At("HEAD"))
  private void beforeRender(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
    RenderFrameEvent.emit(deltaTracker.getGameTimeDeltaPartialTick(false));
  }
  *//*? } else {*/
  /*@Inject(method = "render", at = @At("HEAD"))
  private void beforeRender(
      float partialTick, long frameStartNanos, boolean renderLevel, CallbackInfo ci) {
    RenderFrameEvent.emit(partialTick);
  }
  *//*? }*/
}
