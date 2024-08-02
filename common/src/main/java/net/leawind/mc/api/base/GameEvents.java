package net.leawind.mc.api.base;


import net.leawind.mc.api.client.event.*;

import java.util.function.Consumer;
import java.util.function.Function;

public class GameEvents {
	public static Consumer<ThirdPersonCameraSetupEvent> thirdPersonCameraSetup = null;
	public static Consumer<MinecraftPickEvent>          minecraftPick          = null;
	public static Consumer<RenderTickStartEvent>        renderTickStart        = null;
	public static Consumer<CalculateMoveImpulseEvent>   calculateMoveImpulse   = null;
	public static Function<RenderEntityEvent, Boolean>  renderEntity           = null;
	public static Runnable                              handleKeybindsStart    = null;
	public static Consumer<MouseTurnPlayerStartEvent>   mouseTurnPlayerStart   = null;
	public static Consumer<EntityTurnStartEvent>        entityTurnStart        = null;
}
