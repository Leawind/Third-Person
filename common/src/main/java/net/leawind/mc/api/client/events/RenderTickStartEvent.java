package net.leawind.mc.api.client.events;


import net.leawind.mc.api.base.ModEvent;

public final class RenderTickStartEvent implements ModEvent {
	public final float partialTick;

	public RenderTickStartEvent (float partialTick) {
		this.partialTick = partialTick;
	}

	@Override
	public boolean set () {
		return false;
	}
}
