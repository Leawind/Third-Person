package com.github.leawind.thirdperson.minecraft.bridge.events.context;

import com.github.leawind.thirdperson.utils.Vecs;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;

public class CameraSetupContext {
  public final Level level;
  public final Entity entity;
  public final boolean detached;
  public final float partialTicks;

  public Vector3d pos;
  public float yRot;
  public float xRot;

  public CameraSetupContext(
      Level level, Entity entity, boolean detached, float partialTicks, Camera camera) {
    this.level = level;
    this.entity = entity;
    this.detached = detached;
    this.partialTicks = partialTicks;

    pos = Vecs.toVector3d(camera.position());
    yRot = camera.yRot();
    xRot = camera.xRot();
  }
}
