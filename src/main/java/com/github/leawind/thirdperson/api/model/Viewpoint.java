package com.github.leawind.thirdperson.api.model;

import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3fc;

/// 代表游戏中用于渲染画面的相机
///
/// 实际相当于 Camera，这里命名为 Viewpoint 是避免命名冲突。
public interface Viewpoint {
  Vector3d getPosition();

  Quaternionf getRotation();

  float getXRot();

  float getYRot();

  float getZRot();

  Vector3fc forwardVector();

  Vector3fc upVector();

  Vector3fc leftVector();

  interface Mutable extends Viewpoint {
    void setPosotion(double x, double y, double z);

    void setPosotion(Vec3 pos);

    void setRotation(float yRot, float xRot);

    void setRotation(Quaternionfc rot);
  }
}
