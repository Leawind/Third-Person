package com.github.leawind.thirdperson.mixin;

import com.github.leawind.thirdperson.ThirdPerson;
import com.github.leawind.thirdperson.ThirdPersonStatus;
import com.github.leawind.thirdperson.core.EntityAgent;
import com.github.leawind.thirdperson.util.annotation.VersionSensitive;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RenderTypes.class, priority = 2000)
public class RenderTypeMixin {
  /**
   * 修改自 RenderType#ARMOR_CUTOUT_NO_CULL
   *
   * <p>将 NO_TRANSPARENCY 改成了 TRANSLUCENT_TRANSPARENCY
   */
  @Unique
  private static final Function<Identifier, RenderType> ARMOR_CUTOUT_NO_CULL_TRANSLUCENT =
      Util.memoize(
          identifier -> {
            RenderSetup renderSetup =
                RenderSetup.builder(RenderPipelines.ARMOR_CUTOUT_NO_CULL)
                    .withTexture("Sampler0", identifier)
                    .useLightmap()
                    .useOverlay()
                    .sortOnUpload()
                    .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .affectsCrumbling()
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .createRenderSetup();
            return RenderType.create("armor_cutout_no_cull_translucent", renderSetup);
          });

  /**
   * 对盔甲和鞘翅使用自定义的 RenderType 提供器，实现半透明效果
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
      ci.setReturnValue(ARMOR_CUTOUT_NO_CULL_TRANSLUCENT.apply(resourceLocation));
      ci.cancel();
    }
  }
}
