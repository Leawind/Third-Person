package net.leawind.mc.api.client.events;


import net.leawind.mc.api.base.ModEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public final class ThirdPersonCameraSetupEvent implements ModEvent {
	public final BlockGetter level;
	public final Entity      attachedEntity;
	public final boolean     detached;
	public final boolean     reversedView;
	public final float       partialTick;

	public ThirdPersonCameraSetupEvent (BlockGetter level, Entity attachedEntity, boolean detached, boolean reversedView, float partialTick) {
		this.level          = level;
		this.attachedEntity = attachedEntity;
		this.detached       = detached;
		this.reversedView   = reversedView;
		this.partialTick    = partialTick;
	}

	public Vec3  pos;
	public float xRot = 0;
	public float yRot = 0;

	/**
	 * Set camera position
	 */
	public void setPosition (Vec3 pos) {
		this.pos = pos;
	}

	/**
	 * Set camera rotation
	 */
	public void setRotation (float xRot, float yRot) {
		this.xRot = xRot;
		this.yRot = yRot;
	}

	public boolean set () {
		return pos != null;
	}
}
