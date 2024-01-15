package net.leawind.mc.math.smoothvalue;


import com.mojang.blaze3d.Blaze3D;

import java.util.function.Function;

@SuppressWarnings("unused")
public class FuncSmoothDouble extends FuncSmoothValue<Double> {
	public FuncSmoothDouble (Function<Double, Double> func) {
		super(func);
		value       = 0d;
		startValue  = 0d;
		targetValue = 0d;
	}

	public void setStartValue (double startValue) {
		this.startValue = startValue;
	}

	public void start (double startValue) {
		start(startValue, Blaze3D.getTime());
	}

	public void start (double startValue, double startTime) {
		this.startValue = startValue;
		this.startTime  = startTime;
	}

	public void setTarget (double target) {
		targetValue = target;
	}

	@Override
	public void update (double now) {
		value = func.apply((now - startTime) / duration) * (targetValue - startValue) + startValue;
	}
}
