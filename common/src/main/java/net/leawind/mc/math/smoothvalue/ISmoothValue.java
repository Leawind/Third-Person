package net.leawind.mc.math.smoothvalue;


@SuppressWarnings("unused")
public interface ISmoothValue<T> {
	void setTarget (T target);

	void update (double time);

	T get ();
}
