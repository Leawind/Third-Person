package net.leawind.mc.thirdperson.mixin;


import net.leawind.mc.thirdperson.config.Config;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value=Gui.class, priority=2000)
public class GuiMixin {
	/**
	 * 重定向 isFirstPerson()Z 方法
	 * <p>
	 * 原本仅在第一人称下显示准星，现在第三人称下也可以显示准星
	 */
	@Redirect(method="renderCrosshair(Lcom/mojang/blaze3d/vertex/PoseStack;)V",
			  at=@At(value="INVOKE", target="Lnet/minecraft/client/CameraType;isFirstPerson ()Z"))
	public boolean shouldRenderCrosshair (CameraType instance) {
		return Config.is_mod_enable || Minecraft.getInstance().options.getCameraType().isFirstPerson();
	}
}
