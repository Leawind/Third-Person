package net.leawind.mc.api.client.events;


import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public final class CameraSetupEvent {
	public       boolean     set = false;
	public final BlockGetter level;
	public final Entity      attachedEntity;
	public final boolean     detached;
	public final boolean     reversedView;
	public final float       partialTick;

	public CameraSetupEvent (BlockGetter level, Entity attachedEntity, boolean detached, boolean reversedView, float partialTick) {
		this.level          = level;
		this.attachedEntity = attachedEntity;
		this.detached       = detached;
		this.reversedView   = reversedView;
		this.partialTick    = partialTick;
	}

	public Vec3  pos;
	public float xRot;
	public float yRot;

	/**
	 * Set camera position
	 */
	public void setPosition (Vec3 pos) {
		set      = true;
		this.pos = pos;
	}

	/**
	 * Set camera rotation
	 */
	public void setRotation (float xRot, float yRot) {
		set       = true;
		this.xRot = xRot;
		this.yRot = yRot;
	}
}
