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

public class ThirdPersonPerspectiveMod {
	public static final String MOD_ID = "leawind_third_person_perspective";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static void init () {
		System.out.println(ExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
		ModKeys.register();
		ClientTickEvent.CLIENT_POST.register(ModKeys::handleThrowExpey);
		ClientGuiEvent.RENDER_HUD.register(ModEvents::onRenderHud);
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(ModEvents::onClientPlayerRespawn);
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(ModEvents::onClientPlayerJoin);
	}

	private static class ModEvents {
		/**
		 * 当玩家死亡后重生或加入新的维度时触发
		 *
		 * @param oldPlayer
		 * @param newPlayer
		 */
		public static void onClientPlayerRespawn (LocalPlayer oldPlayer, LocalPlayer newPlayer) {
			CameraAgent.getInstance().player = newPlayer;
			// TODO
			LOGGER.info("on Client player respawn");
		}

		public static void onClientPlayerJoin (LocalPlayer player) {
			LOGGER.info("on Client player join");
		}

		public static void onRenderHud (GuiGraphics graphics, float tickDelta) {
			if (CameraAgent.isAvailable() && CameraAgent.getInstance().isThirdPersonEnabled) {
				CrosshairRenderer.render(graphics);
			}
		}
	}
}
