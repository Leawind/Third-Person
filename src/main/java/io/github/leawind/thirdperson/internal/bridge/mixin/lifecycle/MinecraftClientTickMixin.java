package io.github.leawind.thirdperson.internal.bridge.mixin.lifecycle;

import io.github.leawind.thirdperson.internal.bridge.events.ClientTickEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MinecraftClientTickMixin {
  @Inject(method = "tick", at = @At("TAIL"))
  private void afterClientTick(CallbackInfo ci) {
    ClientTickEvent.emit();
  }
}
