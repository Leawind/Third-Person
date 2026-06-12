package com.github.leawind.thirdperson.minecraft.bridge.mixin;

import com.github.leawind.thirdperson.minecraft.bridge.events.GameClientEvents;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.CameraSetupContext;
import com.github.leawind.thirdperson.utils.Vecs;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Camera.class, priority = 2000)
public abstract class CameraMixin {
  @Shadow
  protected abstract void setPosition(Vec3 arg);

  @Shadow
  protected abstract void setRotation(float f, float g);

  /// `setup` 方法中第三人称下移动相机之前
  ///
  /// `setup` 方法位于真正渲染画面之前。
  ///
  /// `GameRender#render` -> `GameRender#renderLevel` -> `Camera#setup`
  @Inject(
      method = "setup",
      at = {
        @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Camera;move(FFF)V",
            ordinal = 0,
            shift = At.Shift.BEFORE),
        @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Camera;move(FFF)V",
            ordinal = 1,
            shift = At.Shift.BEFORE)
      },
      cancellable = true)
  private void beforeMoveCamera(
      Level level,
      Entity entity,
      boolean detached,
      boolean mirror,
      float partialTicks,
      CallbackInfo ci) {
    var camera = (Camera) (Object) this;

    var ctx = new CameraSetupContext(level, entity, detached, partialTicks, camera);
    GameClientEvents.SETUP_CAMERA.emit(ctx);
    this.setPosition(Vecs.toVec3(ctx.pos));
    this.setRotation(ctx.yRot, ctx.xRot);
    ci.cancel();
  }
}
