package io.github.leawind.thirdperson.internal.bridge.mixin;

import io.github.leawind.thirdperson.internal.bridge.events.MouseScrollEvent;
import io.github.leawind.thirdperson.internal.integration.perspective.PerspectiveGuard;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
abstract class MouseHandlerMixin {
  @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
  private void beforeMouseScroll(
      long windowHandle, double xOffset, double yOffset, CallbackInfo ci) {
    if (PerspectiveGuard.isThirdPersonCurrent()
        && MouseScrollEvent.emit(xOffset, yOffset)) {
      ci.cancel();
    }
  }
}
