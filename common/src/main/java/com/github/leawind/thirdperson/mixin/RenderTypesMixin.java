package com.github.leawind.thirdperson.mixin;

import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.github.leawind.thirdperson.core.EntityAgent;
import com.github.leawind.thirdperson.util.annotation.VersionSensitive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(value = RenderTypes.class, priority = 2000)
public class RenderTypesMixin {
  /**
   * 对盔甲和鞘翅使用自定义的 RenderType 提供器，实现半透明效果
   *
   * <p>将 NO_TRANSPARENCY 改成了 TRANSLUCENT_TRANSPARENCY
   *
   * <p>see ModelPartCubeMixin#compile(float)
   *
   * <p>
   *
   * @see EntityAgent#getSmoothOpacity(float)
   */
  @VersionSensitive
  @Inject(
      method = "armorCutoutNoCull",
      at =
          @At(
              value = "HEAD",
              target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;"),
      cancellable = true)
  private static void setTransparencyState(
      Identifier resourceLocation, @NotNull CallbackInfoReturnable<RenderType> ci) {
    if (ThirdPerson.isAvailable()
        && ThirdPersonStatus.isRenderingInThirdPerson()
        && ThirdPersonStatus.useCameraEntityOpacity(
            (float)
                (Minecraft.getInstance().getFrameTimeNs()
                    / TimeUtil.NANOSECONDS_PER_MILLISECOND))) {
      ci.setReturnValue(RenderTypes.armorTranslucent(resourceLocation));
      ci.cancel();
    }
  }
}
