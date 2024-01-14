package net.leawind.mc.math.vector;


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

	public Vector3d set (double d) {
		return set(d, d, d);
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

	public Vector3d add (Vector3d v, Vector3d dest) {
		return add(v.x, v.y, v.z, dest);
	}

	public Vector3d add (double x, double y, double z, Vector3d dest) {
		dest.x = this.x + x;
		dest.y = this.y + y;
		dest.z = this.z + z;
		return dest;
	}

	public Vector3d add (Vector3d v) {
		return add(v.x, v.y, v.z);
	}

	public Vector3d add (double d) {
		return add(d, d, d);
	}

	public Vector3d add (double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Vector3d sub (Vector3d v, Vector3d dest) {
		return sub(v.x, v.y, v.z, dest);
	}

	public Vector3d sub (double x, double y, double z, Vector3d dest) {
		dest.x = this.x - x;
		dest.y = this.y - y;
		dest.z = this.z - z;
		return dest;
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

	public Vector3d mul (Vector3d v, Vector3d dest) {
		return mul(v.x, v.y, v.z, dest);
	}

	public Vector3d mul (double x, double y, double z, Vector3d dest) {
		dest.x = this.x * x;
		dest.y = this.y * y;
		dest.z = this.z * z;
		return dest;
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

	public Vector3d div (Vector3d v, Vector3d dest) {
		return div(v.x, v.y, v.z, dest);
	}

	public Vector3d div (double x, double y, double z, Vector3d dest) {
		dest.x = this.x / x;
		dest.y = this.y / y;
		dest.z = this.z / z;
		return dest;
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

	public Vector3d pow (Vector3d v, Vector3d dest) {
		return pow(v.x, v.y, v.z, dest);
	}

	public Vector3d pow (double d, Vector3d dest) {
		return pow(d, d, d, dest);
	}

	public Vector3d pow (double x, double y, double z, Vector3d dest) {
		dest.x = Math.pow(this.x, x);
		dest.y = Math.pow(this.y, y);
		dest.z = Math.pow(this.z, z);
		return dest;
	}

	public Vector3d pow (Vector3d v) {
		return pow(v.x, v.y, v.z);
	}

	public Vector3d pow (double d) {
		return pow(d, d, d);
	}

	public Vector3d pow (double x, double y, double z) {
		this.x = Math.pow(this.x, x);
		this.y = Math.pow(this.y, y);
		this.z = Math.pow(this.z, z);
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

	public Vector3d rotateTo (Vector3d direction) {
		return direction.normalize(length());
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

	public Vector3d clamp (double min, double max) {
		x = x < min ? min: Math.min(x, max);
		y = y < min ? min: Math.min(y, max);
		z = z < min ? min: Math.min(z, max);
		return this;
	}

	public Vector3d clamp (Vector3d min, Vector3d max) {
		x = x < min.x ? min.x: Math.min(x, max.x);
		y = y < min.y ? min.y: Math.min(y, max.y);
		z = z < min.z ? min.z: Math.min(z, max.z);
		return this;
	}

	public Vector3d lerp (Vector3d dst, double t) {
		x += (dst.x - x) * t;
		y += (dst.y - y) * t;
		z += (dst.z - z) * t;
		return this;
	}

	public Vector3d lerp (Vector3d dst, Vector3d t) {
		x += (dst.x - x) * t.x;
		y += (dst.y - y) * t.y;
		z += (dst.z - z) * t.z;
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

	public Vector3d copy () {
		return new Vector3d(x, y, z);
	}
}
