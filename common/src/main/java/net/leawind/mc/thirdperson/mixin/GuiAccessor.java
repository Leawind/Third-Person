package net.leawind.mc.thirdperson.mixin;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 用于访问准星贴图资源的路径
 */
@Mixin(net.minecraft.client.gui.Gui.class)
@Environment(EnvType.CLIENT)
public interface GuiAccessor {
	@Accessor("GUI_ICONS_LOCATION")
	static ResourceLocation getGuiIconLocation () {
		throw new AssertionError();
	}
}
