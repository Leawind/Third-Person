package com.github.leawind.thirdperson.core;

import com.github.leawind.thirdperson.utils.math.Vectors;
import java.util.Comparator;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

/// 在预测玩家想击中的目标实体时，根据其位置比较两个实体的优先级
public final class AimingTargetComparator implements Comparator<Vec3> {
  private Vec3 pos;
  private Vector3d viewVector;

  public AimingTargetComparator(Vec3 pos, Vector3d viewVector) {
    this.pos = pos;
    this.viewVector = viewVector;
  }

  public void setPos(Vec3 pos) {
    this.pos = pos;
  }

  public void setViewVector(Vector3d viewVector) {
    this.viewVector = viewVector;
  }

  @Override
  public int compare(Vec3 a, Vec3 b) {
    return (int) Math.signum(getCost(a) - getCost(b));
  }

  private double getCost(Vec3 entityPos) {
    var vectorToTarget = Vectors.toVector3d(entityPos.subtract(pos));
    if (vectorToTarget.length() < 1e-5) {
      return 0;
    }
    vectorToTarget.normalize();
    double dist = pos.distanceTo(entityPos);
    double angrad = Math.acos(viewVector.dot(vectorToTarget));
    double angdeg = Math.toDegrees(angrad);
    return Math.pow(dist, 2) * Math.pow(angdeg, 2.5);
    //		return dist * 2 + angdeg * 5;
  }
}
