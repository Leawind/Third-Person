package net.leawind.mc.thirdpersonperspective.mixin;

import net.leawind.mc.thirdpersonperspective.Config;
import net.leawind.mc.thirdpersonperspective.ThirdPersonPerspective;
import net.leawind.mc.thirdpersonperspective.agent.CameraAgent;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 默认不会在第三人称下显示准星。
 * <p>
 * 所以直接在末尾加一个渲染准星的功能即可
 */
@Mixin(net.minecraft.client.gui.Gui.class)
public class GuiMixin {
	@Inject(method="renderCrosshair", at=@At(value="TAIL"))
	private void renderCrosshair(GuiGraphics guiGraphics, CallbackInfo ci){
		if(Config.do_render_crosshair && CameraAgent.isAvailable()){
			if(CameraAgent.getInstance().isFreeTpv){
				ThirdPersonPerspective.renderCrosshair(guiGraphics);
			}
		}
	}
}
