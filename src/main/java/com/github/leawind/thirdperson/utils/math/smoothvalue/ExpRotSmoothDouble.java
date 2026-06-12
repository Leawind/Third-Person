package com.github.leawind.thirdperson.utils.math.smoothvalue;

import com.github.leawind.thirdperson.utils.math.Vectors;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("unused")
public class ExpRotSmoothDouble extends ExpSmoothDouble {
  private double cycle;

  /**
   * @param cycle 周期
   */
  public ExpRotSmoothDouble(double cycle) {
    super();
    setCycle(cycle);
  }

  public double getCycle() {
    return cycle;
  }

  public void setCycle(double cycle) {
    this.cycle = cycle;
  }

  @Override
  public void setTarget(double d) {
    super.setTarget(Vectors.floorMod(d, cycle));
  }

  @Override
  public @NonNull Double get(double t) {
    lastValue = Vectors.floorMod(lastValue, cycle);
    value = Vectors.floorMod(value, cycle);
    double delta = Vectors.floorMod(value - lastValue, cycle);
    if (delta > cycle / 2) {
      delta -= cycle;
    }
    value = lastValue + delta;
    return Vectors.lerp(lastValue, value, t);
  }

  @Override
  protected void updateWithOutSavingLastValue(double period) {
    value = Vectors.floorMod(value, cycle);
    target = Vectors.floorMod(target, cycle);
    double delta = Vectors.floorMod(target - value, cycle);
    if (delta > cycle / 2) {
      delta -= cycle;
    }
    target = value + delta;
    value = Vectors.lerp(value, target, 1 - Math.pow(smoothFactor, smoothFactorWeight * period));
  }

  @Override
  public void set(@NonNull Double d) {
    d = Vectors.floorMod(d, cycle);
    super.set(d);
  }

  @Override
  public void setHalflife(double halflife) {
    super.setHalflife(halflife);
  }

  @Override
  public void setValue(double d) {
    d = Vectors.floorMod(d, cycle);
    super.setValue(d);
  }

  public static @NonNull ExpRotSmoothDouble createWithHalflife(double cycle, double halflife) {
    var v = new ExpRotSmoothDouble(cycle);
    v.setHalflife(halflife);
    return v;
  }
}
