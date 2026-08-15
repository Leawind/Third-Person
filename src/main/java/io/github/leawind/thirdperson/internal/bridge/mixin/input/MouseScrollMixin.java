package io.github.leawind.thirdperson.internal.bridge.mixin.input;

import io.github.leawind.thirdperson.internal.bridge.events.MouseScrollEvent;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
abstract class MouseScrollMixin {
  @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
  private void beforeMouseScroll(
      long windowHandle, double xOffset, double yOffset, CallbackInfo ci) {
    if (MouseScrollEvent.emit(xOffset, yOffset)) {
      ci.cancel();
    }
  }
}
