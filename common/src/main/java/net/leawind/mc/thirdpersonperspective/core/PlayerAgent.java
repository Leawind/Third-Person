package net.leawind.mc.thirdpersonperspective.core;


import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class PlayerAgent {
	public         LocalPlayer player;
	public         CameraAgent cameraAgent;
	private static PlayerAgent instance;

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
