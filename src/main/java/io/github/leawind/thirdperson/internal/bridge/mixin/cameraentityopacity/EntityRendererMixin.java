package io.github.leawind.thirdperson.internal.bridge.mixin.cameraentityopacity;

import io.github.leawind.thirdperson.internal.bridge.CameraEntityRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
/*? if >=1.21.11 {*/
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*? }*/
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityRenderer.class)
abstract class EntityRendererMixin {
  /*? if >=1.21.11 {*/
  @Inject(
      method =
          "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;",
      at = @At("RETURN"))
  private void captureCameraEntityRenderState(
      Entity entity, float partialTick, CallbackInfoReturnable<EntityRenderState> ci) {
    if (entity == Minecraft.getInstance().getCameraEntity()) {
      CameraEntityRenderState.setCameraEntityRenderState(ci.getReturnValue());
    }
  }
  /*? }*/
}
