package com.github.leawind.thirdperson.minecraft.bridge.mixin;

import com.github.leawind.thirdperson.minecraft.bridge.events.GameClientEvents;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(value = Minecraft.class, priority = 2000)
public class MinecraftMixin {
  @Inject(method = "handleKeybinds", at = @At(value = "HEAD"))
  private void preHandleKeybinds(CallbackInfo ci) {
    GameClientEvents.HANDLE_KEYBINDS_START.emit(null);
  }
}
