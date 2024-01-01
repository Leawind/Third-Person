package net.leawind.mc.util.smoothvalue;


@SuppressWarnings("unused")
public interface ISmoothValue<T> {
	ISmoothValue<T> setTarget (T target);

	ISmoothValue<T> update (double time);

	T get ();

}
