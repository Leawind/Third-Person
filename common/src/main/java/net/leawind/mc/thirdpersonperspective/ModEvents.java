package net.leawind.mc.thirdpersonperspective;


import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.leawind.mc.thirdpersonperspective.core.CameraAgent;
import net.minecraft.client.player.LocalPlayer;

public class ModEvents {
	public static void register () {
		ClientTickEvent.CLIENT_POST.register(ModKeys::handleThrowExpey);
		ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(ModEvents::onClientLevelLoad);
	}

	private static void onClientLevelLoad (LocalPlayer oldPlayer, LocalPlayer newPlayer) {
		CameraAgent.getInstance().player = newPlayer;
	}
}
