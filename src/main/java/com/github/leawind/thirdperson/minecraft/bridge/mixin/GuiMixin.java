package com.github.leawind.thirdperson.minecraft.bridge.mixin;

import com.github.leawind.thirdperson.impl.ThirdPersonStatus;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("unused")
@Mixin(value = Gui.class, priority = 2000)
public class GuiMixin {
  @ModifyExpressionValue(
      method = "renderCrosshair",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"))
  private boolean isFirstPerson(boolean isFirstPersonReally) {
    return isFirstPersonReally || ThirdPersonStatus.forceThirdPersonCrosshair;
  }
}
