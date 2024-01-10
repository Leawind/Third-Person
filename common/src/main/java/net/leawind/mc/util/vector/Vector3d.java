package net.leawind.mc.util.vector;


public class Vector3d {
	public double x;
	public double y;
	public double z;

	public Vector3d () {
		this(0);
	}

	public Vector3d (double d) {
		this(d, d, d);
	}

	public Vector3d (Vector3d v) {
		this(v.x, v.y, v.z);
	}

	public Vector3d (double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3d set (Vector3d v) {
		return set(v.x, v.y, v.z);
	}

	public Vector3d set (double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public Vector3d add (Vector3d v) {
		return add(v.x, v.y, v.z);
	}

	public Vector3d add (Vector3d v, Vector3d dest) {
		dest.x = x + v.x;
		dest.y = y + v.y;
		dest.z = z + v.z;
		return dest;
	}

	public Vector3d add (double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Vector3d sub (Vector3d v) {
		return sub(v.x, v.y, v.z);
	}

	public Vector3d sub (double x, double y, double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public Vector3d mul (Vector3d v) {
		return mul(v.x, v.y, v.z);
	}

	public Vector3d mul (double d) {
		return mul(d, d, d);
	}

	public Vector3d mul (double x, double y, double z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}

	public Vector3d div (Vector3d v) {
		return div(v.x, v.y, v.z);
	}

	public Vector3d div (double d) {
		return div(d, d, d);
	}

	public Vector3d div (double x, double y, double z) {
		this.x /= x;
		this.y /= y;
		this.z /= z;
		return this;
	}

	public double length () {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public double lengthSquared () {
		return x * x + y * y + z * z;
	}

	public double distance (Vector3d v) {
		return distance(v.x, v.y, v.z);
	}

	public double distance (double x, double y, double z) {
		double dx = this.x - x;
		double dy = this.y - y;
		double dz = this.z - z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public double distanceSquared (double x, double y, double z) {
		double dx = this.x - x;
		double dy = this.y - y;
		double dz = this.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

	public Vector3d normalize () {
		double len = length();
		x /= len;
		y /= len;
		z /= len;
		return this;
	}

	public Vector3d normalize (double length) {
		double len = length() / length;
		x /= len;
		y /= len;
		z /= len;
		return this;
	}

	public Vector3d normalizeSafely () {
		double len = length();
		if (len == 0) {
			throw new IllegalStateException("Vector length is 0");
		}
		x /= len;
		y /= len;
		z /= len;
		return this;
	}

	public Vector3d normalizeSafely (double length) {
		double len = length() / length;
		if (len == 0) {
			throw new IllegalStateException("Vector length is 0");
		}
		x /= len;
		y /= len;
		z /= len;
		return this;
	}

	public Vector3d zero () {
		x = y = z = 0;
		return this;
	}

	public Vector3d negate () {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}

	public double dot (Vector3d v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public Vector3d lerp (Vector3d v, double t) {
		x += (v.x - x) * t;
		y += (v.y - y) * t;
		z += (v.z - z) * t;
		return this;
	}

	public Vector3d fma (Vector3d a, Vector3d b) {
		x += a.x * b.x;
		y += a.y * b.y;
		z += a.z * b.z;
		return this;
	}

	public Vector3d absolute () {
		x = Math.abs(x);
		y = Math.abs(y);
		z = Math.abs(z);
		return this;
	}

	public int hashCode () {
		final int l = 31;
		int       r = 1;
		long      t;
		t = Double.doubleToLongBits(x);
		r = l * r + (int)(t ^ (t >>> 32));
		t = Double.doubleToLongBits(y);
		r = l * r + (int)(t ^ (t >>> 32));
		t = Double.doubleToLongBits(z);
		r = l * r + (int)(t ^ (t >>> 32));
		return r;
	}

	public boolean equals (Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Vector3d other = (Vector3d)obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) {
			return false;
		}
		return Double.doubleToLongBits(z) == Double.doubleToLongBits(other.z);
	}
}
