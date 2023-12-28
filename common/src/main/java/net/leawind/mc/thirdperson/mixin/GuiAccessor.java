package net.leawind.mc.thirdperson.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(net.minecraft.client.gui.Gui.class)
public interface GuiAccessor {
	@Accessor("screenWidth")
	int getScreenWidth ();

	@Accessor("screenHeight")
	int getScreenHeight ();
}
