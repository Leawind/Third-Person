package net.leawind.mc.thirdpersonperspective.core;


import com.mojang.logging.LogUtils;
import net.leawind.mc.thirdpersonperspective.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.slf4j.Logger;

public class PlayerAgent {
	public static final Logger      LOGGER = LogUtils.getLogger();
	private static      PlayerAgent instance;
	public              LocalPlayer player;
	public              CameraAgent cameraAgent;

	public PlayerAgent () {
		Minecraft mc = Minecraft.getInstance();
		player = (LocalPlayer)mc.getCameraEntity();
		if (mc.getCameraEntity() != mc.player) {
			throw new Error("This should not happen in release version");
		}
		LOGGER.info("New CameraAgent created");
	}

	public static boolean isAvailable () {
		if (!Config.is_mod_enable) {
			return false;
		}
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
