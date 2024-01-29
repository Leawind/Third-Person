package net.leawind.mc.util.math.vector.api;


import net.leawind.mc.util.math.vector.impl.Vector3dImpl;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface Vector3d {
	@Contract(value=" -> new", pure=true)
	static @NotNull Vector3d of () {
		return of(0);
	}

	@Contract(value="_ -> new", pure=true)
	static @NotNull Vector3d of (double d) {
		return of(d, d, d);
	}

	@Contract(value="_,_,_ -> new", pure=true)
	static @NotNull Vector3d of (double x, double y, double z) {
		return new Vector3dImpl(x, y, z);
	}

	@Contract("_ -> new")
	static @NotNull Vector3d of (@NotNull Vector3d v) {
		return of(v.x(), v.y(), v.z());
	}

	double x ();

	double y ();

	double z ();

	void x (double x);

	void y (double y);

	void z (double z);

	@Contract("_ -> this")
	Vector3d set (double d);

	@Contract("_,_,_ -> this")
	Vector3d set (double x, double y, double z);

	@Contract("_ -> this")
	Vector3d set (@NotNull Vector3d v);

	@Contract("_,_ -> new")
	Vector3d add (@NotNull Vector3d v, @NotNull Vector3d dest);

	@Contract("_,_,_,_ -> param4")
	Vector3d add (double x, double y, double z, @NotNull Vector3d dest);

	@Contract("_ -> this")
	Vector3d add (@NotNull Vector3d v);

	@Contract("_,_,_ -> this")
	Vector3d add (double x, double y, double z);

	@Contract("_ -> this")
	Vector3d add (double d);

	@Contract("_,_ -> new")
	Vector3d sub (@NotNull Vector3d v, @NotNull Vector3d dest);

	@Contract("_,_,_,_ -> new")
	Vector3d sub (double x, double y, double z, @NotNull Vector3d dest);

	@Contract("_ -> this")
	Vector3d sub (@NotNull Vector3d v);

	@Contract("_,_,_ -> this")
	Vector3d sub (double x, double y, double z);

	@Contract("_,_ -> new")
	Vector3d mul (@NotNull Vector3d v, @NotNull Vector3d dest);

	@Contract("_,_,_,_ -> new")
	Vector3d mul (double x, double y, double z, @NotNull Vector3d dest);

	@Contract("_ -> this")
	Vector3d mul (@NotNull Vector3d v);

	@Contract("_,_,_ -> this")
	Vector3d mul (double x, double y, double z);

	@Contract("_ -> this")
	Vector3d mul (double d);

	@Contract("_,_ -> new")
	Vector3d div (@NotNull Vector3d v, @NotNull Vector3d dest);

	@Contract("_,_,_,_ -> new")
	Vector3d div (double x, double y, double z, @NotNull Vector3d dest);

	@Contract("_ -> this")
	Vector3d div (@NotNull Vector3d v);

	@Contract("_,_,_ -> this")
	Vector3d div (double x, double y, double z);

	@Contract("_ -> this")
	Vector3d div (double d);

	@Contract("_,_ -> new")
	Vector3d pow (@NotNull Vector3d v, @NotNull Vector3d dest);

	@Contract("_,_,_,_ -> new")
	Vector3d pow (double x, double y, double z, @NotNull Vector3d dest);

	@Contract("_,_ -> new")
	Vector3d pow (double d, @NotNull Vector3d dest);

	@Contract("_ -> this")
	Vector3d pow (@NotNull Vector3d v);

	@Contract("_,_,_ -> this")
	Vector3d pow (double x, double y, double z);

	@Contract("_ -> this")
	Vector3d pow (double d);

	double lengthSquared ();

	double distance (@NotNull Vector3d v);

	double distance (double x, double y, double z);

	double distanceSquared (double x, double y, double z);

	double length ();

	@Contract("-> this")
	Vector3d normalize ();

	@Contract("-> this")
	Vector3d normalizeSafely ();

	@Contract("_ -> this")
	Vector3d normalizeSafely (double length);

	@Contract("_ -> this")
	Vector3d rotateTo (@NotNull Vector3d direction);

	@Contract("_ -> this")
	Vector3d normalize (double length);

	@Contract("-> this")
	Vector3d zero ();

	@Contract("-> this")
	Vector3d negate ();

	double dot (@NotNull Vector3d v);

	@Contract("_,_ -> this")
	Vector3d clamp (double min, double max);

	@Contract("_,_ -> this")
	Vector3d clamp (@NotNull Vector3d min, @NotNull Vector3d max);

	@Contract("_,_ -> this")
	Vector3d lerp (@NotNull Vector3d end, double t);

	@Contract("_,_ -> this")
	Vector3d lerp (@NotNull Vector3d end, @NotNull Vector3d t);

	@Contract("-> this")
	Vector3d absolute ();

	@Contract("-> new")
	Vector3d copy ();
}
