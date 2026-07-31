package io.github.leawind.thirdperson.internal.bridge.mixin.cameraentityopacity;

import io.github.leawind.thirdperson.internal.bridge.CameraEntityRenderState;
/*? if <1.21.11 {*/
/*import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*//*? } else {*/
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
/*? }*/

@Mixin(LevelRenderer.class)
abstract class LevelRendererMixin {
  /*? if <1.21.11 {*/
  /*@Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
  private void beforeRenderCameraEntity(
      Entity entity,
      double cameraX,
      double cameraY,
      double cameraZ,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      CallbackInfo ci) {
    if (entity != Minecraft.getInstance().getCameraEntity()) {
      return;
    }
    if (CameraEntityRenderState.begin(partialTick)) {
      CameraEntityRenderState.end();
      ci.cancel();
    }
  }

  @Inject(method = "renderEntity", at = @At("TAIL"))
  private void afterRenderCameraEntity(
      Entity entity,
      double cameraX,
      double cameraY,
      double cameraZ,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      CallbackInfo ci) {
    if (entity == Minecraft.getInstance().getCameraEntity()) {
      CameraEntityRenderState.end();
    }
  }
  *//*? }*/
}
