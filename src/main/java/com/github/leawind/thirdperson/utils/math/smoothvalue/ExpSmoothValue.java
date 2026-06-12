package com.github.leawind.thirdperson.utils.math.smoothvalue;

import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public abstract class ExpSmoothValue<T> implements ISmoothValue<T> {
  /** 平滑系数，越大越平滑 */
  public @NonNull T smoothFactor;

  /** 平滑系数乘数，默认应为1 */
  public @NonNull T smoothFactorWeight; // factor ^ weight

  /** 目标值 */
  public @NonNull T target;

  /** 当前平滑的值 */
  protected @NonNull T value;

  /** 上次更新时的目标值 */
  protected @NonNull T lastValue;

  protected ExpSmoothValue(
      @NonNull T smoothFactor,
      @NonNull T smoothFactorWeight,
      @NonNull T value,
      @NonNull T lastValue,
      @NonNull T target) {
    this.smoothFactor = smoothFactor;
    this.smoothFactorWeight = smoothFactorWeight;

    this.value = value;
    this.lastValue = lastValue;
    this.target = target;
  }

  public T getRawValue() {
    return value;
  }

  public T getRawTarget() {
    return target;
  }

  public T getRawLastValue() {
    return lastValue;
  }

  public final void update(double period) {
    saveLastValue();
    updateWithOutSavingLastValue(period);
  }

  @Override
  public final @NonNull T get() {
    return value;
  }

  @Override
  public abstract @NonNull T get(double t);

  @Override
  public final @NonNull T getLast() {
    return lastValue;
  }

  /**
   * 将当前的平滑值 value 存储在 lastValue 中
   *
   * <p>在 update 方法中写入新值前被调用
   */
  protected abstract void saveLastValue();

  protected abstract void updateWithOutSavingLastValue(double period);

  public abstract void setValue(@NonNull T value);

  /**
   * 同时设置目标值和当前平滑值
   *
   * <p>不改变旧值
   */
  public abstract void set(@NonNull T value);

  public abstract void setSmoothFactor(@NonNull T smoothFactor);

  abstract void setSmoothFactor(double smoothFactor);

  /**
   * 根据以下规则设置平滑系数：
   *
   * <p>每隔 time 秒，value 变为原来的 multiplier 倍。
   */
  abstract void setMT(@NonNull T multiplier, @NonNull T time);

  /** 根据半衰期设置平滑系数 */
  abstract void setHalflife(@NonNull T halflife);

  /** 根据半衰期设置平滑系数 */
  abstract void setHalflife(double halflife);
}
