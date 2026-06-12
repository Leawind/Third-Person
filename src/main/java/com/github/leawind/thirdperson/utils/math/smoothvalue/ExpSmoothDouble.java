package com.github.leawind.thirdperson.utils.math.smoothvalue;

import com.github.leawind.thirdperson.utils.math.Vectors;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public class ExpSmoothDouble extends ExpSmoothValue<Double> {
  public ExpSmoothDouble() {
    super(0D, 1D, 0D, 0D, 0D);
  }

  public void setTarget(double target) {
    this.target = target;
  }

  @Override
  public void setTarget(@NonNull Double target) {
    this.target = target;
  }

  @Override
  public @NonNull Double get(double t) {
    return Vectors.lerp(lastValue, value, t);
  }

  @Override
  protected void saveLastValue() {
    lastValue = value;
  }

  @Override
  protected void updateWithOutSavingLastValue(double period) {
    value = Vectors.lerp(value, target, 1 - Math.pow(smoothFactor, smoothFactorWeight * period));
  }

  @Override
  public void setValue(@NonNull Double d) {
    value = d;
  }

  @Override
  public void set(@NonNull Double d) {
    value = target = d;
  }

  @Override
  public void setSmoothFactor(@NonNull Double smoothFactor) {
    this.smoothFactor = smoothFactor;
  }

  @Override
  public void setSmoothFactor(double smoothFactor) {
    this.smoothFactor = smoothFactor;
  }

  @Override
  public void setMT(@NonNull Double multiplier, @NonNull Double time) {
    if (multiplier < 0 || multiplier > 1) {
      throw new IllegalArgumentException("Multiplier should in [0,1]: " + multiplier);
    } else if (time < 0) {
      throw new IllegalArgumentException("Invalid time, non-negative required, but got " + time);
    }
    setSmoothFactor(time == 0 ? 0 : Math.pow(multiplier, 1 / time));
  }

  @Override
  public void setHalflife(@NonNull Double halflife) {
    setMT(0.5, halflife);
  }

  @Override
  public void setHalflife(double halflife) {
    setMT(0.5, halflife);
  }

  public void setSmoothFactorWeight(double weight) {
    this.smoothFactorWeight = weight;
  }

  public void setValue(double d) {
    value = d;
  }

  public static @NonNull ExpSmoothDouble createWithHalflife(double halflife) {
    var v = new ExpSmoothDouble();
    v.setHalflife(halflife);
    return v;
  }
}
