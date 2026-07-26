package com.github.leawind.thirdperson.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftInvoker {
  /** 立即执行选取，更新 {@link Minecraft#hitResult} */
  @Invoker("pick")
  void invokePick(float partialTicks);
}
