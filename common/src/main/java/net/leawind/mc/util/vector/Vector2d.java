package net.leawind.mc.util.vector;


public class Vector2d {
	public double x;
	public double y;

	public Vector2d () {
		this(0);
	}

	public Vector2d (double d) {
		this(d, d);
	}

	public Vector2d (Vector2d v) {
		this(v.x, v.y);
	}

	public Vector2d (double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2d set (Vector2d v) {
		return set(v.x, v.y);
	}

	public Vector2d set (double x, double y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2d add (Vector2d v) {
		return add(v.x, v.y);
	}

	public Vector2d add (Vector2d v, Vector2d dest) {
		dest.x = x + v.x;
		dest.y = y + v.y;
		return dest;
	}

	public Vector2d add (double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Vector2d sub (Vector2d v) {
		return sub(v.x, v.y);
	}

	public Vector2d sub (double x, double y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	public Vector2d mul (Vector2d v) {
		return mul(v.x, v.y);
	}

	public Vector2d mul (double d) {
		return mul(d, d);
	}

	public Vector2d mul (double x, double y) {
		this.x *= x;
		this.y *= y;
		return this;
	}

	public Vector2d div (Vector2d v) {
		return div(v.x, v.y);
	}

	public Vector2d div (double x, double y) {
		this.x /= x;
		this.y /= y;
		return this;
	}

	public double length () {
		return Math.sqrt(x * x + y * y);
	}

	public double lengthSquared () {
		return x * x + y * y;
	}

	public double distance (Vector2d v) {
		return distance(v.x, v.y);
	}

	public double distance (double x, double y) {
		double dx = this.x - x;
		double dy = this.y - y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public double distanceSquared (double x, double y) {
		double dx = this.x - x;
		double dy = this.y - y;
		return dx * dx + dy * dy;
	}

	public Vector2d normalize () {
		double len = length();
		x /= len;
		y /= len;
		return this;
	}

	public Vector2d normalize (double length) {
		double len = length() / length;
		x /= len;
		y /= len;
		return this;
	}

	public Vector2d normalizeSafely () {
		double len = length();
		if (len == 0) {
			throw new IllegalStateException("Vector length is 0");
		}
		x /= len;
		y /= len;
		return this;
	}

	public Vector2d normalizeSafely (double length) {
		double len = length() / length;
		if (len == 0) {
			throw new IllegalStateException("Vector length is 0");
		}
		x /= len;
		y /= len;
		return this;
	}

	public Vector2d zero () {
		x = y = 0;
		return this;
	}

	public Vector2d negate () {
		x = -x;
		y = -y;
		return this;
	}

	public double dot (Vector2d v) {
		return x * v.x + y * v.y;
	}

	public Vector2d lerp (Vector2d v, double t) {
		x += (v.x - x) * t;
		y += (v.y - y) * t;
		return this;
	}

	public Vector2d fma (Vector2d a, Vector2d b) {
		x += a.x * b.x;
		y += a.y * b.y;
		return this;
	}

	public Vector2d absolute () {
		x = Math.abs(x);
		y = Math.abs(y);
		return this;
	}

	public int hashCode () {
		int  l = 31, r = 1;
		long t;
		t = Double.doubleToLongBits(x);
		r = l * r + (int)(t ^ (t >>> 32));
		t = Double.doubleToLongBits(y);
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
		Vector2d other = (Vector2d)obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		return Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y);
	}
}
