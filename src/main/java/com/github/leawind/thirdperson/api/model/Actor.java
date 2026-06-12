package com.github.leawind.thirdperson.api.model;

import net.minecraft.world.phys.AABB;
import org.joml.Vector3d;

public interface Actor {
  Vector3d getEyePosition(float partialTick);

  Vector3d getPosition(float partialTick);

  Vector3d getViewVector(float partialTick);

  AABB getBoundingBox(float partialTick);

  interface Mutable extends Actor {
    void setRotation(float yRot, float xRot);
  }
}
