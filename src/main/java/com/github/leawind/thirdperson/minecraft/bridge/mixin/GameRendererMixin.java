package com.github.leawind.thirdperson.minecraft.bridge.mixin;

import com.github.leawind.thirdperson.minecraft.bridge.events.GameClientEvents;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.EventContext;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameRenderer.class, priority = 2000)
public abstract class GameRendererMixin {

  @ModifyReturnValue(method = "getFov", at = @At(value = "RETURN", ordinal = 1))
  private float modifyFov(float fov) {
    var ctx = new EventContext<>(fov);
    GameClientEvents.MODIFY_FOV.emit(ctx);
    return ctx.get();
  }

  @Inject(method = "render", at = @At("HEAD"))
  private void preRender(DeltaTracker deltaTracker, boolean doRenderLevel, CallbackInfo ci) {
    GameClientEvents.RENDER_TICK_START.emit(deltaTracker.getGameTimeDeltaTicks());
  }

  @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
  private void cancelBobView(PoseStack poseStack, float partialTick, CallbackInfo ci) {
    var ctx = new EventContext<>(true);
    GameClientEvents.ENABLE_BOB_VIEW.emit(ctx);
    if (!ctx.get()) {
      ci.cancel();
    }
  }
}
