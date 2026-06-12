package com.github.leawind.thirdperson.minecraft.bridge.mixin;

import com.github.leawind.thirdperson.minecraft.bridge.events.GameClientEvents;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.EventContext;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Gui.class, priority = 2000)
public class GuiMixin {
  @ModifyExpressionValue(
      method = "renderCrosshair",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"))
  private boolean isFirstPerson(boolean isFirstPerson) {
    var ctx = new EventContext<>(false);
    GameClientEvents.ENABLE_THIRD_PERSON_CROSSHAIR.emit(ctx);
    return isFirstPerson || ctx.get();
  }
}
