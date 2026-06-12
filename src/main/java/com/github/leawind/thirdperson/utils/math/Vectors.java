package com.github.leawind.thirdperson.utils.math;

import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3fc;
import org.joml.Vector3ic;

public final class Vectors {
  private Vectors() {}

  /**
   * 将方向转换成角度（弧度制）
   *
   * @param r 方向
   * @return [x=俯仰角, y=偏航角] 当 d 的模为 0 时，返回值将包含 NaN
   */
  public static Vector3d directionFromRotationDegree(Vector2dc r) {
    return directionFromRotationDegree(r.x(), r.y());
  }

  /**
   * 将方向转换成角度（弧度制）
   *
   * @param d 方向
   */
  public static Vector2d rotationRadianFromDirection(Vector3dc d) {
    double length = d.length();
    if (length < 1e-5) {
      return new Vector2d(0, 0);
    }
    d = d.normalize(new Vector3d());
    return new Vector2d(-Math.asin(d.y()), Math.atan2(-d.x(), d.z()));
  }

  public static Vector3d directionFromRotationDegree(double x, double y) {
    double h = Math.cos(-y * 0.017453292519943295 - Math.PI);
    double i = Math.sin(-y * 0.017453292519943295 - Math.PI);

    double j = -Math.cos(-x * 0.017453292519943295);
    double k = Math.sin(-x * 0.017453292519943295);

    return new Vector3d(i * j, k, h * j);
  }

  /**
   * 将一个向量相对原本方向旋转一定弧度
   *
   * @param vec 原向量
   * @param dy 偏航角变化量（弧度制）
   * @param dx 俯仰角变化量（弧度制）
   */
  public static Vector3d rotateRadian(Vector3dc vec, float dy, float dx) {
    return directionFromRotationDegree(rotationRadianFromDirection(vec).add(new Vector2d(dx, dy)))
        .mul(vec.length());
  }

  /**
   * 将一个向量相对原本方向旋转一定弧度
   *
   * @param vec 原向量
   * @param rotation 弧度变化量（弧度制）
   */
  public static Vector3d rotateRadian(Vector3dc vec, Vector2d rotation) {
    return directionFromRotationDegree(rotationRadianFromDirection(vec).add(rotation))
        .mul(vec.length());
  }

  /**
   * 将一个向量相对原本方向旋转一定角度
   *
   * @param vec 原向量
   * @param dy 偏航角变化量（角度制）
   * @param dx 俯仰角变化量（角度制）
   */
  public static Vector3d rotateDegree(Vector3dc vec, double dy, double dx) {
    return directionFromRotationDegree(rotationDegreeFromDirection(vec).add(new Vector2d(dx, dy)))
        .mul(vec.length());
  }

  /**
   * 将方向转换成角度（角度制）
   *
   * @param d 方向
   */
  public static Vector2d rotationDegreeFromDirection(Vector3dc d) {
    double length = d.length();
    if (length < 1e-5) {
      return new Vector2d(0, 0);
    }
    var nd = new Vector3d(d).normalize();
    return new Vector2d(
        (-Math.toDegrees(Math.asin(nd.y))), Math.toDegrees(Math.atan2(-nd.x, nd.z)));
  }

  /**
   * 将一个向量相对原本方向旋转一定角度
   *
   * @param vec 原向量
   * @param rotationAngle 角度变化量（角度制）
   */
  public static Vector3d rotateDegree(Vector3dc vec, Vector2dc rotationAngle) {
    return directionFromRotationDegree(rotationDegreeFromDirection(vec).add(rotationAngle))
        .mul(vec.length());
  }

  /**
   * 将方向转换成角度（角度制）
   *
   * @param d 方向
   * @return [x=俯仰角, y=偏航角] 当 d 的模为 0 时，返回值将包含 NaN
   */
  public static double rotationDegreeFromDirection(Vector2dc d) {
    return -Math.toDegrees(Math.atan2(d.x(), d.y()));
  }

