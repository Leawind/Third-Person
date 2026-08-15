package io.github.leawind.thirdperson.internal.bridge.mixin.interaction;

import io.github.leawind.thirdperson.internal.bridge.events.AfterVanillaPickEvent;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
abstract class GameRendererPickingMixin {
  /*? if <26.1 {*/
  /*@Inject(method = "pick", at = @At("RETURN"))
  private void afterVanillaPick(float partialTick, CallbackInfo ci) {
    AfterVanillaPickEvent.emit(partialTick);
  }
  *//*? }*/
}
