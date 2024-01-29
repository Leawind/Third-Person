package net.leawind.mc.util.math.vector.api;


import net.leawind.mc.util.math.vector.impl.Vector2dImpl;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface Vector2d {
	@Contract("-> new")
	static @NotNull Vector2d of () {
		return of(0);
	}

	@Contract("_ -> new")
	static @NotNull Vector2d of (double d) {
		return of(d, d);
	}

	@Contract("_,_ -> new")
	static @NotNull Vector2d of (double x, double y) {
		return new Vector2dImpl(x, y);
	}

	@Contract("_ -> new")
	static @NotNull Vector2d of (@NotNull Vector2d v) {
		return of(v.x(), v.y());
	}

	double x ();

	double y ();

	void x (double x);

	void y (double y);

	@Contract("_ -> this")
	Vector2d set (double d);

	@Contract("_ -> this")
	Vector2d set (@NotNull Vector2d v);

	@Contract("_,_ -> this")
	Vector2d set (double x, double y);

	@Contract("_,_ -> param2")
	Vector2d add (@NotNull Vector2d v, @NotNull Vector2d dest);

	@Contract("_,_,_ -> param3")
	Vector2d add (double x, double y, @NotNull Vector2d dest);

	@Contract("_ -> this")
	Vector2d add (@NotNull Vector2d v);

	@Contract("_ -> this")
	Vector2d add (double d);

	@Contract("_,_ -> this")
	Vector2d add (double x, double y);

	@Contract("_,_ -> param2")
	Vector2d sub (@NotNull Vector2d v, @NotNull Vector2d dest);

	Vector2d sub (double x, double y, @NotNull Vector2d dest);

	@Contract("_ -> this")
	Vector2d sub (@NotNull Vector2d v);

	@Contract("_,_ -> this")
	Vector2d sub (double x, double y);

	Vector2d mul (@NotNull Vector2d v, @NotNull Vector2d dest);

	Vector2d mul (double x, double y, @NotNull Vector2d dest);

	@Contract("_ -> this")
	Vector2d mul (@NotNull Vector2d v);

	@Contract("_ -> this")
	Vector2d mul (double d);

	@Contract("_,_ -> this")
	Vector2d mul (double x, double y);

	Vector2d div (@NotNull Vector2d v, @NotNull Vector2d dest);

	Vector2d div (double x, double y, @NotNull Vector2d dest);

	@Contract("_ -> this")
	Vector2d div (@NotNull Vector2d v);

	@Contract("_,_ -> this")
	Vector2d div (double x, double y);

	Vector2d pow (@NotNull Vector2d v, @NotNull Vector2d dest);

	Vector2d pow (double d, @NotNull Vector2d dest);

	Vector2d pow (double x, double y, @NotNull Vector2d dest);

	@Contract("_ -> this")
	Vector2d pow (@NotNull Vector2d v);

	@Contract("_ -> this")
	Vector2d pow (double d);

	@Contract("_,_ -> this")
	Vector2d pow (double x, double y);

	double length ();

	double lengthSquared ();

	double distance (@NotNull Vector2d v);

	double distance (double x, double y);

	double distanceSquared (double x, double y);

	@Contract("-> this")
	Vector2d normalize ();

	@Contract("_ -> this")
	Vector2d normalize (double length);

	@Contract("-> this")
	Vector2d normalizeSafely ();

	@Contract("_ -> this")
	Vector2d normalizeSafely (double length);

	@Contract("_ -> this")
	Vector2d rotateTo (@NotNull Vector2d direction);

	@Contract("-> this")
	Vector2d zero ();

	@Contract("-> this")
	Vector2d negate ();

	double dot (@NotNull Vector2d v);

	@Contract("_,_ -> this")
	Vector2d clamp (double min, double max);

	@Contract("_,_ -> this")
	Vector2d clamp (@NotNull Vector2d min, @NotNull Vector2d max);

	@Contract("_,_ -> this")
	Vector2d lerp (@NotNull Vector2d end, double t);

	@Contract("_,_ -> this")
	Vector2d lerp (@NotNull Vector2d end, @NotNull Vector2d t);

	@Contract("-> this")
	Vector2d absolute ();

	@Contract("-> new")
	Vector2d copy ();
}
