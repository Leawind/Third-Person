package net.leawind.mc.math.smoothvalue;


@SuppressWarnings("unused")
public interface ISmoothValue<T> {
	ISmoothValue<T> setTarget (T target);

	ISmoothValue<T> update (double time);

	T get ();
}
