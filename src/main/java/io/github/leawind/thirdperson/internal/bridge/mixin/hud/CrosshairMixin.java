package io.github.leawind.thirdperson.internal.bridge.mixin.hud;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.leawind.thirdperson.internal.bridge.events.CrosshairGateEvent;
/*? if >=26.2 {*/
import net.minecraft.client.gui.Hud;
/*? } else {*/
/*import net.minecraft.client.gui.Gui;
*//*? }*/
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/*? if >=26.2 {*/
@Mixin(Hud.class)
/*? } else {*/
/*@Mixin(Gui.class)
*//*? }*/
abstract class CrosshairMixin {
  @ModifyExpressionValue(
      /*? if >=26.1 {*/
      method = "extractCrosshair",
      /*? } else {*/
      /*method = "renderCrosshair",
      *//*? }*/
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"))
  private boolean modifyFirstPersonCrosshairGate(boolean vanillaDecision) {
    return CrosshairGateEvent.emit(vanillaDecision);
  }
}
