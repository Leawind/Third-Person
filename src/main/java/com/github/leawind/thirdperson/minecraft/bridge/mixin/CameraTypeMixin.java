package com.github.leawind.thirdperson.minecraft.bridge.mixin;

import net.minecraft.client.CameraType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CameraType.class, priority = 2000)
public class CameraTypeMixin {
  /**
   * 这个字段的含义是：玩家想要通过按键切换到的是否为第一人称
   *
   * <p>通过 {@link CameraType#cycle()} 可以更改此字段，其他模组也可以通过直接设置此字段来切换视角。
   *
   * <p>注意：即使此字段为true，即目标是第一人称，也未必以第一人称渲染。还可能处于从第三人称过渡到第一人称的过程中。
   */
  @Final @Shadow private boolean firstPerson;

  /** 提供API：更改 {@link CameraType#isFirstPerson()} 的返回值 */
  @Inject(method = "isFirstPerson", at = @At("RETURN"), cancellable = true)
  private void isFirstPerson(CallbackInfoReturnable<Boolean> ci) {
    // TODO
  }

  /// 提供API：是否跳过原版第二人称视角
  @Inject(method = "cycle", at = @At("RETURN"), cancellable = true)
  private void modifyCycle(CallbackInfoReturnable<CameraType> ci) {
    // TODO
  }
}
