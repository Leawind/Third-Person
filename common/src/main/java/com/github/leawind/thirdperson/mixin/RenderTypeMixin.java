package com.github.leawind.thirdperson.mixin;

import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.github.leawind.thirdperson.core.EntityAgent;
import com.github.leawind.thirdperson.util.annotation.VersionSensitive;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TimeUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(value = RenderType.class, priority = 2000)
public class RenderTypeMixin {

  @Unique
  private static final Function<ResourceLocation, RenderType> ARMOR_CUTOUT_NO_CULL_TRANSLUCENT =
      Util.<ResourceLocation, RenderType>memoize(RenderType::entityTranslucent);

  /**
   * 对盔甲和鞘翅使用自定义的 RenderType 提供器，实现半透明效果
   *
   * <p>see ModelPartCubeMixin#compile(float)
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
      ResourceLocation location, @NotNull CallbackInfoReturnable<RenderType> ci) {
    if (ThirdPerson.isAvailable()
        && ThirdPersonStatus.isRenderingInThirdPerson()
        && ThirdPersonStatus.useCameraEntityOpacity(
            (float)
                (Minecraft.getInstance().getFrameTimeNs()
                    / TimeUtil.NANOSECONDS_PER_MILLISECOND))) {
      ci.setReturnValue(ARMOR_CUTOUT_NO_CULL_TRANSLUCENT.apply(location));
      ci.cancel();
    }
  }
}
