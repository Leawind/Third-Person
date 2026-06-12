package com.github.leawind.thirdperson.minecraft.bridge.mixin;

import com.github.leawind.thirdperson.minecraft.bridge.events.GameClientEvents;
import com.github.leawind.thirdperson.minecraft.bridge.events.context.ModifyArmorRenderTypeContext;
import com.github.leawind.thirdperson.utils.annotation.VersionSensitive;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(value = RenderTypes.class, priority = 2000)
public class RenderTypesMixin {
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
      Identifier identifier, @NonNull CallbackInfoReturnable<RenderType> ci) {
    var ctx = new ModifyArmorRenderTypeContext(identifier);
    GameClientEvents.MODIFY_ARMOR_RENDER_TYPE.emit(ctx);
    if (ctx.useTranslucent) {
      ci.setReturnValue(ARMOR_CUTOUT_NO_CULL_TRANSLUCENT.apply(identifier));
      ci.cancel();
    }
  }
}
