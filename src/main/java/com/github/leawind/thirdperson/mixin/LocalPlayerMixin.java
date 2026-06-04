package com.github.leawind.thirdperson.mixin;

import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
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
    var config = ThirdPerson.getConfig();
    if (config.is_mod_enabled
        && !Minecraft.getInstance().options.getCameraType().isFirstPerson()
        && !config.allow_double_tap_sprint) {
      sprintTriggerTime = 0;
    }
  }

  /**
   * 修改探测实体的起点和终点
   *
   * @param pickFrom 探测起点，原本是玩家眼睛位置
   * @param pickTo 探测终点，原本是玩家眼睛前方距离为 pickRange 的位置
   * @param aabb 碰撞箱，只有与它有交点的实体才会被考虑
   * @param predicate 目标实体谓词
   * @param pickRangeSqr 探测距离上限的平方
   */
  @WrapOperation(
      method = "pick",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;"
                      + "Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;"))
  private static EntityHitResult wrapEntityHit(
      Entity receiver,
      Vec3 pickFrom,
      Vec3 pickTo,
      AABB aabb,
      Predicate<Entity> predicate,
      double pickRangeSqr,
      Operation<EntityHitResult> original) {
    if (ThirdPerson.isAvailable() && ThirdPersonStatus.isRenderingInThirdPerson()) {
      if (receiver == Minecraft.getInstance().getCameraEntity()) {
        pickTo = ThirdPerson.CAMERA_AGENT.getHitResult().getLocation();
        if (ThirdPersonStatus.shouldPickFromCamera()) {
          // 从相机到准星处
          pickFrom = Minecraft.getInstance().gameRenderer.getMainCamera().position();
          var pickVector = pickFrom.vectorTo(pickTo).normalize().scale(1e-4);
          pickTo = pickTo.add(pickVector);
        } else {
          // 从玩家眼睛到准星处
          // 限制距离为 pickRange
          var pickRange = Math.sqrt(pickRangeSqr);
          var pickVector = pickFrom.vectorTo(pickTo).normalize().scale(pickRange);
          pickTo = pickFrom.add(pickVector);
        }
        aabb = new AABB(pickFrom, pickTo).inflate(1.0);
        pickRangeSqr = pickFrom.distanceToSqr(pickTo);
      }
    }
    return original.call(receiver, pickFrom, pickTo, aabb, predicate, pickRangeSqr);
  }
}
