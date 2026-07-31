package io.github.leawind.thirdperson.internal.logic.base;

import org.joml.Quaternionfc;
import org.joml.Vector3dc;
import org.joml.Vector3fc;

/// Finite-value validation at subsystem boundaries.
public final class FiniteMath {
  private FiniteMath() {}

  public static boolean isFinite(Vector3dc value) {
    return Double.isFinite(value.x())
        && Double.isFinite(value.y())
        && Double.isFinite(value.z());
  }

  public static boolean isFinite(Vector3fc value) {
    return Float.isFinite(value.x())
        && Float.isFinite(value.y())
        && Float.isFinite(value.z());
  }

  public static boolean isFinite(Quaternionfc value) {
    return Float.isFinite(value.x())
        && Float.isFinite(value.y())
        && Float.isFinite(value.z())
        && Float.isFinite(value.w());
  }
}
