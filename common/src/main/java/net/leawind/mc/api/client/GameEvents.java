package net.leawind.mc.api.client;


import net.leawind.mc.api.client.events.CameraSetupEvent;

import java.util.function.Consumer;

public class GameEvents {
	public static Consumer<CameraSetupEvent> setupCamera = null;
}
