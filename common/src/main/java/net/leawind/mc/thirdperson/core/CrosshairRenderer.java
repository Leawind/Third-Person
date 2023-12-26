package net.leawind.mc.thirdperson.core;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.leawind.mc.thirdperson.mixin.GuiAccessor;
import net.minecraft.client.gui.GuiGraphics;

/**
 * 绘制准星
 */
@Environment(EnvType.CLIENT)
public class CrosshairRenderer {
	public static void render (GuiGraphics graphics) {
		final int crosshairSize = 15;
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
									   GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
									   GlStateManager.SourceFactor.ONE,
									   GlStateManager.DestFactor.ZERO);
		graphics.blit(GuiAccessor.getGuiIconLocation(),
					  (graphics.guiWidth() - crosshairSize) / 2,
					  (graphics.guiHeight() - crosshairSize) / 2,
					  0,
					  0,
					  crosshairSize,
					  crosshairSize);
		RenderSystem.defaultBlendFunc();
	}
}
