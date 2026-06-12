package com.github.leawind.thirdperson.impl.cameraoffset;

import com.github.leawind.thirdperson.core.config.Config;
import org.jspecify.annotations.NonNull;
import org.joml.Vector2d;
import org.joml.Vector3d;

/**
 * 相机偏移模式
 *
 * <p>描述相机应该如何偏移
 */
public abstract class AbstractCameraOffsetMode {
  protected final @NonNull Config config;

  public AbstractCameraOffsetMode(@NonNull Config config) {
    this.config = config;
  }

  /** 设置相机在玩家的左边还是右边 */
  public void setSide(boolean isCameraLeftOfPlayer) {
    if (isCameraLeftOfPlayer ^ isCameraLeftOfPlayer()) {
      toNextSide();
      setCentered(false);
    }
  }

  /**
   * 获取偏移量
   *
   * <p>根据当前是居中还是在两侧自动计算偏移量
   */
  public void getOffsetRatio(@NonNull Vector2d v) {
    if (isCentered()) {
      v.set(0, getCenterOffsetRatio());
    } else {
      getSideOffsetRatio(v);
    }
  }

  public Vector2d getOffsetRatio() {
    var v = new Vector2d();
    getOffsetRatio(v);
    return v;
  }

  /** 眼睛平滑半衰期 */
  @NonNull
  public abstract Vector3d getEyeSmoothHalflife();

  /** 距离平滑系数 */
  public abstract double getDistanceSmoothHalflife();

  /** 相机偏移平滑系数 */
  @NonNull
  public abstract Vector2d getOffsetSmoothHalflife();

  /** 相机到玩家的距离限制 */
  public abstract double getDistanceLimit();

  /** 设置相机到玩家的距离限制 */
  public abstract void setDistanceLimit(double distance);

  /** 当前是否居中 */
  public abstract boolean isCentered();

  /** 设置是否居中 */
  public abstract void setCentered(boolean isCentered);

  public abstract boolean isCameraLeftOfPlayer();

  /** 切换到另一边，如果当前居中，则退出居中 */
  public abstract void toNextSide();

  /** 设置当相机位于两侧，而非居中时的偏移量。 */
  public abstract void setSideOffsetRatio(@NonNull Vector2d v);

  /** 获取当相机居中时的，垂直偏移量 */
  public abstract double getCenterOffsetRatio();

  /** 设置当相机居中时的，垂直偏移量 */
  public abstract void setCenterOffsetRatio(double offset);

  /**
   * 获取当相机位于两侧，而非居中时的偏移量。
   *
   * @param v 将取得的数据存入该向量
   * @return 与传入参数是同一个对象
   */
  @NonNull
  public abstract Vector2d getSideOffsetRatio(@NonNull Vector2d v);
}
