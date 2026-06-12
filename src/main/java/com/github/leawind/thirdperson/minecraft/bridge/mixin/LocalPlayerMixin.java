package com.github.leawind.thirdperson.minecraft.bridge.mixin;

import com.github.leawind.thirdperson.minecraft.bridge.events.GameClientEvents;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.EventContext;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.ModifyEntityHitContext;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.function.Predicate;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(value = LocalPlayer.class, priority = 2000)
public class LocalPlayerMixin {
  @Shadow protected int sprintTriggerTime;

  @Inject(method = "aiStep()V", at = @At("HEAD"))
  private void resetSprintTriggerTime(CallbackInfo ci) {
    var ctx = new EventContext<>(false);
    GameClientEvents.DISABLE_DOUBLE_TAP_SPRINT.emit(ctx);
    if (ctx.get()) {
      sprintTriggerTime = 0;
    }
  }

  @WrapOperation(
      method = "pick",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;"
                      + "Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"))
  private static EntityHitResult modifyPickEntity(
      Entity entity,
      Vec3 pickFrom,
      Vec3 pickTo,
      AABB aabb,
      Predicate<Entity> predicate,
      double pickRangeSqr,
      Operation<EntityHitResult> original) {
    var ctx = new ModifyEntityHitContext(entity, pickFrom, pickTo, aabb, predicate, pickRangeSqr);
    GameClientEvents.PICK_ENTITY.emit(ctx);
    return original.call(
        entity, ctx.pickFrom, ctx.pickTo, ctx.aabb, ctx.predicate, ctx.pickRangeSqr);
  }
}
