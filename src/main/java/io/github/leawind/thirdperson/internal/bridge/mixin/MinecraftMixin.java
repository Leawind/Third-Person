package io.github.leawind.thirdperson.internal.bridge.mixin;

import io.github.leawind.thirdperson.internal.bridge.events.BeforeInteractionEvent;
import io.github.leawind.thirdperson.internal.bridge.events.ClientTickEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {
  /*? if >=26.1 {*/
  @Invoker("pick")
  protected abstract void invokePick(float partialTicks);
  /*? }*/

  @Inject(method = "tick", at = @At("TAIL"))
  private void afterClientTick(CallbackInfo ci) {
    ClientTickEvent.emit();
  }

  @Inject(method = "startAttack", at = @At("HEAD"))
  private void beforeStartAttack(CallbackInfoReturnable<Boolean> cir) {
    prepareInteraction();
  }

  @Inject(method = "startUseItem", at = @At("HEAD"))
  private void beforeStartUseItem(CallbackInfo ci) {
    prepareInteraction();
  }

  @Inject(method = "continueAttack", at = @At("HEAD"))
  private void beforeContinueAttack(boolean attacking, CallbackInfo ci) {
    if (attacking) {
      prepareInteraction();
    }
  }

  /*? if >=26.1 {*/
  @Inject(method = "pickBlockOrEntity", at = @At("HEAD"))
  private void beforePickBlockOrEntity(CallbackInfo ci) {
    prepareInteraction();
  }
  /*? } else {*/
  /*@Inject(method = "pickBlock", at = @At("HEAD"))
  private void beforePickBlock(CallbackInfo ci) {
    prepareInteraction();
  }
  *//*? }*/

  @Unique
  private void prepareInteraction() {
    BeforeInteractionEvent.Result result = BeforeInteractionEvent.emit();
    if (result != BeforeInteractionEvent.Result.REPICK) {
      return;
    }
    /*? if >=26.1 {*/
    invokePick(1.0f);
    /*? } else {*/
    /*Minecraft.getInstance().gameRenderer.pick(1.0f);
    *//*? }*/
  }
}
