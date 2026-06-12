package com.github.leawind.thirdperson.utils.math.smoothvalue;

import com.github.leawind.thirdperson.utils.Vecs;
import org.jetbrains.annotations.NotNull;

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
    super.setTarget(Vecs.floorMod(d, cycle));
  }

  @Override
  public @NotNull Double get(double t) {
    lastValue = Vecs.floorMod(lastValue, cycle);
    value = Vecs.floorMod(value, cycle);
    double delta = Vecs.floorMod(value - lastValue, cycle);
    if (delta > cycle / 2) {
      delta -= cycle;
    }
    value = lastValue + delta;
    return Vecs.lerp(lastValue, value, t);
  }

  @Override
  protected void updateWithOutSavingLastValue(double period) {
    value = Vecs.floorMod(value, cycle);
    target = Vecs.floorMod(target, cycle);
    double delta = Vecs.floorMod(target - value, cycle);
    if (delta > cycle / 2) {
      delta -= cycle;
    }
    target = value + delta;
    value = Vecs.lerp(value, target, 1 - Math.pow(smoothFactor, smoothFactorWeight * period));
  }

  @Override
  public void set(@NotNull Double d) {
    d = Vecs.floorMod(d, cycle);
    super.set(d);
  }

  @Override
  public void setHalflife(double halflife) {
    super.setHalflife(halflife);
  }

  @Override
  public void setValue(double d) {
    d = Vecs.floorMod(d, cycle);
    super.setValue(d);
  }

  public static @NotNull ExpRotSmoothDouble createWithHalflife(double cycle, double halflife) {
    var v = new ExpRotSmoothDouble(cycle);
    v.setHalflife(halflife);
    return v;
  }
}
