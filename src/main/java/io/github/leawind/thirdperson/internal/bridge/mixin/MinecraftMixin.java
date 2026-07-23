package io.github.leawind.thirdperson.internal.bridge.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

  @Inject(method = "handleKeybinds", at = @At(value = "HEAD"))
  private void beforeHandleKeybinds(CallbackInfo ci) {
    // Placeholder for future keybind handling
  }
}
