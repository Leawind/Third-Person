package net.leawind.mc.thirdpersonperspective;


import com.mojang.logging.LogUtils;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.leawind.mc.thirdpersonperspective.core.CameraAgent;
import net.leawind.mc.thirdpersonperspective.core.CrosshairRenderer;
import net.leawind.mc.thirdpersonperspective.userprofile.UserProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import org.slf4j.Logger;
import org.slf4j.event.Level;

public class ThirdPersonPerspectiveMod {
	public static final String MOD_ID = "leawind_third_person_perspective";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static void init () {
		LOGGER.atLevel(Level.TRACE);
		System.out.println(ExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
		ModKeys.register();
		ModEvents.register();
		LOGGER.debug("Debug message oziaosfdp");
	}

	private static class ModEvents {
		public static void register () {
			ClientLifecycleEvent.CLIENT_STARTED.register(ModEvents::onClientStarted);
			ClientGuiEvent.RENDER_HUD.register(ModEvents::onRenderHud);
			ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(ModEvents::onClientPlayerRespawn);
			ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(ModEvents::onClientPlayerJoin);
			ClientTickEvent.CLIENT_POST.register(ModKeys::handleThrowExpey);
		}

		/**
		 * 当玩家死亡后重生或加入新的维度时触发
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

		public static void onClientStarted (Minecraft minecraft) {
			UserProfile.loadDefault();
			UserProfile.load();
		}
	}
}