  public static Vector2d directionFromRotationDegree(double yRot) {
    double x = Math.sin(yRot * 0.017453292519943295 + Math.PI);
    double z = -Math.cos(yRot * 0.017453292519943295 + Math.PI);
    return new Vector2d(x, z);
  }

  public static Vector3d toVector3d(Vec3 v) {
    return new Vector3d(v.x, v.y, v.z);
  }

  public static Vector3d toVector3d(Vector3fc v) {
    return new Vector3d(v.x(), v.y(), v.z());
  }

  public static Vec3i toVec3i(Vec3 v) {
    return new Vec3i((int) v.x, (int) v.y, (int) v.z);
  }

  public static Vec3i toVec3i(Vector3ic v) {
    return new Vec3i(v.x(), v.y(), v.z());
  }

  public static Vec3 toVec3(Vector3dc v) {
    return new Vec3(v.x(), v.y(), v.z());
  }

  @Contract("_, _ -> param1")
  public static @NonNull Vector2d pow(Vector2d v, Vector2dc p) {
    v.x = Math.pow(v.x, p.x());
    v.y = Math.pow(v.y, p.y());
    return v;
  }

  @Contract("_, _ -> param1")
  public static @NonNull Vector3d pow(Vector3d v, Vector3dc p) {
    v.x = Math.pow(v.x, p.x());
    v.y = Math.pow(v.y, p.y());
    v.z = Math.pow(v.z, p.z());
    return v;
  }

  public static int clamp(int d, int min, int max) {
    return d < min ? min : Math.min(d, max);
  }

  public static long clamp(long d, long min, long max) {
    return d < min ? min : Math.min(d, max);
  }

  public static float clamp(float d, float min, float max) {
    return d < min ? min : Math.min(d, max);
  }

  public static double clamp(double d, double min, double max) {
    return d < min ? min : Math.min(d, max);
  }

  public static void clamp(Vector2d v, double min, double max) {
    v.x = clamp(v.x, min, max);
    v.y = clamp(v.y, min, max);
  }

  /** start = start + (end - start) * t */
  public static double lerp(double start, double end, double t) {
    return start + t * (end - start);
  }

  /** start = start + (end - start) * t */
  public static void lerp(Vector2d start, Vector2dc end, Vector2dc t) {
    start.x = lerp(start.x, end.x(), t.x());
    start.y = lerp(start.y, end.y(), t.y());
  }

  /** start = start + (end - start) * t */
  public static void lerp(Vector3d start, Vector3dc end, Vector3dc t) {
    start.x = lerp(start.x, end.x(), t.x());
    start.y = lerp(start.y, end.y(), t.y());
    start.z = lerp(start.z, end.z(), t.z());
  }

  public static double floorMod(double x, double y) {
    return ((x % y) + y) % y;
  }

  public static float floorMod(float x, float y) {
    return ((x % y) + y) % y;
  }

  public static int floorMod(int x, int y) {
    return Math.floorMod(x, y);
  }

  public static long floorMod(long x, long y) {
    return Math.floorMod(x, y);
  }

  /** 角度a是否在角度x与y的夹角中 */
  public static boolean isWithinRadian(double x, double a, double b) {
    double tp = Math.sin(b - a);
    return Math.sin(b - x) * tp > 0 && Math.sin(x - a) * tp > 0;
  }

  /** 两角度的夹角大小 */
  public static double subtractRadian(double a, double b) {
    double x = Math.abs(Math.IEEEremainder(a - b, 6.283185307179586));
    return x > 3.141592653589793 ? 6.283185307179586 - x : x;
  }

  public static boolean isWithinDegrees(double x, double a, double b) {
    return isWithinRadian(
        x * 0.017453292519943295, a * 0.017453292519943295, b * 0.017453292519943295);
  }

  public static double subtractDegrees(double a, double b) {
    double x = Math.abs(Math.IEEEremainder(a - b, 360));
    return x > 180 ? 360 - x : x;
  }
}
