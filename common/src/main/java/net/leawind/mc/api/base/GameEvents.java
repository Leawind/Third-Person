package net.leawind.mc.api.base;


import net.leawind.mc.api.client.events.*;

import java.util.function.Consumer;
import java.util.function.Function;

public class GameEvents {
	public static Consumer<ThirdPersonCameraSetupEvent> thirdPersonCameraSetup = null;
	public static Consumer<MinecraftPickEvent>          minecraftPick          = null;
	public static Consumer<PreRenderTickEvent>          preRenderTick          = null;
	public static Consumer<CalculateMoveImpulseEvent>   calculateMoveImpulse   = null;
	public static Function<RenderEntityEvent, Boolean>  renderEntity           = null;
	public static Runnable                              preHandleKeybinds      = null;
	public static Consumer<PreMouseTurnPlayerEvent>     preMouseTurnPlayer     = null;
	public static Consumer<PreEntityTurnEvent>          preEntityTurn          = null;
}
