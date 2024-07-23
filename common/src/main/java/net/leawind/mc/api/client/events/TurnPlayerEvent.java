package net.leawind.mc.api.client.events;


import net.leawind.mc.api.base.ModEvent;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;

public class TurnPlayerEvent implements ModEvent {
	public final MouseHandler mouseHandler;
	public final LocalPlayer  player;
	public final double       dx;
	public final double       dy;

	public TurnPlayerEvent (MouseHandler mouseHandler, LocalPlayer player, double dx, double dy) {
		this.mouseHandler = mouseHandler;
		this.player       = player;
		this.dx           = dx;
		this.dy           = dy;
	}

	@Override
	public boolean set () {
		return false;
	}
}
