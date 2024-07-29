package net.leawind.mc.api.client.events;


import net.leawind.mc.api.base.ModEvent;
import net.minecraft.world.entity.Entity;

public class PreEntityTurnEvent implements ModEvent {
	public final Entity  entity;
	public final double  dXRot;
	public final double  dYRot;
	private      boolean isDefaultCancelled = false;

	public PreEntityTurnEvent (Entity entity, double dYRot, double dXRot) {
		this.entity = entity;
		this.dXRot  = dXRot;
		this.dYRot  = dYRot;
	}

	/**
	 * 取消默认操作
	 */
	public void cancelDefault () {
		isDefaultCancelled = true;
	}

	public boolean isDefaultCancelled () {
		return isDefaultCancelled;
	}

	@Override
	public boolean set () {
		return false;
	}
}
