package net.leawind.mc.util.api.math.vector;


import net.leawind.mc.util.impl.math.vector.Vector3dImpl;

public interface Vector3d {
	static Vector3d of () {
		return of(0);
	}

	static Vector3d of (double d) {
		return of(d, d, d);
	}

	static Vector3d of (double x, double y, double z) {
		return new Vector3dImpl(x, y, z);
	}

	static Vector3d of (Vector3d v) {
		return of(v.x(), v.y(), v.z());
	}

	double x ();

	double y ();

	double z ();

	Vector3d set (double d);

	Vector3d set (double x, double y, double z);

	Vector3d set (Vector3d v);

	Vector3d add (Vector3d v, Vector3d dest);

	Vector3d add (double x, double y, double z, Vector3d dest);

	Vector3d add (Vector3d v);

	Vector3d add (double x, double y, double z);

	Vector3d add (double d);

	Vector3d sub (Vector3d v, Vector3d dest);

	Vector3d sub (double x, double y, double z, Vector3d dest);

	Vector3d sub (Vector3d v);

	Vector3d sub (double x, double y, double z);

	Vector3d mul (Vector3d v, Vector3d dest);

	Vector3d mul (double x, double y, double z, Vector3d dest);

	Vector3d mul (Vector3d v);

	Vector3d mul (double x, double y, double z);

	Vector3d mul (double d);

	Vector3d div (Vector3d v, Vector3d dest);

	Vector3d div (double x, double y, double z, Vector3d dest);

	Vector3d div (Vector3d v);

	Vector3d div (double x, double y, double z);

	Vector3d div (double d);

	Vector3d pow (Vector3d v, Vector3d dest);

	Vector3d pow (double x, double y, double z, Vector3d dest);

	Vector3d pow (double d, Vector3d dest);

	Vector3d pow (Vector3d v);

	Vector3d pow (double x, double y, double z);

	Vector3d pow (double d);

	double lengthSquared ();

	double distance (Vector3d v);

	double distance (double x, double y, double z);

	double distanceSquared (double x, double y, double z);

	Vector3d normalize ();

	double length ();

	Vector3d normalizeSafely ();

	Vector3d normalizeSafely (double length);

	Vector3d rotateTo (Vector3d direction);

	Vector3d normalize (double length);

	Vector3d zero ();

	Vector3d negate ();

	double dot (Vector3d v);

	Vector3d clamp (double min, double max);

	Vector3d clamp (Vector3d min, Vector3d max);

	Vector3d lerp (Vector3d dst, double t);

	Vector3d lerp (Vector3d dst, Vector3d t);

	Vector3d absolute ();

	Vector3d copy ();

	void x (double x);

	void y (double y);

	void z (double z);
}
