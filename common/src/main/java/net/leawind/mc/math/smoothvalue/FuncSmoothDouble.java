package net.leawind.mc.math.smoothvalue;


import com.mojang.blaze3d.Blaze3D;

@SuppressWarnings("unused")
public class FuncSmoothDouble extends FuncSmoothValue<Double> {
	public FuncSmoothDouble () {
		value       = 0d;
		startValue  = 0d;
		targetValue = 0d;
	}

	public FuncSmoothDouble setStartValue (double startValue) {
		this.startValue = startValue;
		return this;
	}

	public FuncSmoothDouble start (double startValue) {
		return start(startValue, Blaze3D.getTime());
	}

	public FuncSmoothDouble start (double startValue, double startTime) {
		this.startValue = startValue;
		this.startTime  = startTime;
		return this;
	}

	public FuncSmoothDouble setTarget (double target) {
		targetValue = target;
		return this;
	}

	@Override
	public FuncSmoothDouble update (double now) {
		value = func.apply((now - startTime) / duration) * (targetValue - startValue) + startValue;
		return this;
	}
}
