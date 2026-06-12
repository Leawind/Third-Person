package com.github.leawind.thirdperson.impl.model;

import com.github.leawind.thirdperson.api.model.Viewpoint;
import com.github.leawind.thirdperson.minecraft.bridge.mixin.CameraInvoker;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record ViewpointImpl(@NonNull Camera camera) implements Viewpoint.Mutable {
  public static final Logger LOGGER = LoggerFactory.getLogger(ViewpointImpl.class);

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
    return 0f;
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
    var q = new Quaternionf(rot);
    float yRot =
        (float)
            Math.toDegrees(
                Math.atan2(2 * (q.y * q.w + q.x * q.z), 1 - 2 * (q.y * q.y + q.z * q.z)));
    float xRot = (float) Math.toDegrees(Math.asin(2 * (q.y * q.z + q.w * q.x)));
    setRotation(yRot, xRot);
  }
}
