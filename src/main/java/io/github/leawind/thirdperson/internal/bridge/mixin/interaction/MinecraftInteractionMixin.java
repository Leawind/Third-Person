package io.github.leawind.thirdperson.internal.bridge.mixin.interaction;

import io.github.leawind.thirdperson.internal.bridge.events.AfterVanillaPickEvent;
import io.github.leawind.thirdperson.internal.bridge.events.BeforeInteractionEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
abstract class MinecraftInteractionMixin {
  /*? if >=26.1 {*/
  @Inject(method = "pick", at = @At("RETURN"))
  private void afterVanillaPick(float partialTick, CallbackInfo ci) {
    AfterVanillaPickEvent.emit(partialTick);
  }
  /*? }*/

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
    BeforeInteractionEvent.emit();
  }
}
