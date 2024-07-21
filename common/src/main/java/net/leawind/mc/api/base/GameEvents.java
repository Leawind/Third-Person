package net.leawind.mc.api.base;


import net.leawind.mc.api.client.events.CameraSetupEvent;
import net.leawind.mc.api.client.events.MinecraftPickEvent;

import java.util.function.Consumer;

public class GameEvents {
	public static Consumer<CameraSetupEvent>   setupCamera   = null;
	public static Consumer<MinecraftPickEvent> minecraftPick = null;
}
