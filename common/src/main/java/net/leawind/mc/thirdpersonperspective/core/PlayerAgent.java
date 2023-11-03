package net.leawind.mc.thirdpersonperspective.core;


import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class PlayerAgent {
	public         LocalPlayer player;
	public         CameraAgent cameraAgent;
	private static PlayerAgent instance;

	public PlayerAgent () {
		Minecraft mc = Minecraft.getInstance();
		player = (LocalPlayer)mc.getCameraEntity();
		if (mc.getCameraEntity() != mc.player) {
			throw new Error("This should not happen in release version of my mod");
		}
	}

	public static boolean isAvailable () {
		Minecraft   mc     = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		return player != null;
	}

	public static PlayerAgent getInstance () {
		if (instance == null) {
			instance = new PlayerAgent();
		}
		return instance;
	}
}
