package com.github.leawind.thirdperson.impl.model;

import com.github.leawind.thirdperson.api.model.Actor;
import com.github.leawind.thirdperson.utils.math.Vectors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3d;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record ActorImpl(Entity entity) implements Actor.Mutable {
  public static final Logger LOGGER = LoggerFactory.getLogger(ActorImpl.class);

  public ActorImpl(@NonNull Entity entity) {
    this.entity = entity;
  }

  @Override
  public Vector3d getEyePosition(float partialTick) {
    return Vectors.toVector3d(entity.getEyePosition(partialTick));
  }

  @Override
  public Vector3d getPosition(float partialTick) {
    return Vectors.toVector3d(entity.getPosition(partialTick));
  }

  @Override
  public Vector3d getViewVector(float partialTick) {
    return Vectors.toVector3d(entity.getViewVector(partialTick));
  }

  @Override
  public AABB getBoundingBox(float partialTick) {
    var root = entity.getRootVehicle();
    return root.getPassengersAndSelf()
        .map(Entity::getBoundingBox)
        .reduce(AABB::minmax)
        .orElse(root.getBoundingBox());
  }

  @Override
  public void setRotation(float yRot, float xRot) {
    if (!Double.isFinite(xRot) || !Double.isFinite(yRot)) {
      return;
    }
    entity.setYRot(entity.yRotO = yRot);
    entity.setXRot(entity.xRotO = xRot);
  }
}
