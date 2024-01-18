package net.leawind.mc.util.api.math.vector;


import net.leawind.mc.util.impl.math.vector.Vector2dImpl;

public interface Vector2d {
	static Vector2d of () {
		return of(0);
	}

	static Vector2d of (double d) {
		return of(d, d);
	}

	static Vector2d of (Vector2d v) {
		return of(v.x(), v.y());
	}

	static Vector2d of (double x, double y) {
		return new Vector2dImpl(x, y);
	}

	double x ();

	double y ();

	Vector2d set (double d);

	Vector2d set (Vector2d v);

	Vector2d set (double x, double y);

	Vector2d add (Vector2d v, Vector2d dest);

	Vector2d add (double x, double y, Vector2d dest);

	Vector2d add (Vector2d v);

	Vector2d add (double d);

	Vector2d add (double x, double y);

	Vector2d sub (Vector2d v, Vector2d dest);

	Vector2d sub (double x, double y, Vector2d dest);

	Vector2d sub (Vector2d v);

	Vector2d sub (double x, double y);

	Vector2d mul (Vector2d v, Vector2d dest);

	Vector2d mul (double x, double y, Vector2d dest);

	Vector2d mul (Vector2d v);

	Vector2d mul (double d);

	Vector2d mul (double x, double y);

	Vector2d div (Vector2d v, Vector2d dest);

	Vector2d div (double x, double y, Vector2d dest);

	Vector2d div (Vector2d v);

	Vector2d div (double x, double y);

	Vector2d pow (Vector2d v, Vector2d dest);

	Vector2d pow (double d, Vector2d dest);

	Vector2d pow (double x, double y, Vector2d dest);

	Vector2d pow (Vector2d v);

	Vector2d pow (double d);

	Vector2d pow (double x, double y);

	double length ();

	double lengthSquared ();

	double distance (Vector2d v);

	double distance (double x, double y);

	double distanceSquared (double x, double y);

	Vector2d normalize ();

	Vector2d normalize (double length);

	Vector2d normalizeSafely ();

	Vector2d normalizeSafely (double length);

	Vector2d rotateTo (Vector2d direction);

	Vector2d zero ();

	Vector2d negate ();

	double dot (Vector2d v);

	Vector2d clamp (double min, double max);

	Vector2d clamp (Vector2d min, Vector2d max);

	Vector2d lerp (Vector2d dst, double t);

	Vector2d lerp (Vector2d dst, Vector2d t);

	Vector2d absolute ();

	Vector2d copy ();

	void x (double x);

	void y (double y);
}
