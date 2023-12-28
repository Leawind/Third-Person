package net.leawind.mc.thirdperson.mixin;


import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 用于访问准星贴图资源的路径 net.minecraft.client.gui.GuiComponent
 */
@Mixin(net.minecraft.client.gui.GuiComponent.class)
public interface GuiComponentAccessor {
	@Accessor("GUI_ICONS_LOCATION")
	static ResourceLocation getGuiIconLocation () {
		throw new AssertionError();
	}
}
