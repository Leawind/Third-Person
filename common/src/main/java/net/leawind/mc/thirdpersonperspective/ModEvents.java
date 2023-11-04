package net.leawind.mc.thirdpersonperspective;


import com.mojang.logging.LogUtils;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.leawind.mc.thirdpersonperspective.core.CameraAgent;
import net.leawind.mc.thirdpersonperspective.core.CrosshairRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import org.slf4j.Logger;

public class ModEvents {
	public static final Logger LOGGER = LogUtils.getLogger();

	public static void register () {
		ClientTickEvent.CLIENT_POST.register(ModKeys::handleThrowExpey);
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(ModEvents::onClientPlayerRespawn);
		ClientGuiEvent.RENDER_HUD.register(ModEvents::onRenderHud);
	}

	private static void onRenderHud (GuiGraphics graphics, float tickDelta) {
		if (CameraAgent.isAvailable() && CameraAgent.getInstance().isThirdPersonEnabled) {
			CrosshairRenderer.render(graphics);
		}
	}

	private static void onClientPlayerRespawn (LocalPlayer oldPlayer, LocalPlayer newPlayer) {
		LOGGER.info("on Client player respawn");
		CameraAgent.getInstance().player = newPlayer;
	}
}
