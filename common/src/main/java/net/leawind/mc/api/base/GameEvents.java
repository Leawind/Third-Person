package net.leawind.mc.api.base;


import net.leawind.mc.api.client.events.CalculateMoveImpulseEvent;
import net.leawind.mc.api.client.events.CameraSetupEvent;
import net.leawind.mc.api.client.events.MinecraftPickEvent;
import net.leawind.mc.api.client.events.PreRenderTickEvent;

import java.util.function.Consumer;

public class GameEvents {
	public static Consumer<CameraSetupEvent>          setupCamera          = null;
	public static Consumer<MinecraftPickEvent>        minecraftPick        = null;
	public static Consumer<PreRenderTickEvent>        preRenderTick        = null;
	public static Consumer<CalculateMoveImpulseEvent> calculateMoveImpulse = null;
}
