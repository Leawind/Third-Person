package net.leawind.mc.math.smoothvalue;


import net.leawind.mc.math.LMath;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class SmoothDouble {
	private          double                   value;
	private          double                   lastValue;
	private @NotNull Function<Double, Double> func;

	public SmoothDouble (@NotNull Function<Double, Double> func) {
		this.func = func;
	}

	public void setFunc (@NotNull Function<Double, Double> func) {
		this.func = func;
	}

	public double getLast () {
		return lastValue;
	}

	public void setLast (double d) {
		lastValue = d;
	}

	public double getValue () {
		return value;
	}

	public void setValue (double d) {
		value = d;
	}

	public void update (double newValue) {
		lastValue = value;
		value     = newValue;
	}

	public void set (double d) {
		lastValue = value = d;
	}

	public double get (double t) {
		if (t <= 0) {
			return lastValue;
		} else if (t >= 1) {
			return value;
		} else {
			return LMath.lerp(lastValue, value, func.apply(t));
		}
	}
}
