package com.github.leawind.thirdperson.minecraft.bridge.events.context;

import net.minecraft.world.entity.Entity;
import org.joml.Vector3d;

public class PickBlockContext {
  public Vector3d from;
  public Vector3d to;

  public final Entity entity;
  public final double pickRange;
  public final float partialTick;

  public PickBlockContext(
      Vector3d from, Vector3d to, Entity entity, double pickRange, float partialTick) {
    this.from = from;
    this.to = to;
    this.entity = entity;

    this.pickRange = pickRange;
    this.partialTick = partialTick;
  }
}
