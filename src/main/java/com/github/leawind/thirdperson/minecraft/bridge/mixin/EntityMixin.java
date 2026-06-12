package com.github.leawind.thirdperson.minecraft.bridge.mixin;

import com.github.leawind.thirdperson.minecraft.bridge.events.GameClientEvents;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.PickBlockContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.TurnPlayerContext;
import com.github.leawind.thirdperson.utils.Vecs;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class, priority = 2000)
public class EntityMixin implements EntityOpacityAccessor {

  @Unique private float leawind_third_person$opacity = 1.0f;

  @Override
  public float leawind_third_person$getOpacity() {
    return leawind_third_person$opacity;
  }

  @Override
  public void leawind_third_person$setOpacity(float opacity) {
    leawind_third_person$opacity = opacity;
  }

  /// 实体探测方块
  ///
  /// 原版行为：从实体眼睛出发。使用实体的{@link Entity#getViewVector}计算方向，结合探测距离计算探测终点
  ///
  /// @param pickFrom 探测起点，原本是玩家眼睛位置
  /// @param pickTo 探测终点，原本是玩家眼睛前方距离为 pickRange 的位置
  /// @param blockShape 探测的方块类型
  /// @param fluidShape 探测的流体类型
  /// @param entity 探测者
  /// @param pickRange 探测距离，即目标与玩家眼睛间的最大距离
  /// @see GameRendererMixin
  ///
  @WrapOperation(
      method = "pick",
      at = @At(value = "NEW", target = "Lnet/minecraft/world/level/ClipContext;"))
  private ClipContext wrapPick(
      Vec3 pickFrom,
      Vec3 pickTo,
      Block blockShape,
      Fluid fluidShape,
      Entity entity,
      Operation<ClipContext> original,
      @Local(argsOnly = true) double pickRange,
      @Local(argsOnly = true) float partialTick) {
    var ctx =
        new PickBlockContext(
            Vecs.toVector3d(pickFrom), Vecs.toVector3d(pickTo), entity, pickRange, partialTick);
    GameClientEvents.PICK_BLOCK.emit(ctx);
    return original.call(
        Vecs.toVec3(ctx.from), Vecs.toVec3(ctx.to), blockShape, fluidShape, entity);
  }

  /** 鼠标移动事件处理函数会调用此方法旋转玩家，参考 `MouseHandler#turnPlayer()` */
  @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
  private void beforeTurnPlayer(double yRotDelta, double xRotDelta, @NotNull CallbackInfo ci) {
    var ctx = new TurnPlayerContext((Entity) (Object) this, yRotDelta, xRotDelta);
    GameClientEvents.TURN_PLAYER.emit(ctx);
    if (ctx.cancelDefault) {
      ci.cancel();
    }
  }
}
