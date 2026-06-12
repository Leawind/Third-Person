package com.github.leawind.thirdperson.impl.model;

import com.github.leawind.thirdperson.api.model.Actor;
import com.github.leawind.thirdperson.utils.Vecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3d;

public record ActorImpl(Entity entity) implements Actor.Mutable {

  @Override
  public Vector3d getEyePosition(float partialTick) {
    return Vecs.toVector3d(entity.getEyePosition(partialTick));
  }

  @Override
  public Vector3d getPosition(float partialTick) {
    return Vecs.toVector3d(entity.getPosition(partialTick));
  }

  @Override
  public Vector3d getViewVector(float partialTick) {
    return Vecs.toVector3d(entity.getViewVector(partialTick));
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
