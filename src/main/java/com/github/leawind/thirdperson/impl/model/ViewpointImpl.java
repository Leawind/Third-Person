package com.github.leawind.thirdperson.impl.model;

import com.github.leawind.thirdperson.api.model.Viewpoint;
import com.github.leawind.thirdperson.minecraft.bridge.mixin.CameraInvoker;
import java.util.Objects;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3fc;

public record ViewpointImpl(Camera camera) implements Viewpoint.Mutable {
  public ViewpointImpl(Camera camera) {
    this.camera = Objects.requireNonNull(camera);
  }

  @Override
  public Vector3d getPosition() {
    return new Vector3d(camera.position().x, camera.position().y, camera.position().z);
  }

  @Override
  public Quaternionf getRotation() {
    return camera.rotation();
  }

  @Override
  public float getXRot() {
    return camera.xRot();
  }

  @Override
  public float getYRot() {
    return camera.yRot();
  }

  @Override
  public float getZRot() {
    // TODO
    throw new UnsupportedOperationException();
  }

  @Override
  public Vector3fc forwardVector() {
    return camera.forwardVector();
  }

  @Override
  public Vector3fc upVector() {
    return camera.upVector();
  }

  @Override
  public Vector3fc leftVector() {
    return camera.leftVector();
  }

  @Override
  public void setPosotion(double x, double y, double z) {
    ((CameraInvoker) camera).invokeSetPosition(x, y, z);
  }

  @Override
  public void setPosotion(Vec3 pos) {
    ((CameraInvoker) camera).invokeSetPosition(pos);
  }

  @Override
  public void setRotation(float yRot, float xRot) {
    ((CameraInvoker) camera).invokeSetRotation(yRot, xRot);
  }

  @Override
  public void setRotation(Quaternionfc rot) {
    // TODO
    throw new UnsupportedOperationException();
  }
}
