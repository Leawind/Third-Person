package net.leawind.mc.util.smoothvalue;


@SuppressWarnings("unused")
public class FuncSmoothDouble extends FuncSmoothValue<Double> {
	@Override
	public FuncSmoothValue<Double> update (double now) {
		value = func.apply((now - startTime) / duration) * (targetValue - startValue) + startValue;
		return this;
	}
}
