package com.github.leawind.thirdperson.minecraft.bridge.mixin;

import com.github.leawind.thirdperson.api.ThirdPerson;
import com.github.leawind.thirdperson.impl.ThirdPersonStatus;
import com.github.leawind.thirdperson.api.base.GameEvents;
import com.github.leawind.thirdperson.api.client.event.RenderTickStartEvent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@link GameRenderer#pick(float)} 的作用是更新{@link Minecraft#hitResult}和{@link
 * Minecraft#crosshairPickEntity}
 *
 * <p>在原版中它有两处调用：
 *
 * <p>1. 在{@link Minecraft#tick()}开头，tick 完 chatListener 和 gui 之后调用
 *
 * <p>2. 在{@link GameRenderer#renderLevel}的开头，更新完相机实体后调用
 *
 * <p>GameRenderer#pick会先调用{@link Entity#pick}探测方块，再通过{@link
 * ProjectileUtil#getEntityHitResult}探测实体，然后计算最终探测结果
 *
 * <p>当探测结果为空时，它会通过 {@link BlockHitResult#miss(Vec3, Direction, BlockPos)} 创建一个表示结果为空的
 * BlockHitResult 对象，此时会根据玩家的朝向计算 Direction 参数。
 */
@Mixin(value = GameRenderer.class, priority = 2000)
public abstract class GameRendererMixin {

  @ModifyReturnValue(method = "getFov", at = @At(value = "RETURN", ordinal = 1))
  private float modifyFov(float fov) {
    if (!((GameRenderer) (Object) this).isPanoramicMode()
        && ThirdPerson.isAvailable()
        && ThirdPersonStatus.isRenderingInThirdPerson()) {
      fov /= (float) ThirdPerson.CAMERA_AGENT.getSmoothFovDivisor();
    }
    return fov;
  }

  /** 渲染tick前 */
  @Inject(method = "render", at = @At("HEAD"))
  private void preRender(DeltaTracker deltaTracker, boolean doRenderLevel, CallbackInfo ci) {
    if (GameEvents.renderTickStart != null) {
      GameEvents.renderTickStart.accept(
          new RenderTickStartEvent(deltaTracker.getGameTimeDeltaTicks()));
    }
  }

  /** 禁用第三人称视角摇晃 */
  @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
  private void cancelBobView(PoseStack poseStack, float partialTick, CallbackInfo ci) {
    if (ThirdPerson.isAvailable()
        && ThirdPersonStatus.isRenderingInThirdPerson()
        && ThirdPerson.getConfig().disable_third_person_bob_view) {
      ci.cancel();
    }
  }
}
