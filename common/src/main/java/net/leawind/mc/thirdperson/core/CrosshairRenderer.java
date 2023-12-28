package net.leawind.mc.thirdperson.core;


import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.leawind.mc.thirdperson.mixin.GuiAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

/**
 * 绘制准星
 */
public class CrosshairRenderer {
	public static void render (PoseStack matrices) {
		// TODO Render crosshair
		Minecraft mc            = Minecraft.getInstance();
		Gui       gui           = mc.gui;
		final int crosshairSize = 15;
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
									   GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
									   GlStateManager.SourceFactor.ONE,
									   GlStateManager.DestFactor.ZERO);
		Gui.blit(matrices,
				 (((GuiAccessor)gui).getScreenWidth() - crosshairSize) / 2,
				 (((GuiAccessor)gui).getScreenHeight() - crosshairSize) / 2,
				 0,
				 0,
				 crosshairSize,
				 crosshairSize);
		RenderSystem.defaultBlendFunc();
	}
}
