package net.leawind.mc.util.smoothvalue;


import com.mojang.blaze3d.Blaze3D;

import java.util.function.Function;

@SuppressWarnings("unused")
public abstract class FuncSmoothValue<T> implements ISmoothValue<T> {
	public    T                        value;
	public    double                   duration;
	public    Function<Double, Double> func = t -> Math.sqrt(t * (2 - t));
	protected double                   startTime;
	protected T                        startValue;
	protected T                        targetValue;

	public FuncSmoothValue<T> setFunc (Function<Double, Double> func) {
		this.func = func;
		return this;
	}

	final public T getValue () {
		return value;
	}

	final public FuncSmoothValue<T> setStartValue (T startValue) {
		this.startValue = startValue;
		return this;
	}

	final public FuncSmoothValue<T> setDuration (double duration) {
		this.duration = duration;
		return this;
	}

	final public FuncSmoothValue<T> start (T startValue, double startTime) {
		this.startValue = startValue;
		this.startTime  = startTime;
		return this;
	}

	final public FuncSmoothValue<T> start (T startValue) {
		return start(startValue, Blaze3D.getTime());
	}

	final public FuncSmoothValue<T> setTarget (T target) {
		targetValue = target;
		return this;
	}

	final public FuncSmoothValue<T> update () {
		return update(Blaze3D.getTime());
	}

	abstract public FuncSmoothValue<T> update (double now);
}
